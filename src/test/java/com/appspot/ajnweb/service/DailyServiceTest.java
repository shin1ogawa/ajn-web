package com.appspot.ajnweb.service;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slim3.datastore.Datastore;

import twitter4j.TwitterException;
import appengine.test.util.AppEngineTestUtil;

import com.appspot.ajnweb.meta.DailyMeta;
import com.appspot.ajnweb.meta.TweetMeta;
import com.appspot.ajnweb.model.Daily;
import com.appspot.ajnweb.model.Tweet;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * {@link DailyService}のテストケース。
 * @author shin1ogawa
 */
public class DailyServiceTest {

	static final Logger logger = Logger.getLogger(DailyServiceTest.class.getName());

	TweetMeta tweetMeta = new TweetMeta();

	DailyMeta dailyMeta = new DailyMeta();


	/**
	 * {@link DailyService#summary(int, int, int)}, {@link DailyService#getDaily(int, int, int)}
	 */
	@Test
	public void summary() {
		DailyService.summary(2009, 10, 26);
		assertThat(Datastore.query(dailyMeta).count(), is(equalTo(1)));
		Daily daily = DailyService.getDaily(2009, 10, 26);
		assertThat(daily, is(not(nullValue())));
		assertThat(daily.getCount(), is(equalTo(8)));

		DailyService.summary(2009, 10, 27);
		assertThat(Datastore.query(dailyMeta).count(), is(equalTo(2)));
		daily = DailyService.getDaily(2009, 10, 27);
		assertThat(daily, is(not(nullValue())));
		assertThat(daily.getCount(), is(equalTo(2)));

		DailyService.summary(2009, 10, 28);
		assertThat(Datastore.query(dailyMeta).count(), is(equalTo(3)));
		daily = DailyService.getDaily(2009, 10, 28);
		assertThat(daily, is(not(nullValue())));
		assertThat(daily.getCount(), is(equalTo(15)));
	}

	/**
	 * {@link DailyService#getDailyTweets(int, int, int)}
	 * <p>降順にソートされる</p>
	 */
	@Test
	public void getDailyTweets() {
		List<Tweet> tweets = DailyService.getDailyTweets(2009, 10, 26);
		assertThat(tweets.size(), is(equalTo(8)));
		for (int i = 0; i < 8 - 1; i++) {
			assertThat(tweets.get(i).getCreatedAt(), is(greaterThan(tweets.get(i + 1)
				.getCreatedAt())));
		}
	}

	/**
	 * 作成済みのテストデータをテスト用フォルダにコピーし、テスト環境を起動する。
	 * @throws IOException
	 * @throws TwitterException 
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException, TwitterException {
		File testDir = new File("target/serviceTest");
		if (testDir.exists() == false) {
			testDir.mkdirs();
		}
		FileUtils.copyDirectory(new File("src/test/resources/testdata01"), testDir);
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment("ajn-web", "serviceTest"),
						"target/serviceTest", "war", false);
		// 想定通りのデータが読み込めているか確認しておく。
		assertThat(Datastore.query(new TweetMeta()).count(), is(equalTo(30)));
	}


	static LocalServiceTestHelper helper;


	/**
	 * テスト環境を終了する。
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}

	/**
	 * テストデータの作成用。
	 * @throws TwitterException
	 */
	static void createTestData() throws TwitterException {
		// 最近3日間のデータを対象に10件/日のTweetを取得し、保存する。
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -4);
		for (int i = 0; i < 3; i++) {
			Date since = calendar.getTime();
			calendar.add(Calendar.DATE, 1);
			Date until = calendar.getTime();
			String q = "appengine since:" + df.format(since) + " until:" + df.format(until);
			List<twitter4j.Tweet> list = TwitterQueryService.query(q, 10);
			logger.log(Level.INFO, "query=" + q + ", result count=" + list.size());
			List<Long> statusIds = new ArrayList<Long>(list.size());
			for (twitter4j.Tweet tweet : list) {
				statusIds.add(tweet.getId());
			}
			TweetService.fetchAndSave(statusIds);
		}
	}
}
