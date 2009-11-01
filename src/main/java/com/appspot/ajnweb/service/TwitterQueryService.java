package com.appspot.ajnweb.service;

import java.util.List;

import org.slim3.datastore.Datastore;

import twitter4j.Query;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import appengine.util.MemcacheUtil;

import com.appspot.ajnweb.meta.TwitterQueryMeta;
import com.appspot.ajnweb.model.TwitterQuery;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Method.*;

/**
 * {@link TwitterQuery}に対する操作を行うユーティリティ。
 * @author shin1ogawa
 */
public class TwitterQueryService {

	final static String MEMCACHEKEY_ALL = TwitterQueryService.class.getName() + ":all";


	private TwitterQueryService() {
	}

	/**
	 * @return datastoreに保存された{@link TwitterQuery}の全エンティティ
	 */
	public static List<TwitterQuery> getAll() {
		@SuppressWarnings("unchecked")
		List<TwitterQuery> list = MemcacheUtil.get(MEMCACHEKEY_ALL, List.class);
		if (list != null) {
			return list;
		}
		TwitterQueryMeta meta = new TwitterQueryMeta();
		list = Datastore.query(meta).asList();
		MemcacheUtil.put(MEMCACHEKEY_ALL, list);
		return list;
	}

	/**
	 * クエリを保存する。
	 * @param query
	 */
	public static void addQuery(String query) {
		TwitterQuery twitterQuery = new TwitterQuery();
		twitterQuery.setQueryString(query);
		Datastore.put(twitterQuery);
		MemcacheUtil.delete(MEMCACHEKEY_ALL);
	}

	/**
	 * Twitter4Jの検索機能経由で検索を行う。
	 * <p>1page分しか取得していない。
	 * TODO 取得済みの最新IDを覚えておいて、since_idとかで差分を取得すべきだけど面倒なんでマジメにやってない。
	 * @param queryString
	 * @param returnPerPage 1page辺りの件数
	 * @return クエリにHitした最新の100件
	 * @throws TwitterException
	 * @see twitter4j.Twitter#search(Query)
	 */
	public static List<twitter4j.Tweet> query(String queryString, int returnPerPage)
			throws TwitterException {
		Query query = new Query(queryString);
		query.setLang("ja");
		query.setRpp(returnPerPage);
		return new Twitter().search(query).getTweets();
	}

	/**
	 * クエリを実行し、クエリ結果から{@link Tweet}を取得＆保存するTaskを追加する。
	 * <p>Tweetを取得＆保存するTaskには10件ずつStatusIDを渡す。</p>
	 * @param queryString
	 * @throws TwitterException
	 */
	public static void executeQueryAndAddTask(String queryString) throws TwitterException {
		Queue queue = QueueFactory.getQueue("background-processing");
		List<Tweet> list = TwitterQueryService.query(queryString, 30);
		int count = 0;
		TaskOptions options = url("/sys/addTweets").method(GET);
		for (Tweet tweet : list) {
			options.param("id[" + count + "]", String.valueOf(tweet.getId()));
			if (++count == 10) {
				// 10件ごとに分割してStatus取得＆Tweet保存用のTaskをQueueに追加する。
				queue.add(options);
				options = url("/sys/addTweets").method(GET);
				count = 0;
			}
		}
	}
}
