package com.appspot.ajnweb.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slim3.datastore.Datastore;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import appengine.test.util.AppEngineTestUtil;
import appengine.util.MakeSyncCallServletDelegate;

import com.appspot.ajnweb.meta.TweetMeta;
import com.appspot.ajnweb.model.Tweet;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * {@link Tweet}に{@code screenName}属性を追加したので、古いデータをリモートバッチ処理で追随させる。
 * <p>古いデータは{@literal schemaVersion == 1}の条件で抽出できる</p>
 * <ol>
 * <li>{@literal schemaVersion == 1}の条件で移行対象のエンティティを取得する。</li>
 * <li>念のため、それらのエンティティのバックアップを保存しておく。</li>
 * <li>{@link Tweet#getUserId()}(Twitterのユーザ)で集約し、Twitterのユーザに対応する{@code screenName}を取得して該当するエンティティを更新する<ul>
 * <li>取得した{@code screenName}を設定</li><li>スキーマバージョンを{@literal 2}に設定</li></ul></li>
 * <li>更新したエンティティをデプロイ環境へ保存する。</li>
 * </ol>
 * @author shin1ogawa
 */
public class MigrationTweet1To2 {

	static final Logger logger = Logger.getLogger(MigrationTweet1To2.class.getName());

	static final String SERVER = "http://2009-11-03c.latest.ajn-web.appspot.com/";

	static final String SERVLET = "sys/makesynccall";

	static String email;

	static String password;


	/**
	 * リモート環境へ直結して{@link Tweet}エンティティの移行処理を行う。
	 * @param args
	 * @throws IOException
	 * @throws TwitterException
	 */
	public static void main(String[] args) throws IOException, TwitterException {
		setUpBeforeClass(); // ローカルのAppEngine環境を開始する
		try {
			// デプロイ環境のMakeSyncCallServletに接続するためのアカウント情報を入力させる
			getAccountInfo();
			// 移行対象のエンティティを取得する
			final List<Tweet> tweets = getOldEntities();
			// ローカルのデータストアにバックアップを作成する
			backupToLocalDatastore(tweets);
			// データ移行済みのエンティティを作成する
			final List<Tweet> updated = createUpdatedEntity(tweets, 50);
			// デプロイ環境側のデータストアに、データ移行済みのエンティティを保存する
			MakeSyncCallServletDelegate.runInDelegateWithAuth(new Runnable() {

				@Override
				public void run() {
					executeBatch(updated);
				}
			}, email, password, SERVER, SERVLET);
		} finally {
			tearDownAfterClass(); // ローカルのAppEngine環境を終了する
		}
	}

	static void executeBatch(final List<Tweet> updated) {
		logger.info("更新処理を開始");
		// 100件ずつまとめて、batch putする。
		List<Tweet> buffer = new ArrayList<Tweet>(100);
		int bufferCount = 0;
		for (Tweet tweet : updated) {
			logger.info(ToStringBuilder.reflectionToString(tweet));
			buffer.add(tweet);
			if (++bufferCount == 100) {
				Datastore.put(buffer);
				buffer.clear();
				bufferCount = 0;
			}
		}
		if (buffer.isEmpty() == false) {
			Datastore.put(buffer);
		}
		logger.info("更新処理が終了");
		MemcacheServiceFactory.getMemcacheService().clearAll();
		logger.info("Memcache#clearAll()しました");
	}

	static List<Tweet> createUpdatedEntity(final List<Tweet> tweets, int limit) {
		logger.info("更新済みエンティティの作成を開始");
		final Set<Integer> users = new HashSet<Integer>();
		final Map<Integer, List<Tweet>> tweetMap = new HashMap<Integer, List<Tweet>>();
		final List<Tweet> updated = new ArrayList<Tweet>();
		for (Tweet tweet : tweets) {
			users.add(tweet.getUserId());
			List<Tweet> list = tweetMap.get(tweet.getUserId());
			if (list == null) {
				list = new ArrayList<Tweet>();
			}
			list.add(tweet);
			tweetMap.put(tweet.getUserId(), list);
		}
		int userCount = 0;
		for (Integer userId : users) {
			Twitter twitter = new TwitterFactory().getInstance();
			User user;
			try {
				user = twitter.showUser(String.valueOf(userId));
			} catch (TwitterException e) {
				logger.warning("Twitter#showUser()でエラーが返ってきたのでユーザ情報の取得を中断します");
				break;
			}
			logger.info("Twitter#showUser: userName=" + user.getName() + ", screenName="
					+ user.getScreenName());
			List<Tweet> list = tweetMap.get(userId);
			for (Tweet tweet : list) {
				tweet.setScreenName(user.getScreenName());
				tweet.setSchemaVersion(2);
				updated.add(tweet);
			}
			if (++userCount >= limit) {
				break;
			}
		}
		logger.info("更新済みエンティティの作成が完了");
		return updated;
	}

	private static void backupToLocalDatastore(List<Tweet> tweets) {
		logger.info("backup開始");
		for (Tweet tweet : tweets) {
			Datastore.put(tweet);
		}
		logger.info("backup完了");
	}

	/**
	 * 移行対象のエンティティを取得する。
	 * <p>Twitter側の制限にひっかかるので、一度に100件ずつくらいを処理対象とする。</p>
	 * @return 移行対象のTweetのリスト
	 * @throws HttpException
	 * @throws IOException
	 */
	private static List<Tweet> getOldEntities() throws HttpException, IOException {
		logger.info("更新対象のエンティティの取得を開始");
		final List<Tweet> tweets = new ArrayList<Tweet>();
		MakeSyncCallServletDelegate.runInDelegateWithAuth(new Runnable() {

			@Override
			public void run() {
				TweetMeta meta = new TweetMeta();
				tweets.addAll(Datastore.query(meta).filter(meta.schemaVersion.equal(1)).sort(
						meta.schemaVersion.desc).sort(meta.createdAt.desc).limit(1000).asList());
			}
		}, email, password, SERVER, SERVLET);
		logger.info("更新対象のエンティティの取得が終了: 件数=" + tweets.size());
		return tweets;
	}

	private static void getAccountInfo() throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("google email:");
		email = input.readLine();
		System.out.println("google password:");
		password = input.readLine();
	}


	static LocalServiceTestHelper helper;


	static void setUpBeforeClass() throws IOException {
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment(),
						"target/MigrationTweet1To2", "war", false);
	}

	static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}
}
