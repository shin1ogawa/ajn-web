package com.appspot.ajnweb.service;

import java.util.HashMap;
import java.util.Map;

import com.google.apphosting.api.ApiProxy;

/**
 * Serviceクラスの自動テストで使う{@link ApiProxy.Environment}.
 * @author shin1ogawa
 */
public class LocalEnvironment implements ApiProxy.Environment {

	final String appId;

	final String versionId;


	/**
	 * the constructor.
	 * @param appId
	 * @param versionId
	 * @category constructor
	 */
	LocalEnvironment(String appId, String versionId) {
		this.appId = appId;
		this.versionId = versionId;
	}

	/**
	 * sdkで起動した時に{@code ApiProxy.getCurrentEnvironment().getAppId()}で取得される値
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * sdkで起動した時に{@code ApiProxy.getCurrentEnvironment().getVersionId()}
	 * で取得される値
	 */
	public String getVersionId() {
		return versionId;
	}

	public String getRequestNamespace() {
		return "";
	}

	public String getAuthDomain() {
		return "gmail.com";
	}

	public boolean isLoggedIn() {
		return true;
	}

	public String getEmail() {
		return "servicetest@gmail.com";
	}

	public boolean isAdmin() {
		return true;
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<String, Object>();
		return map;
	}
}
