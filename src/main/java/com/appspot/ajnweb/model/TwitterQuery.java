package com.appspot.ajnweb.model;

import java.io.Serializable;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * Twitter検索で使用する検索文字列。
 * @author shin1ogawa
 */
@Model
public class TwitterQuery implements Serializable {

	private static final long serialVersionUID = -4656424535899743111L;

	/** スキーマのバージョン。構造が変わったら更新する事！ */
	private int schemaVersion = 1;

	@Attribute(primaryKey = true)
	private Key key;

	/** クエリ文字列 */
	private String queryString;


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
	 * @return the parameter
	 * @category accessor
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the parameter to set
	 * @category accessor
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @param schemaVersion the schemaVersion to set
	 * @category accessor
	 */
	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	/**
	 * @return the schemaVersion
	 * @category accessor
	 */
	public int getSchemaVersion() {
		return schemaVersion;
	}
}
