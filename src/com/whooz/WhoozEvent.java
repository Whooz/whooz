package com.whooz;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONArray;

import android.graphics.Bitmap;

public class WhoozEvent {

	private String header;
	private String descrip;
	private String ownerId;
	private Boolean isPrivate;
	private Bitmap pic;
	private WhoozLocation place;
	private int hour;
	private int minute;
	private long date;
	private int id;	
	private String objectId;
	private JSONArray eventUsers;
	private List<String> invitedIds=null;
	private boolean isDefaultImage;

	

	public WhoozEvent(String header, String descrip,
			Boolean isPrivate,long dateInMilli,WhoozLocation location,Bitmap pic,String ownerId,List<String> invitedIds) {
		Date d = new Date(dateInMilli);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		this.header = header;
		this.descrip = descrip;
		this.isPrivate = isPrivate;
		this.place = location;
		this.minute = c.get(GregorianCalendar.MINUTE);
		this.hour = c.get(GregorianCalendar.HOUR_OF_DAY);
		this.date = dateInMilli;
		this.id = ParseManager.getNewId();
		this.pic = pic;	
		this.ownerId = ownerId;
		this.eventUsers = new JSONArray();
		this.invitedIds = invitedIds;
	

	}
	
	public WhoozEvent(int id,String header, String descrip,
			Boolean isPrivate,long dateInMilli,WhoozLocation location,Bitmap pic,String ownerId,List<String> invitedIds) {
		Date d = new Date(dateInMilli);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		this.header = header;
		this.descrip = descrip;
		this.isPrivate = isPrivate;
		this.place = location;
		this.minute = c.get(GregorianCalendar.MINUTE);
		this.hour = c.get(GregorianCalendar.HOUR_OF_DAY);
		this.date = dateInMilli;
		this.id = id;
		this.pic = pic;	
		this.ownerId = ownerId;
		this.eventUsers = new JSONArray();
		this.invitedIds = invitedIds;
	

	}
	
	public WhoozEvent(String header, String descrip,
			Boolean isPrivate,long dateInMilli,WhoozLocation location,Bitmap pic,String objectId,JSONArray eventUsers) {
		Date d = new Date(dateInMilli);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		
		this.header = header;
		this.descrip = descrip;
		this.isPrivate = isPrivate;
		this.place = location;
		this.minute = c.get(GregorianCalendar.MINUTE);
		this.hour = c.get(GregorianCalendar.HOUR_OF_DAY);
		this.date = dateInMilli;
		this.id = ParseManager.getNewId();
		this.pic = pic;		
		this.objectId = objectId;
		this.eventUsers = eventUsers;
	}
	public WhoozEvent(int id,String header, String descrip,
			Boolean isPrivate,long dateInMilli,WhoozLocation location,Bitmap pic,String objectId,JSONArray eventUsers) {
		Date d = new Date(dateInMilli);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		
		this.header = header;
		this.descrip = descrip;
		this.isPrivate = isPrivate;
		this.place = location;
		this.minute = c.get(GregorianCalendar.MINUTE);
		this.hour = c.get(GregorianCalendar.HOUR_OF_DAY);
		this.date = dateInMilli;
		this.id = id;
		this.pic = pic;		
		this.objectId = objectId;
		this.eventUsers = eventUsers;
	}
	/*public WhoozEvent(String header, String descrip,
			Boolean isPrivate,long dateInMilli,Bitmap pic,String objectId) {

		this.header = header;
		this.descrip = descrip;
		this.isPrivate = isPrivate;
		this.objectId = objectId;
	
		Date d = new Date(dateInMilli);

		Calendar c = new GregorianCalendar();
		c.setTime(d);

		this.place = null;
		this.minute = c.get(GregorianCalendar.MINUTE);
		this.hour = c.get(GregorianCalendar.HOUR_OF_DAY);

		this.date = dateInMilli;
		this.id = ParseManager.getNewId();
		this.pic = pic;
		this.eventUsers = new JSONArray();

	}*/


	public List<String> getInvitedIds(){
		return invitedIds;
	}
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDescrip() {
		return descrip;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}



	public Bitmap getPic() {
		return pic;
	}

	public void setPics(Bitmap pic) {
		this.pic = pic;
	}

	public WhoozLocation getPlace() {
		return place;
	}

	public void setLocation(WhoozLocation location) {
		this.place = location;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	public int getHour(){
		return hour;
	}
	public int getMinute(){
		return minute;
	}
	public int getId(){
		return id;
	}
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}


	public void addUser(String userId){
		eventUsers.put(userId);
		
	}

	public JSONArray getUsers(){
		return eventUsers;
	}
	
	public void updateEvent(WhoozEvent event){
		this.header = event.header;
		this.descrip = event.descrip;
		this.ownerId = event.ownerId;
		this.isPrivate = event.isPrivate;
		this.pic = event.pic;
		this.place = event.place;
		this.hour = event.hour;
		this.minute = event.minute;
		this.date = event.date;
		this.id = event.id;	
		this.objectId = event.objectId;
		this.eventUsers = event.eventUsers;
		this.invitedIds = event.invitedIds;
	}
	
	public boolean isDefaultImage() {
		return isDefaultImage;
	}

	public void setDefaultImage(boolean isDefaultImage) {
		this.isDefaultImage = isDefaultImage;
	}
	
}
