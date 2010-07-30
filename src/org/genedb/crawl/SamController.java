package org.genedb.crawl;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.genedb.crawl.business.HeirarchyIndex;
import org.genedb.crawl.business.Sam;
import org.genedb.crawl.model.BaseResult;
import org.genedb.crawl.model.FileInfoList;
import org.genedb.crawl.model.FileInfo;
import org.genedb.crawl.model.MappedCoverageItemOld;
import org.genedb.crawl.model.MappedSAMHeader;
import org.genedb.crawl.model.MappedSAMRecord;
import org.genedb.crawl.model.MappedSAMSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import net.sf.samtools.*;


@Controller
@RequestMapping("/sam")
public class SamController {
	
	private Logger logger = Logger.getLogger(SamController.class);
	private HeirarchyIndex heirarchyIndex;
	private Sam sam = new Sam();
	
	@Autowired
	public void setHeirarchyIndex(HeirarchyIndex heirarchyIndex) {
		this.heirarchyIndex = heirarchyIndex;
		sam.heirarchyIndex = heirarchyIndex;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/header", "/header.*"})
	public ModelAndView header(HttpServletRequest request, @RequestParam("fileID") int fileID) {
		ModelAndView mav = new ModelAndView("service:");
		
		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		if (inputSam == null) {
			mav.addObject("model", "Could not find the file " + fileID);
			return  mav;
		}
		
		BaseResult model = new BaseResult();
		MappedSAMHeader mModel = new MappedSAMHeader();
		model.addResult(mModel);
		
		for (Map.Entry<String, Object> entry : inputSam.getFileHeader().getAttributes()) {
			mModel.attributes.put(entry.getKey(), entry.getValue().toString());
		}
		
		mav.addObject("model", model);
		
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/sequences", "/sequences.*"})
	public ModelAndView sequences(HttpServletRequest request, @RequestParam("fileID") int fileID) {
		ModelAndView mav = new ModelAndView("service:");
		
		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		if (inputSam == null) {
			mav.addObject("model", "Could not find the file " + fileID);
			return  mav;
		}
		
		BaseResult model = new BaseResult();
		
		for (SAMSequenceRecord ssr : inputSam.getFileHeader().getSequenceDictionary().getSequences()) {
			MappedSAMSequence mss = new MappedSAMSequence();
			
			mss.length = ssr.getSequenceLength();
			mss.name = ssr.getSequenceName();
			mss.index = ssr.getSequenceIndex();
			
			model.addResult(mss);
			
		}
		
		mav.addObject("model", model);
		
		return mav;
	}
	
	
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/query", "/query.*"})
	public synchronized ModelAndView query(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("contained") boolean contained) {
		
		ModelAndView mav = new ModelAndView("service:");
		
		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		
		if (inputSam == null) {
			mav.addObject("model", "Could not find the file " + fileID);
			return  mav;
		}
		
		BaseResult model = new BaseResult();
		
		
		/**
		 * 
		 * According to the BAMFileReader2 docs:
		 * 
	     * "Prepare to iterate through the SAMRecords in file order.
	     * Only a single iterator on a BAM file can be extant at a time.  If getIterator() or a query method has been called once,
	     * that iterator must be closed before getIterator() can be called again.
	     * A somewhat peculiar aspect of this method is that if the file is not seekable, a second call to
	     * getIterator() begins its iteration where the last one left off.  That is the best that can be
	     * done in that situation."
	     * 
	     * For this reason, we must make sure that we close the iterator at the end of the loop AND make sure that the methods
	     * that use this iterator are iterates is synchronized. 
	     * 
	     */
		
		SAMRecordIterator i = inputSam.query(sequence, start, end, true);
		
		while ( i.hasNext() )  {
			SAMRecord record = i.next();
			MappedSAMRecord m = new MappedSAMRecord(record);
			model.addResult(m);
			logger.info("Adding " + m);
		}
		
		logger.debug("Closing iterator");
		i.close();
		
		mav.addObject("model", model);
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/coverage", "/coverage.*"})
	public synchronized ModelAndView coverage(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("window") int window) throws Exception {
		
		ModelAndView mav = new ModelAndView("service:");
		
		mav.addObject("model", sam.coverage(fileID, sequence, start, end, window));
		return mav;
	}
	
	@RequestMapping(method=RequestMethod.GET, value={"/queryCoverage", "/queryCoverage.*"})
	public synchronized ModelAndView queryCoverage(
			@RequestParam("fileID") int fileID, 
			@RequestParam("sequence") String sequence,
			@RequestParam("start") int start,
			@RequestParam("end") int end,
			@RequestParam("contained") boolean contained,
			@RequestParam("window") final int window) {
		
		ModelAndView mav = new ModelAndView("service:");
		
		final SAMFileReader inputSam = heirarchyIndex.getSamOrBam(fileID);
		
		if (inputSam == null) {
			mav.addObject("model", "Could not find the file " + fileID);
			return  mav;
		}
		
		BaseResult model = new BaseResult();
		
		logger.info("starting");
		
		for (int i = start; i <= end; i=i+window) {
			
			//Interval interval = new Interval(sequence, i, i+window);
			//Coverage coverage = new HsMetricsCalculator.Coverage(interval, 0); 
			
			SAMRecordIterator iter = inputSam.query(sequence, i, i+window, false);
			
			
			MappedCoverageItemOld mappedCoverage = new MappedCoverageItemOld();
			mappedCoverage.start = i;
			mappedCoverage.end = i+ window;
			
			// mappedCoverage.count2 = coverage.getDepths().length;
			
			//			
			//logger.info(i);
			
			
			
			while ( iter.hasNext() )  {
				SAMRecord record = iter.next();
				//logger.info(record);
				mappedCoverage.count++;
			}
			
			iter.close();
//			
			model.addResult(mappedCoverage);
		}
		
		logger.info("ending");
		
		
		mav.addObject("model", model);
		return mav;
	}
	
	
	@RequestMapping(method=RequestMethod.GET, value={"/list", "/list.*"})
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView("service:");
		
		BaseResult model = new BaseResult();
		FileInfoList list= new FileInfoList();
		model.addResult(list);
		
		for (FileInfo file : heirarchyIndex) {
			list.addResult(file);
			logger.info(file);
			System.out.println(file);
		}
		
		mav.addObject("model", model);
		
		return mav;
	}
	
	
}


