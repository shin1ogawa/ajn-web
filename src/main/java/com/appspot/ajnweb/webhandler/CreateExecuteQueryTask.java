package com.appspot.ajnweb.webhandler;

import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.ajnweb.model.TwitterQuery;
import com.appspot.ajnweb.service.TwitterQueryService;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Method.*;

/**
 * 保存された{@link TwitterQuery}をすべて読み出し、それらを実行するTaskQueueを投入する。
 * <p>cronで起動される。</p>
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class CreateExecuteQueryTask extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		Queue queue = QueueFactory.getDefaultQueue();
		List<TwitterQuery> all = TwitterQueryService.getAll();
		for (TwitterQuery twitterQuery : all) {
			queue.add(url("/sys/executeQuery").method(GET)
				.param("q", twitterQuery.getQueryString()));
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	public void init(ServletConfig config) {
		if (TwitterQueryService.getAll().isEmpty()) {
			// 最初のデプロイ時にデータを1件だけ投入しておく。
			TwitterQueryService.addQuery("GAE OR appengine OR datastore OR slim3 OR JDO OR ajn");
		}
	}
}
