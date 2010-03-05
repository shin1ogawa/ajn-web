package com.appspot.ajnweb.webhandler;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import appengine.util.AppEngineUtil;

import com.appspot.ajnweb.service.ApplicationSettingService;
import com.appspot.ajnweb.service.DailyService;

/**
 * 日別のTweet件数を集計する。
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class CountDaily extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String yearString = req.getParameter("year");
		String monthString = req.getParameter("month");
		String dayString = req.getParameter("day");
		try {
			int year = 0;
			int month = 0;
			int day = 0;
			if (StringUtils.isNotEmpty(yearString) && StringUtils.isNotEmpty(monthString)
					&& StringUtils.isNotEmpty(dayString)) {
				year = Integer.parseInt(yearString);
				month = Integer.parseInt(monthString);
				day = Integer.parseInt(dayString);
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
				calendar.add(Calendar.DATE, -1); // 前日が対象
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH) + 1;
				day = calendar.get(Calendar.DATE);
			}
			DailyService.summary(year, month, day);
			if (AppEngineUtil.isProduction()) {
				updateStatus(year, month, day);
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			Logger.getLogger(CountDaily.class.getName()).log(Level.WARNING, "日別集計に失敗しますた。", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void updateStatus(int year, int month, int day) {
		try {
			String ymdString = String.format("%04d-%02d-%02d", year, month, day);
			String consumerKey =
					ApplicationSettingService.get(
							ApplicationSettingService.SettingKey.TWITTER_OAUTH_CONSUMER_KEY, "");
			String consumerSecret =
					ApplicationSettingService.get(
							ApplicationSettingService.SettingKey.TWITTER_OAUTH_CONSUMER_SECRET, "");
			String accessToken =
					ApplicationSettingService.get(
							ApplicationSettingService.SettingKey.TWITTER_OAUTH_TOKEN, "");
			String tokenSecret =
					ApplicationSettingService.get(
							ApplicationSettingService.SettingKey.TWITTER_OAUTH_TOKEN_SECRET, "");
			if (StringUtils.isNotEmpty(consumerKey) && StringUtils.isNotEmpty(consumerSecret)
					&& StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(tokenSecret)) {
				Twitter twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(consumerKey, consumerSecret);
				twitter.setOAuthAccessToken(new AccessToken(accessToken, tokenSecret));
				String status =
						ymdString + "のAppEngine関連のつぶやきをまとめました。 http://ajn-web.appspot.com/day/"
								+ ymdString;
				twitter.updateStatus(status);
			}
		} catch (Throwable th) {
			Logger.getLogger(CountDaily.class.getName()).log(Level.WARNING, "集計報告のつぶやきに失敗したお", th);
		}
	}
}
