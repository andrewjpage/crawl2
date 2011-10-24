package org.genedb.crawl.controller.editor;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatePropertyEditor extends PropertyEditorSupport {
	
	
	final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void setAsText(String text) {
		
		try {
			Date date = df.parse(text);
			this.setValue(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public String getAsText() {
		Date date = (Date) this.getValue();
		return df.format(date);
	}
}