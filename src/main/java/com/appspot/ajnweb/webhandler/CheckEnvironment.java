package com.appspot.ajnweb.webhandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.appspot.ajnweb.service.ApplicationSettingService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.apphosting.api.ApiProxy;

/**
 * AppEngineのプラットフォームに関する状態をチェックし、事件があれば全力でつぶやく。
 * <p>TODO すでにつぶやいたかどうか？のフラグはmemcacheで管理しているので何度か繰り返してつぶやいちゃぅかも。</p>
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class CheckEnvironment extends HttpServlet {

	static final String KEY_DATACENTER = "com.google.apphosting.api.ApiProxy.datacenter";

	static final String EXPECTED_DATACENTER = "na1";

	static final String EXPECETD_SERVERINFO = "Google App Engine/1.3.0";

	static final Logger logger = Logger.getLogger(CheckEnvironment.class.getName());


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println("serverinfo=" + checkServerInfo());
		resp.getWriter().println("datacenter=" + checkDataCenter());
	}

	private String checkDataCenter() {
		String dataCenter = null;
		Object dataCenterObject =
				ApiProxy.getCurrentEnvironment().getAttributes().get(KEY_DATACENTER);
		if (dataCenterObject != null) {
			dataCenter = dataCenterObject.toString();
			if (dataCenter.equals(EXPECTED_DATACENTER) == false
					&& StringUtils.isNotEmpty(dataCenter)) {
				// 未知のデータセンター上で動作しているようだ。
				Object flag =
						MemcacheServiceFactory.getMemcacheService().get("DATACENTER_" + dataCenter);
				if (flag == null) {
					// みんなに知らせなきゃ！！
					try {
						updateStatus("【重要】未知のデータセンター(na1以外)上で動作しているかも！:" + dataCenter
								+ " #appengine");
						MemcacheServiceFactory.getMemcacheService().put("DATACENTER_" + dataCenter,
								Boolean.TRUE);
					} catch (TwitterException e) {
						logger.log(Level.WARNING, "つぶやきに失敗しました。", e);
					}
				}
			}
		}
		return dataCenter;
	}

	private String checkServerInfo() {
		String serverInfo = getServletContext().getServerInfo();
		if (StringUtils.equalsIgnoreCase(serverInfo, EXPECETD_SERVERINFO) == false
				&& StringUtils.isNotEmpty(serverInfo)) {
			// たぶんSDKのバージョンが変わった。
			Object flag =
					MemcacheServiceFactory.getMemcacheService().get("SERVERINFO_" + serverInfo);
			if (flag == null) {
				// みんなに知らせなきゃ！！
				try {
					updateStatus("【重要】SDKのバージョンが変わっているかも！:" + serverInfo + " #appengine");
					MemcacheServiceFactory.getMemcacheService().put("SERVERINFO_" + serverInfo,
							Boolean.TRUE);
				} catch (TwitterException e) {
					logger.log(Level.WARNING, "つぶやきに失敗しました。", e);
				}
			}
		}
		return serverInfo;
	}

	private void updateStatus(String status) throws TwitterException {
		logger.info(status);
		String twitterUser =
				ApplicationSettingService
					.get(ApplicationSettingService.SettingKey.TWITTER_USER, "");
		String twitterPassword =
				ApplicationSettingService.get(
						ApplicationSettingService.SettingKey.TWITTER_PASSWORD, "");
		if (StringUtils.isNotEmpty(twitterUser) && StringUtils.isNotEmpty(twitterPassword)) {
			Twitter twitter = new Twitter(twitterUser, twitterPassword);
			twitter.updateStatus(status);
		}
	}
}
