package com.appspot.ajnweb.model;

import java.io.Serializable;
import java.util.Date;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * TwitterのStatus.
 * <p>Userの情報を少し展開した状態。</p>
 * @author shin1ogawa
 * @see twitter4j.Status
 * @see twitter4j.Tweet
 */
@Model
public class Tweet implements Serializable {

	private static final long serialVersionUID = 82952814930599860L;

	/** スキーマのバージョン。構造が変わったら更新する事！ */
	private int schemaVersion = 1;

	@Attribute(primaryKey = true)
	private Key key;

	private Date createdAt;

	private Long inReplyToStatusId;

	private Integer inReplyToUserId;

	private Integer userId;

	private String userName;

	@Attribute(lob = true)
	private String text;

	private String userProfileImageUrl;


	/**
	 * {@link twitter4j.Status}の値をコピーしてインスタンスを作成する。
	 * @param status
	 * @return 新しく作成したインスタンス
	 */
	public static Tweet newInstance(twitter4j.Status status) {
		Tweet instance = new Tweet();
		instance.setKey(KeyFactory.createKey(Tweet.class.getSimpleName(), status.getId()));
		instance.setCreatedAt(status.getCreatedAt());
		instance.setInReplyToStatusId(status.getInReplyToStatusId());
		instance.setInReplyToUserId(status.getInReplyToUserId());
		instance.setText(status.getText());
		instance.setUserId(status.getUser().getId());
		instance.setUserName(status.getUser().getName());
		instance.setUserProfileImageUrl(status.getUser().getProfileImageURL().toString());
		return instance;
	}

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
	 * @return the createdAt
	 * @category accessor
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 * @category accessor
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the inReplyToStatusId
	 * @category accessor
	 */
	public Long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	/**
	 * @param inReplyToStatusId the inReplyToStatusId to set
	 * @category accessor
	 */
	public void setInReplyToStatusId(Long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	/**
	 * @return the inReplyToUserId
	 * @category accessor
	 */
	public Integer getInReplyToUserId() {
		return inReplyToUserId;
	}

	/**
	 * @param inReplyToUserId the inReplyToUserId to set
	 * @category accessor
	 */
	public void setInReplyToUserId(Integer inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	/**
	 * @return the userId
	 * @category accessor
	 */
	public Integer getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 * @category accessor
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	/**
	 * @return the userName
	 * @category accessor
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 * @category accessor
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the text
	 * @category accessor
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 * @category accessor
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the userProfileImageUrl
	 * @category accessor
	 */
	public String getUserProfileImageUrl() {
		return userProfileImageUrl;
	}

	/**
	 * @param userProfileImageUrl the userProfileImageUrl to set
	 * @category accessor
	 */
	public void setUserProfileImageUrl(String userProfileImageUrl) {
		this.userProfileImageUrl = userProfileImageUrl;
	}
}
