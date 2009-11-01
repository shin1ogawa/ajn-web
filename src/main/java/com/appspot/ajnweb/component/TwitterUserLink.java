package com.appspot.ajnweb.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.ExternalLink;

import com.appspot.ajnweb.model.Tweet;

/**
 * Tweetの投稿者のページへのリンク。
 * @author shin1ogawa
 */
public class TwitterUserLink extends ExternalLink {

	private static final long serialVersionUID = 8595894269206739735L;

	final Tweet tweet;


	/**
	 * the constructor.
	 * @param id wicket:id
	 * @param tweet {@link Tweet}
	 * @category constructor
	 */
	public TwitterUserLink(String id, Tweet tweet) {
		super(id, "http://twitter.com/" + tweet.getUserName());
		this.tweet = tweet;
	}

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		super.replaceComponentTagBody(markupStream, openTag, tweet.getUserName());
	}
}
