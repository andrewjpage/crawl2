package uk.ac.sanger.artemis.components.variant;

import java.io.IOException;

import org.apache.log4j.Logger;

public class TabixReaderAdapter extends VariantReaderAdapter{
	
	private Logger logger = Logger.getLogger(TabixReaderAdapter.class);
	
	private TabixReader reader;
	
	public TabixReaderAdapter(String url) throws IOException {
		
		reader = new TabixReader(url);
		abstractReader = reader;
		logger.info(String.format("Intantiatging tabix reader %s with url %s ", reader.getName(), reader.getFileName()));
		
		String line;
	    while( (line = reader.readLine() ) != null ) {
	        if(!line.startsWith("##"))
	          break;
	        
	        if(line.indexOf("VCFv4") > -1) {
	        	reader.setVcf_v4(true);
	        	break;
	        }
	    }
	}

	// TODO - tabix readers do not have a close method...
    @Override
    public void close() throws IOException {
        reader = null;
    }
	
	
}