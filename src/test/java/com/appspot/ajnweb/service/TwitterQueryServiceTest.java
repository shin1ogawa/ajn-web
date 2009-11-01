package com.appspot.ajnweb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelQuery;

import twitter4j.Tweet;
import twitter4j.TwitterException;
import appengine.test.util.AppEngineTestUtil;
import appengine.test.util.TaskQueueTestUtil.HookTaskQueueDelegate;
import appengine.test.util.TaskQueueTestUtil.HookTaskQueueDelegateHandlerAdapter;

import com.appspot.ajnweb.meta.TwitterQueryMeta;
import com.appspot.ajnweb.model.TwitterQuery;
import com.google.appengine.api.labs.taskqueue.TaskQueuePb.TaskQueueAddRequest;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * {@link TwitterQueryService}のテストケース。
 * @author shin1ogawa
 */
public class TwitterQueryServiceTest {

	/**
	 * Datastoreに保存されたクエリ文字列を全件取得する。
	 * {@link TwitterQueryService#getAll()}
	 */
	@Test
	public void getAll() {
		assertThat(TwitterQueryService.getAll().size(), is(equalTo(0)));
		// 2件程追加する。
		TwitterQueryService.addQuery("GAE OR appengine OR datastore OR slim3 OR JDO OR ajn");
		TwitterQueryService.addQuery("higayasuo");
		assertThat(TwitterQueryService.getAll().size(), is(equalTo(2)));
	}

	/**
	 * クエリ文字列をDatastoreに追加する。
	 * {@link TwitterQueryService#addQuery(String)}
	 */
	@Test
	public void addQuery() {
		TwitterQueryMeta meta = new TwitterQueryMeta();
		ModelQuery<TwitterQuery> query = Datastore.query(meta);
		int count = query.count();
		assertThat(count, is(equalTo(0)));
		// 2件程追加する。
		TwitterQueryService.addQuery("GAE OR appengine OR datastore OR slim3 OR JDO OR ajn");
		TwitterQueryService.addQuery("higayasuo");
		count = query.count();
		assertThat(count, is(equalTo(2)));
	}

	/**
	 * クエリを実行する。
	 * <p>実際にクエリをするのでオフラインでは動作しない。</p>
	 * @throws TwitterException
	 */
	@Test
	public void query() throws TwitterException {
		String q = "GAE OR appengine OR datastore OR slim3 OR JDO OR ajn";
		List<Tweet> query = TwitterQueryService.query(q, 10);
		// 何か返ってくるはず。
		assertThat(query.size(), is(greaterThan(0)));
	}

	/**
	 * クエリ＆タスクの追加を実行する。
	 * <p>実際にクエリをするのでオフラインでは動作しない。</p>
	 * @throws TwitterException
	 */
	@Test
	public void executeQueryAndAddTask() throws TwitterException {
		final String q = "GAE OR appengine OR datastore OR slim3 OR JDO OR ajn";
		final List<TaskQueueAddRequest> requests = new ArrayList<TaskQueueAddRequest>();
		HookTaskQueueDelegate.runInDelegate(new Runnable() {

			public void run() {
				try {
					TwitterQueryService.executeQueryAndAddTask(q);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}, new HookTaskQueueDelegateHandlerAdapter() {

			@Override
			public void addRequest(TaskQueueAddRequest requestPb) {
				requests.add(requestPb);
			}
		});
		// QueueにTaskが追加される。
		assertThat(requests.size(), is(greaterThan(0)));
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
