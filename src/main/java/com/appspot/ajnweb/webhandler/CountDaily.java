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
			try {
				String ymdString = String.format("%04d-%02d-%02d", year, month, day);
				String twitterUser =
						ApplicationSettingService.get(
								ApplicationSettingService.SettingKey.TWITTER_USER, "");
				String twitterPassword =
						ApplicationSettingService.get(
								ApplicationSettingService.SettingKey.TWITTER_PASSWORD, "");
				if (StringUtils.isNotEmpty(twitterUser) && StringUtils.isNotEmpty(twitterPassword)) {
					Twitter twitter = new Twitter(twitterUser, twitterPassword);
					String status =
							ymdString + "のAppEngine関連のつぶやきをまとめました。 http://ajn-web.appspot.com/day/"
									+ ymdString;
					twitter.updateStatus(status);
				}
			} catch (Throwable th) {
				Logger.getLogger(CountDaily.class.getName()).log(Level.WARNING, "集計報告のつぶやきに失敗したお",
						th);
			}
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			Logger.getLogger(CountDaily.class.getName()).log(Level.WARNING, "日別集計に失敗しますた。", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
