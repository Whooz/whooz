package com.whooz;

public class NavDrawerItem {

	private String title;
	private int icon;
	private String username;
	private String userId;


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserId() {
		return userId;
	}

	public NavDrawerItem(String username, String userId) {		
		this.username = username;
		this.userId = userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public NavDrawerItem(){}

	public NavDrawerItem(String title, int icon){
		this.title = title;
		this.icon = icon;
	}

	public String getTitle(){
		return this.title;
	}

	public int getIcon(){
		return this.icon;
	}


	public void setTitle(String title){
		this.title = title;
	}

	public void setIcon(int icon){
		this.icon = icon;
	}

}
