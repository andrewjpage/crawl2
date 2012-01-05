package org.genedb.crawl.controller.editor;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.StringUtils;

public class ListSplittingPropertyEditor extends PropertyEditorSupport {
		
		@Override
		public void setAsText(String text) {
		    
			List<String> list = Arrays.asList(text.split(","));
			
			this.setValue(list);
		}
		
		
		@Override
		public String getAsText() {
			
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) this.getValue();
			String str = StringUtils.arrayToCommaDelimitedString(list.toArray());
			return str;
		}
	}