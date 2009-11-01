package com.appspot.ajnweb.webhandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.TwitterException;

import com.appspot.ajnweb.service.TwitterQueryService;

/**
 * クエリを実行してStatusのIDの一覧を取得し、Statusを取得するTaskをQueueに追加する。
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class ExecuteQuery extends HttpServlet {

	final static Logger logger = Logger.getLogger(ExecuteQuery.class.getName());


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			TwitterQueryService.executeQueryAndAddTask(request.getParameter("q"));
			response.setStatus(HttpServletResponse.SC_OK);
		} catch (TwitterException e) {
			logger.log(Level.WARNING, "クエリの実行に失敗しました。", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
