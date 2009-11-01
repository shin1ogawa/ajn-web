package com.appspot.ajnweb.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.image.Image;

import com.appspot.ajnweb.model.Tweet;

/**
 * Tweetの投稿者の画像。
 * @author shin1ogawa
 */
public class TwitterUserImage extends Image {

	private static final long serialVersionUID = 825106190162725875L;

	final Tweet tweet;


	/**
	 * the constructor.
	 * @param id wicket:id
	 * @param tweet {@link Tweet}
	 * @category constructor
	 */
	public TwitterUserImage(String id, Tweet tweet) {
		super(id);
		this.tweet = tweet;
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		tag.put("src", tweet.getUserProfileImageUrl());
		tag.setName("img");
	}

	@Override
	protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
		openTag.setName("img");
		super.replaceComponentTagBody(markupStream, openTag, "");
	}
}
