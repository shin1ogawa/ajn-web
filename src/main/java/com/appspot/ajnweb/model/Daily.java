package com.appspot.ajnweb.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * 日別の集計用。
 * <p>primary keyは年4桁月2桁日2桁の数値8桁.</p>
 * @author shin1ogawa
 */
@Model
public class Daily implements Serializable {

	private static final long serialVersionUID = -1256090940876843174L;

	/** スキーマのバージョン。構造が変わったら更新する事！ */
	private int schemaVersion = 1;

	@Attribute(primaryKey = true)
	private Key key;

	private Integer count;

	private Date created;

	private Date updated;


	/**
	 * @return the schemaVersion
	 * @category accessor
	 */
	public int getSchemaVersion() {
		return schemaVersion;
	}

	/**
	 * @param schemaVersion the schemaVersion to set
	 * @category accessor
	 */
	public void setSchemaVersion(int schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

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
	 * @return the count
	 * @category accessor
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 * @category accessor
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the created
	 * @category accessor
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 * @category accessor
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @param updated the updated to set
	 * @category accessor
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	/**
	 * @return the updated
	 * @category accessor
	 */
	public Date getUpdated() {
		return updated;
	}
}
