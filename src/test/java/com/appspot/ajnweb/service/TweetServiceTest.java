package com.appspot.ajnweb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import twitter4j.TwitterException;
import appengine.test.util.AppEngineTestUtil;

import com.appspot.ajnweb.meta.TweetMeta;
import com.appspot.ajnweb.model.Tweet;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * {@link TweetService}のテストケース。
 * @author shin1ogawa
 */
public class TweetServiceTest {

	TweetMeta tweetMeta = new TweetMeta();


	/**
	 * {@link TweetService#fetchAndSave(List)}
	 * <p>実際にクエリをするのでオフラインでは動作しない。</p>
	 * @throws TwitterException
	 */
	@Test
	public void fetchAndSave() throws TwitterException {
		List<twitter4j.Tweet> query = TwitterQueryService.query("appengine", 20);
		// いつ実行してもたぶん10件以上は返ってきてくれるはず。
		assertThat(query.size(), is(greaterThan(10)));
		List<Long> statusIds = new ArrayList<Long>(query.size());
		for (twitter4j.Tweet tweet : query) {
			statusIds.add(tweet.getId());
		}
		TweetService.fetchAndSave(statusIds.subList(0, 5));
		assertThat(Datastore.query(tweetMeta).count(), is(equalTo(5)));
		// 同じstatusIdがfetchされても、statusIdで存在チェックをしているため全体の件数は増えないはず。
		TweetService.fetchAndSave(statusIds.subList(0, 5));
		assertThat(Datastore.query(tweetMeta).count(), is(equalTo(5)));
	}

	/**
	 * {@link TweetService#getRecent()}
	 * <p>実際にクエリをするのでオフラインでは動作しない。</p>
	 * @throws TwitterException 
	 */
	@Test
	public void getRecent() throws TwitterException {
		List<twitter4j.Tweet> query = TwitterQueryService.query("appengine", 20);
		// いつ実行してもたぶん10件以上は返ってきてくれるはず。
		assertThat(query.size(), is(greaterThan(10)));
		List<Long> statusIds = new ArrayList<Long>(query.size());
		for (twitter4j.Tweet tweet : query) {
			statusIds.add(tweet.getId());
		}
		TweetService.fetchAndSave(statusIds.subList(0, 5));
		List<Tweet> recent1 = TweetService.getRecent();
		assertThat(recent1.size(), is(equalTo(5)));
		TweetService.fetchAndSave(statusIds.subList(0, 5)); // Datastore内に変化は無いはず
		List<Tweet> recent2 = TweetService.getRecent();
		assertThat(recent2.size(), is(equalTo(5)));
		TweetService.fetchAndSave(statusIds.subList(5, 10)); //Datastoreに新たなTweetが追加される。
		// memcacheがクリアされて新しい情報に更新されているはず。
		List<Tweet> recent3 = TweetService.getRecent();
		assertThat(recent3.size(), is(equalTo(10)));
	}

	/**
	 * テスト環境を起動する。
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		AppEngineTestUtil.setUpAppEngine(new LocalEnvironment("ajn-web", "serviceTest"),
				"target/serviceTest", "war", true);
	}

	/**
	 * テスト環境を終了する。
	 */
	@After
	public void tearDown() {
		AppEngineTestUtil.tearDownAppEngine();
	}
}
