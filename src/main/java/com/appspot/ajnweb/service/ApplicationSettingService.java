package com.appspot.ajnweb.service;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.EntityNotFoundRuntimeException;

import com.appspot.ajnweb.meta.ApplicationSettingMeta;
import com.appspot.ajnweb.model.ApplicationSetting;

/**
 * {@link ApplicationSetting}に関する操作を行うユーティリティ。
 * @author shin1ogawa
 */
public class ApplicationSettingService {

	/**
	 * 設定値のキーの列挙。
	 * @author shin1ogawa
	 */
	public enum SettingKey {
		/** Twitterのアカウントのユーザ名 */
		TWITTER_USER, //
		/** Twitterのアカウントのパスワード */
		TWITTER_PASSWORD,
		/** Twitter:OAuth ConsumerKey */
		TWITTER_OAUTH_CONSUMER_KEY,
		/** Twitter:OAuth ConsumerSecret */
		TWITTER_OAUTH_CONSUMER_SECRET;
	}


	/**
	 * アプリケーションの設定値を設定する。
	 * @param key 設定のキー
	 * @param value 設定値
	 */
	public static void put(SettingKey key, String value) {
		ApplicationSettingMeta meta = new ApplicationSettingMeta();
		ApplicationSetting setting = new ApplicationSetting();
		setting.setKey(Datastore.createKey(meta, key.name()));
		setting.setValue(value);
		Datastore.put(setting);
	}

	/**
	 * 設定値を取得する。
	 * @param key 設定のキー
	 * @param defaultValue 設定値が存在しない場合に返す値
	 * @return {@code key}に対する設定値。{@code key}に対する値が存在しない場合は{@code defaultValue}。
	 */
	public static String get(SettingKey key, String defaultValue) {
		ApplicationSettingMeta meta = new ApplicationSettingMeta();
		try {
			ApplicationSetting setting = Datastore.get(meta, Datastore.createKey(meta, key.name()));
			return setting.getValue();
		} catch (EntityNotFoundRuntimeException e) {
			return defaultValue;
		}
	}
}
