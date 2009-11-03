package com.appspot.ajnweb.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.ExternalLink;

import com.appspot.ajnweb.model.Tweet;

/**
 * Tweetへのリンク。
 * @author shin1ogawa
 */
public class TwitterStatusLink extends ExternalLink {

	private static final long serialVersionUID = -7381757783844811556L;

	final Tweet tweet;


	/**
	 * the constructor.
	 * @param id wicket:id
	 * @param tweet {@link Tweet}
	 * @category constructor
	 */
	public TwitterStatusLink(String id, Tweet tweet) {
		super(id, StringUtils.isNotEmpty(tweet.getScreenName()) ? "http://twitter.com/"
				+ tweet.getScreenName() + "/status/" + tweet.getKey().getId() : "#");
		this.tweet = tweet;
	}

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
		df.setTimeZone(TimeZone.getTimeZone("GMT+9"));
		super.replaceComponentTagBody(markupStream, openTag, df.format(tweet.getCreatedAt()));
	}
}
