package com.appspot.ajnweb.model;

import java.io.Serializable;
import java.util.Set;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * 環境のチェックに使用するためのモデル。
 * <p>Keyのname値に、環境変数のキー値等を放り込んでおく</p>
 * @author shin1ogawa
 */
@Model
public class EnvInfo implements Serializable {

	private static final long serialVersionUID = 7932931273237762710L;

	@Attribute(primaryKey = true)
	private Key key;

	private String value;

	private Set<String> values;


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
	 * @return the values
	 * @category accessor
	 */
	public Set<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 * @category accessor
	 */
	public void setValues(Set<String> values) {
		this.values = values;
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
