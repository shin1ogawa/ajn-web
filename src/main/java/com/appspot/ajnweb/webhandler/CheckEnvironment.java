package com.appspot.ajnweb.webhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slim3.datastore.Datastore;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;

import com.appspot.ajnweb.meta.EnvInfoMeta;
import com.appspot.ajnweb.model.EnvInfo;
import com.appspot.ajnweb.service.ApplicationSettingService;
import com.google.apphosting.api.ApiProxy;

/**
 * AppEngineのプラットフォームに関する状態をチェックし、事件があれば全力でつぶやく。
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class CheckEnvironment extends HttpServlet {

	static final String DATACENTER_KEY = "com.google.apphosting.api.ApiProxy.datacenter";

	static final Logger logger = Logger.getLogger(CheckEnvironment.class.getName());


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");

		EnvInfoMeta meta = EnvInfoMeta.get();
		EnvInfo expectedServerInfo =
				Datastore.getOrNull(meta, (Datastore.createKey(meta, "serverinfo")));
		EnvInfo expectedDataCenter =
				Datastore.getOrNull(meta, (Datastore.createKey(meta, DATACENTER_KEY)));

		if (expectedDataCenter == null || expectedServerInfo == null) {
			return;
		}

		PrintWriter writer = resp.getWriter();
		checkServerInfo(expectedServerInfo, writer);
		checkAttributes(expectedDataCenter, writer);
		resp.flushBuffer();
	}

	private void checkAttributes(EnvInfo expectedDataCenter, PrintWriter writer) {
		EnvInfoMeta meta = EnvInfoMeta.get();
		Map<String, Object> attributes = ApiProxy.getCurrentEnvironment().getAttributes();
		Iterator<Entry<String, Object>> i = attributes.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Object> next = i.next();
			String key = next.getKey();
			Object value = next.getValue();
			writer.println(key + "=" + value);
			if (StringUtils.equals(DATACENTER_KEY, key) == false) {
				if (Datastore.getOrNull(meta, Datastore.createKey(meta, key)) == null) {
					EnvInfo entity = new EnvInfo();
					entity.setKey(Datastore.createKey(meta, key));
					if (value != null) {
						entity.setValue(value.toString());
					}
					String message = "【重要】見た事が無いattribute keyを見つけたかも！:" + key + " #appengine";
					updateStatusAndPutEntity(message, expectedDataCenter, writer);
				}
			} else {
				Set<String> values = expectedDataCenter.getValues();
				if (values.contains(value) == false) {
					expectedDataCenter.getValues().add(value.toString());
					updateStatusAndPutEntity("【重要】未知のデータセンター(na1以外)上で動作しているかも！:" + value
							+ " #appengine", expectedDataCenter, writer);
				}
			}
		}
	}

	void checkServerInfo(EnvInfo expectedServerInfo, PrintWriter writer) {
		String serverInfo = getServletContext().getServerInfo();
		writer.println("serverinfo=" + serverInfo);
		if (StringUtils.equals(expectedServerInfo.getValue(), serverInfo) == false) {
			Set<String> values = expectedServerInfo.getValues();
			values.add(serverInfo);
			expectedServerInfo.setValues(values);
			expectedServerInfo.setValue(serverInfo);
			updateStatusAndPutEntity("【重要】SDKのバージョンが変わっているかも！:" + serverInfo
					+ " #appengine 関係者は要確認:/sys/checkenv", expectedServerInfo, writer);
		}
	}

	void updateStatusAndPutEntity(String message, EnvInfo entity, PrintWriter writer) {
		try {
			writer.println(message);
			updateStatus(message);
			Datastore.put(entity);
		} catch (TwitterException e) {
			logger.log(Level.WARNING, "つぶやきに失敗しました。", e);
		} catch (Exception e) {
			logger.log(Level.WARNING, "たぶんデータの保存に失敗しました。"
					+ ToStringBuilder.reflectionToString(entity), e);
			try {
				updateStatus("@shin1ogawa やばいよ、何度も同じことをつぶやきそうです。メンテよろ！");
			} catch (TwitterException e1) {
				logger.log(Level.WARNING, "つぶやきに失敗しました。", e);
			}
		}
	}

	void updateStatus(String status) throws TwitterException {
		logger.info(status);
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
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);
		twitter.setOAuthAccessToken(new AccessToken(accessToken, tokenSecret));
		twitter.updateStatus(status);
	}
}
