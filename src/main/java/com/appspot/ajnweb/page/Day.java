package com.appspot.ajnweb.page;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.component.TweetDataView;
import com.appspot.ajnweb.model.Tweet;
import com.appspot.ajnweb.service.DailyService;

/**
 * 一日分のTweetを表示する。
 * @author shin1ogawa
 */
public class Day extends WebPage {

	final int year, month, day;


	/**
	 * the constructor.
	 * @param parameters ハイフンで区切られた{@literal yyyy-MM-dd}
	 * @category constructor
	 */
	public Day(PageParameters parameters) {
		String ymdString = parameters.getString("0");
		String[] split = ymdString.split("\\-");
		year = Integer.valueOf(split[0]);
		month = Integer.valueOf(split[1]);
		day = Integer.valueOf(split[2]);

		add(new Label("ymd", ymdString));
		WebMarkupContainer container = new WebMarkupContainer("container");
		add(container.setOutputMarkupId(true));
		TweetDataView tweetDataView = new TweetDataView("list", createDataProvider(), 25);
		container.add(tweetDataView);
		add(new AppEngineAjaxPagingNavigator("navi", tweetDataView));
	}

	private IDataProvider<Tweet> createDataProvider() {
		return new IDataProvider<Tweet>() {

			private static final long serialVersionUID = 4647248321485562057L;

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
				list = DailyService.getDailyTweets(year, month, day);
				return list.size();
			}

			public void detach() {
				list = null;
			}
		};
	}
}
