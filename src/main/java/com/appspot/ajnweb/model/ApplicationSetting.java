package com.appspot.ajnweb.model;

import java.io.Serializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * アプリケーションの動作に使用する設定値。
 * <p>twitterのアカウント情報とかいろいろ。</p>
 * @author shin1ogawa
 */
@Model
public class ApplicationSetting implements Serializable {

	private static final long serialVersionUID = 2072753632254402103L;

	/** 設定キーを保持している主キー */
	@Attribute(primaryKey = true)
	private Key key;

	private String value;


	/**
	 * @return the key
	 * @category accessor
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 * @category accessor
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return the value
	 * @category accessor
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 * @category accessor
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
