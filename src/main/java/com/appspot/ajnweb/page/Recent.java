package com.appspot.ajnweb.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.component.TweetDataView;
import com.appspot.ajnweb.model.Tweet;
import com.appspot.ajnweb.service.TweetService;

/**
 * 直近の検索結果を取得して表示するページ。
 * @author shin1ogawa
 */
public class Recent extends WebPage {

	/**
	 * the constructor.
	 * @category constructor
	 */
	public Recent() {
		WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));
		TweetDataView tweetDataView = new TweetDataView("list", createDataProvider(), 25);
		container.add(tweetDataView);
		add(new AppEngineAjaxPagingNavigator("navi", tweetDataView));
	}

	private IDataProvider<Tweet> createDataProvider() {
		return new IDataProvider<Tweet>() {

			private static final long serialVersionUID = -4589123994384616956L;

			transient List<Tweet> list;


			public Iterator<? extends Tweet> iterator(int first, int count) {
				int toIndex = first + count;
				if (toIndex > list.size()) {
					toIndex = list.size();
				}
				return new ArrayList<Tweet>(list.subList(first, toIndex)).iterator();
			}

			public IModel<Tweet> model(Tweet object) {
				return new Model<Tweet>(object);
			}

			public int size() {
				list = TweetService.getRecent();
				return list.size();
			}

			public void detach() {
				list = null;
			}
		};
	}
}
