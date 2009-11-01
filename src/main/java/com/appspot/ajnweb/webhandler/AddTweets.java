package com.appspot.ajnweb.webhandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import twitter4j.TwitterException;

import com.appspot.ajnweb.model.Tweet;
import com.appspot.ajnweb.service.TweetService;

/**
 * 取得すべきStatusのIDの一覧を取得し、{@link Tweet}を保存する。
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class AddTweets extends HttpServlet {

	final static Logger logger = Logger.getLogger(AddTweets.class.getName());


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		List<Long> statusIds = new ArrayList<Long>();
		for (int i = 0;; i++) {
			String string = request.getParameter("id[" + i + "]");
			if (StringUtils.isEmpty(string)) {
				break;
			}
			statusIds.add(Long.valueOf(string));
		}
		try {
			TweetService.fetchAndSave(statusIds);
		} catch (TwitterException e) {
			logger.log(Level.WARNING, "Tweetの保存に失敗しますた。", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}
}
