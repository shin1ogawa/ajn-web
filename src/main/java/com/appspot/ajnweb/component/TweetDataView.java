package com.appspot.ajnweb.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import com.appspot.ajnweb.model.Tweet;

/**
 * Tweetの一覧を表示するための{@link DataView}。
 * <ul>
 * <li>createdAt</li>
 * <li>userName</li>
 * <li>userProfileImageUrl</li>
 * <li>text</li>
 * </ul>
 * @author shin1ogawa
 */
public class TweetDataView extends DataView<Tweet> {

	private static final long serialVersionUID = 1430866670419667912L;


	/**
	 * the constructor.
	 * @param id
	 * @param dataProvider
	 * @param itemsPerPage
	 * @category constructor
	 */
	public TweetDataView(String id, IDataProvider<Tweet> dataProvider, int itemsPerPage) {
		super(id, dataProvider, itemsPerPage);
	}

	@Override
	protected void populateItem(Item<Tweet> item) {
		Tweet tweet = item.getModelObject();
		item.add(new DateLabel("createdAt", tweet.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
		item.add(new TwitterUserLink("userName", tweet));
		item.add(new TwitterUserImage("userProfileImageUrl", tweet));
		item.add(new Label("text", tweet.getText()));
	}
}
