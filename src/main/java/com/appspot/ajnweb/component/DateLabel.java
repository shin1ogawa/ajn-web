package com.appspot.ajnweb.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;

/**
 * 日付ラベル。
 * @author shin1ogawa
 */
public class DateLabel extends Label {

	private static final long serialVersionUID = -4960233950835097870L;

	final Date date;

	final String pattern;


	/**
	 * the constructor.
	 * @param id
	 * @param date
	 * @param pattern 
	 * @category constructor
	 */
	public DateLabel(String id, Date date, String pattern) {
		super(id);
		this.date = date;
		this.pattern = pattern;
	}

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		String string = "";
		if (date != null) {
			DateFormat df = new SimpleDateFormat(pattern);
			df.setTimeZone(TimeZone.getTimeZone("GMT+9"));
			string = df.format(date);
		}
		super.replaceComponentTagBody(markupStream, openTag, string);
	}
}
