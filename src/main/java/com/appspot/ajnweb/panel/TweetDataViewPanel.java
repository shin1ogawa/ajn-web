package com.appspot.ajnweb.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.IDataProvider;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.component.TweetDataView;
import com.appspot.ajnweb.model.Tweet;

/**
 * @author shin1ogawa
 */
public class TweetDataViewPanel extends Panel {

	private static final long serialVersionUID = 8791527795572099648L;


	/**
	 * the constructor.
	 * @param id
	 * @param dataProvider
	 * @param itemsPerPage 
	 * @category constructor
	 */
	public TweetDataViewPanel(String id, IDataProvider<Tweet> dataProvider, int itemsPerPage) {
		super(id);
		WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));
		TweetDataView tweetDataView = new TweetDataView("list", dataProvider, 25);
		container.add(tweetDataView);
		add(new AppEngineAjaxPagingNavigator("navi", tweetDataView));
	}

}
