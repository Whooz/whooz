package com.whooz;

public class WhoozComment {

	private String comment;
	private String userId;
	private String userName;
	private String objectId;
	
	public WhoozComment(String comment, String userId, String userName) {

		this.comment = comment;
		this.userId = userId;
		this.setUserName(userName);
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


}
