package org.genedb.crawl.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("response")
public class BaseResult {
	
    public String name;

    @XStreamImplicit()
    private List<BaseResult> results = new ArrayList<BaseResult>();

    public void addResult(BaseResult br)
    {
        results.add(br);
    }
    
    
    
}





