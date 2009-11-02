package com.appspot.ajnweb.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import appengine.util.MemcacheUtil;

import com.appspot.ajnweb.meta.DailyMeta;
import com.appspot.ajnweb.meta.TweetMeta;
import com.appspot.ajnweb.model.Daily;
import com.appspot.ajnweb.model.Tweet;
import com.google.appengine.api.datastore.Key;

/**
 * {@link Daily}に関する操作を行うユーティリティ。
 * @author shin1ogawa
 */
public class DailyService {

	final static String MEMCACHEKEY_DAILY_PREFIX = TwitterQueryService.class.getName() + ":";


	private DailyService() {
	}

	/**
	 * @param year GMT+9の年
	 * @param month GMT+9の月
	 * @param day GMT+9の日
	 * @return 日別に集計済みの結果を返す。
	 */
	public static Daily getDaily(int year, int month, int day) {
		long ymd = year * 10000 + month * 100 + day;
		DailyMeta dailyMeta = new DailyMeta();
		Key key = Datastore.createKey(dailyMeta, ymd);
		try {
			return Datastore.get(dailyMeta, key);
		} catch (EntityNotFoundRuntimeException e) {
			return null;
		}
	}

	/**
	 * @param year GMT+9の年
	 * @param month GMT+9の月
	 * @param day GMT+9の日
	 * @return 指定された日にPostされたTweet一覧を返す。
	 */
	public static List<Tweet> getDailyTweets(int year, int month, int day) {
		long ymd = year * 10000 + month * 100 + day;
		String memcacheKey = MEMCACHEKEY_DAILY_PREFIX + ymd;
		@SuppressWarnings("unchecked")
		List<Tweet> list = MemcacheUtil.get(memcacheKey, List.class);
		if (list != null) {
			return list;
		}
		TweetMeta tweetMeta = new TweetMeta();
		Date start = getStart(year, month, day);
		Date end = getEnd(year, month, day);
		list =
				Datastore.query(tweetMeta).filter(tweetMeta.createdAt.greaterThanOrEqual(start))
					.filter(tweetMeta.createdAt.lessThanOrEqual(end))
					.sort(tweetMeta.createdAt.desc).asList();
		MemcacheUtil.put(memcacheKey, list);
		return list;
	}

	/**
	 * 日本時間の日別の集計を行う。
	 * @param year GMT+9の年
	 * @param month GMT+9の月
	 * @param day GMT+9の日
	 */
	public static void summary(int year, int month, int day) {
		long ymd = year * 10000 + month * 100 + day;
		MemcacheUtil.delete(MEMCACHEKEY_DAILY_PREFIX + ymd);
		TweetMeta tweetMeta = new TweetMeta();
		Date start = getStart(year, month, day);
		Date end = getEnd(year, month, day);
		int count =
				Datastore.query(tweetMeta).filter(tweetMeta.createdAt.greaterThanOrEqual(start))
					.filter(tweetMeta.createdAt.lessThanOrEqual(end)).count();
		Daily daily;
		DailyMeta dailyMeta = new DailyMeta();
		Key key = Datastore.createKey(dailyMeta, ymd);
		try {
			daily = Datastore.get(dailyMeta, key);
		} catch (EntityNotFoundRuntimeException e) {
			daily = new Daily();
			daily.setKey(key);
			daily.setCreated(new Date(System.currentTimeMillis()));
		}
		daily.setCount(count);
		daily.setUpdated(new Date(System.currentTimeMillis()));
		Datastore.put(daily);
	}

	private static Date getStart(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
		return calendar.getTime();
	}

	private static Date getEnd(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day, 23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+9"));
		return calendar.getTime();
	}
}
