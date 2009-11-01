package com.appspot.ajnweb.service;

import java.util.ArrayList;
import java.util.List;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import appengine.util.MemcacheUtil;

import com.appspot.ajnweb.meta.TweetMeta;
import com.appspot.ajnweb.model.Tweet;
import com.google.appengine.api.datastore.Key;

/**
 * {@link Tweet}に対する操作を行うユーティリティ。
 * @author shin1ogawa
 */
public class TweetService {

	final static String MEMCACHEKEY_RECENT = TwitterQueryService.class.getName() + ":recent";


	private TweetService() {
	}

	/**
	 * StatusのIDの一覧からStatusを取得し、Tweetとして保存する。
	 * @param statusIds
	 * @throws TwitterException 
	 */
	public static void fetchAndSave(List<Long> statusIds) throws TwitterException {
		Twitter twitter = new Twitter();
		List<Tweet> tweets = new ArrayList<Tweet>(statusIds.size());
		TweetMeta meta = new TweetMeta();
		for (Long id : statusIds) {
			Key key = Datastore.createKey(meta, id);
			try {
				Datastore.get(key);
			} catch (EntityNotFoundRuntimeException e) {
				Status showStatus = twitter.showStatus(id);
				tweets.add(Tweet.newInstance(showStatus));
			}
		}
		if (tweets.isEmpty() == false) {
			Datastore.put(tweets);
			MemcacheUtil.delete(MEMCACHEKEY_RECENT);
		}
	}

	/**
	 * @return 最近保存された{@link Tweet}のリスト
	 */
	public static List<Tweet> getRecent() {
		@SuppressWarnings("unchecked")
		List<Tweet> list = MemcacheUtil.get(MEMCACHEKEY_RECENT, List.class);
		if (list != null) {
			return list;
		}
		TweetMeta meta = new TweetMeta();
		list = Datastore.query(meta).sort(meta.createdAt.desc).limit(1000).asList();
		MemcacheUtil.put(MEMCACHEKEY_RECENT, list);
		return list;
	}
}
