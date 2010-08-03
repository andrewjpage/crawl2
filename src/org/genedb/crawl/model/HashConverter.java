package org.genedb.crawl.model;


import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class HashConverter implements Converter {
	
	private Logger logger = Logger.getLogger(HashConverter.class);
	
	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		
		Hashtable ht = (Hashtable) obj;
		Enumeration keys = ht.keys();
		
		while (keys.hasMoreElements()) {
			
			String key = keys.nextElement().toString();
			logger.debug(key);
			Object value = ht.get(key);
			
			if (value instanceof List) {
				
				for (Object v : (List) value) {
					
					writer.startNode(key);
					context.convertAnother(v);
					writer.endNode();
					
				}
				
			} else {
				
				writer.startNode(key);
				writer.setValue(value.toString());
				writer.endNode();
				
			}
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canConvert(Class cls) {
		return cls.equals(Hashtable.class);
	}

}
