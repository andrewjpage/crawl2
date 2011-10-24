package org.genedb.crawl.model;

import javax.xml.bind.annotation.XmlAttribute;

public class CvtermRelationship {
    
    @XmlAttribute(required=false)
    public String relationship;
    
    @XmlAttribute
    public String link;
    
}
