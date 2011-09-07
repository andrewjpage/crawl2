package org.genedb.crawl.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;

public class Db implements Serializable {
    
    @XmlAttribute
    public String name;
    
    @XmlAttribute
    public String urlprefix;
    
    @XmlAttribute
    public String url;
    
    @XmlAttribute
    public String description;
}
