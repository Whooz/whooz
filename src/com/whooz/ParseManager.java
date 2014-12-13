package com.whooz;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;

import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

public class ParseManager {
	public static String EVENT_CLASS_NAME = "Events";
	public static String USER_CLASS_NAME = "Users";
	public static String COUNTERS_CLASS_NAME = "Counters";

	public static String EVENT_ID = "eventId";
	public static String EVENT_NAME = "name";
	public static String EVENT_DESCRIPTION = "description";
	public static String EVENT_PLACE = "place";
	public static String EVENT_DATE = "date";
	public static String EVENT_OWNER = "eventOwner";
	public static String EVENT_PRIVATE = "isPrivate";
	public static String EVENT_PHOTO = "coverPhoto";
	public static String EVENT_INVITED = "invited";
	public static String EVENT_TAKING_PART = "takingPart";
	public static String EVENT_PLACE_DESCRIPTION = "placeDescription";

	public static String USER_ID = "user_id";
	public static String USER_PENDING = "pendingFriendRequests";
	public static String USER_TAKINGPART = "takingPart";

	public static String USER_FIRST_NAME = "firstName";
	public static String USER_SECOND_NAME = "secondName";
	public static String USER_EMAIL_ADDRESS = "emailAddress";
	public static String USER_AGE = "age";
	public static String USER_GENDER = "gender";
	public static String USER_FRIENDS = "friends";
	public static String USER_REALTIONSHEEP_STATUS = "realtionsheepStatus";
	public static String USER_INTERSTED_IN = "interstedIn";
	public static String USER_ABOUT = "about";
	public static String USER_CREATED_EVENTS = "createdEvents";
	public static String USER_INVITED_TO_EVENTS = "invitedToEvents";

	public static String COUNTERS_CURR_FREE_ID = "currentFreeEventId";
	public static int currId;
	public static void setId(int id) {
		currId = id;
	}

	public static void addEvent(WhoozEvent event) {
		ParseObject obj = new ParseObject(EVENT_CLASS_NAME);
		final int eventid=event.getId();
		//String [] invitedIdsArr = new String[event.getInvitedIds().size()];
		JSONArray invitedIdsArr = new JSONArray();
		for(int i=0;i<event.getInvitedIds().size();i++) invitedIdsArr.put((event.getInvitedIds()).get(i));
		obj.put(EVENT_ID,eventid );
		obj.put(EVENT_NAME, event.getHeader());
		obj.put(EVENT_DESCRIPTION, event.getDescrip());
		obj.put(EVENT_PLACE, event.getPlace().getGeoPoint());
		obj.put(EVENT_PLACE_DESCRIPTION, event.getPlace().getDescription());
		obj.put(EVENT_DATE, event.getDate());
		obj.put(EVENT_OWNER, event.getOwnerId());
		obj.put(EVENT_PRIVATE, event.getIsPrivate());
		obj.put(EVENT_INVITED, invitedIdsArr);
		Bitmap bmp = event.getPic();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		ParseFile file = new ParseFile(byteArray);
		file.saveInBackground();
		obj.put(EVENT_PHOTO, file);
		obj.put(EVENT_TAKING_PART, new JSONArray());
		obj.saveInBackground();

		//save the event id to every invited user
		for (int i=0;i<invitedIdsArr.length();i++){
			try {
				ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);
				query.whereEqualTo(USER_ID, invitedIdsArr.get(i));
				query.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> users, ParseException e) {
						// TODO Auto-generated method stub
						for(ParseObject user : users){
							JSONArray eventsInvitedTo = user.getJSONArray(USER_INVITED_TO_EVENTS);	
							eventsInvitedTo.put(eventid);
							user.put(USER_INVITED_TO_EVENTS, eventsInvitedTo);
							user.saveInBackground();
						}

					}
				});
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		//Until here
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);
		final String userId = MainActivity.currUser.getId();
		query.whereEqualTo(USER_ID, userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				ParseObject user=users.get(0);
				user.add(USER_CREATED_EVENTS, eventid);				
				try {
					user.save();					
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		sendInvatations(eventid);
	}

	public static int getNewId() {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(COUNTERS_CLASS_NAME);

		try {
			ParseObject object = query.get("YyfMpIz5gw");
			int id = object.getInt(COUNTERS_CURR_FREE_ID);
			object.put(COUNTERS_CURR_FREE_ID, id + 1);
			object.saveInBackground();
			return id;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -1;
	}

	public static void addUser(GraphUser user) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);
		final String userId = MainActivity.currUser.getId();
		query.whereEqualTo(USER_ID, userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					if (users.size() == 0) {
						String birthday = MainActivity.currUser.getBirthday();
						Calendar c = Calendar.getInstance();
						int currentYear = c.get(Calendar.YEAR);
						int usersYear = 0;
						if (birthday!=null){
							usersYear = Integer.parseInt(birthday.split("/")[2]);
						}
						int age = currentYear - usersYear;
						ParseObject obj = new ParseObject(USER_CLASS_NAME);
						obj.put(USER_ID, userId);
						/*here*/obj.put(USER_FIRST_NAME,
								MainActivity.currUser.getFirstName());
						obj.put(USER_SECOND_NAME,
								MainActivity.currUser.getLastName());
						obj.put(EVENT_TAKING_PART, new JSONArray());
						obj.put(USER_INVITED_TO_EVENTS, new JSONArray());
						obj.put(USER_PENDING, new JSONArray());
						obj.put(USER_TAKINGPART, new JSONArray());	
						obj.put(USER_FRIENDS, new JSONArray());
						//						String email = (String) MainActivity.currUser.asMap()
						//								.get("email");
						//						Log.i("************************", email);
						//						obj.put(USER_EMAIL_ADDRESS, email);

						obj.put(USER_AGE, age);
						obj.put(USER_GENDER, (String) MainActivity.currUser
								.asMap().get("gender"));
						try {
							obj.save();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				}
			}
		});
	}

	public static void updateUserInfo(final String firstName, final String secondName,
		 final int age, final String gender, final String intestedIn,
			final String realtionsheepStat, final String about) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);
		final String userId = MainActivity.currUser.getId();
		query.whereEqualTo(USER_ID, userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				ParseObject user=users.get(0);
				user.put(USER_FIRST_NAME, firstName);
				user.put(USER_SECOND_NAME, secondName);
//				user.put(USER_EMAIL_ADDRESS,email );
				user.put(USER_AGE,age );
				user.put(USER_GENDER, gender);
				user.put(USER_INTERSTED_IN, intestedIn);
				user.put(USER_REALTIONSHEEP_STATUS,realtionsheepStat );				
				user.put(USER_ABOUT, about);
				try {
					user.save();					
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static void sendFriendRequest(final String userId) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);		
		query.whereEqualTo(USER_ID, userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				ParseObject user=users.get(0);
				user.add("pendingFriendRequests", MainActivity.currUser.getId());

				user.saveInBackground();									
			}
		});
	}

	public static void checkFriendRequest(final Context context) {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);		
		query.whereEqualTo(USER_ID, MainActivity.currUser.getId());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				JSONArray requests;
				final ParseObject user;
				if (!users.isEmpty()){
				/*here*/ user=users.get(0);
				requests = user.getJSONArray("pendingFriendRequests");
				}
				else{
					user = null;
					requests = new JSONArray();
				} 
				for(int i=0;i<requests.length();i++){
					ParseQuery<ParseObject> query2 = ParseQuery.getQuery(USER_CLASS_NAME);		
					try {
						query2.whereEqualTo(USER_ID, requests.getString(i));
						final ParseObject sender = query2.find().get(0);
						String  message = sender.getString(USER_FIRST_NAME)+" "+sender.getString(USER_SECOND_NAME)+" wants to be friend with you";
						DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which){
								case DialogInterface.BUTTON_POSITIVE:
									//Yes button clicked
									ArrayList<String>	elementToRemove=new ArrayList<String>();
									elementToRemove.add(sender.getString(USER_ID));
									user.removeAll("pendingFriendRequests",elementToRemove);
									user.add(USER_FRIENDS, sender.get(USER_ID));
									sender.add(USER_FRIENDS, user.get(USER_ID));
									user.saveInBackground();
									sender.saveInBackground();
									break;

								case DialogInterface.BUTTON_NEGATIVE:
									//No button clicked
									elementToRemove=new ArrayList<String>();
									elementToRemove.add(sender.getString(USER_ID));
									user.removeAll("pendingFriendRequests",elementToRemove);
									user.saveInBackground();
									break;
								}
							}
						};

						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setMessage(message).setPositiveButton("Conifirm", dialogClickListener)
						.setNegativeButton("Ignore", dialogClickListener).show();
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}


	public static void sendInvatations(final int eventId){
		for(String friend: InviteFriends.invitedFriends){
			ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);		
			query.whereEqualTo(USER_ID, friend);
			query.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> users, ParseException e) {
					ParseObject user=users.get(0);
					user.add(USER_INVITED_TO_EVENTS, eventId);
					user.saveInBackground();

				}
			});
		}

	}



	/**
	 * this function all event's dependencies (in the User table)
	 * and finally deletes the event itself from the events table.
	 * the dependencies are:
	 * - delete eventId from the createdEvents column of the event owner (in comments: dep-1).
	 * - delete eventId from the invitedToEvents column of all the people that are invited(in comments: dep-2).
	 * @param obj - the event to be deleted
	 */
	public static void deleteProcedure(ParseObject obj){
		try{
			//Necessary info
			final String ownerId = obj.getString(EVENT_OWNER);
			final int eventId = obj.getInt(EVENT_ID);
			final JSONArray invitedFriends = obj.getJSONArray(EVENT_INVITED);
			//query for finding event owner
			ParseQuery<ParseObject> ownerQuery = ParseQuery.getQuery(USER_CLASS_NAME);
			ownerQuery.whereEqualTo(USER_ID, ownerId);
			//list of queries that will be send to parse with 'OR' condition (for reducing parse calls) 
			List<ParseQuery<ParseObject>> queryList = new ArrayList<ParseQuery<ParseObject>>();
			queryList.add(ownerQuery);
			//queries for finding all invited people, and adding them to the list
			for(int i=0;i<invitedFriends.length();i++){
				ParseQuery<ParseObject> invitedQuery = ParseQuery.getQuery(USER_CLASS_NAME);
				invitedQuery.whereEqualTo(USER_ID, invitedFriends.get(i));
				queryList.add(invitedQuery);	
			}
			//combined or query and parse request
			ParseQuery<ParseObject> mainQuery=ParseQuery.or(queryList);
			mainQuery.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> users, ParseException e) {
					try{
						for(ParseObject user: users){//users holds the event owner and invited people
							//handle dep-1
							if(user.getString(USER_ID).equals(ownerId)){//delete the eventId from "created events"
								JSONArray createdEvents = user.getJSONArray(USER_CREATED_EVENTS);
								JSONArray newCreatedEvents = new JSONArray();
								for(int i=0;i<createdEvents.length();i++){
									if(createdEvents.getInt(i)==eventId)
										continue;
									else
										newCreatedEvents.put(createdEvents.getInt(i));
								}
								user.put(USER_CREATED_EVENTS, newCreatedEvents);
							}//handle dep-2
							else{//delete the eventId from "invited to"
								JSONArray invitedTo = user.getJSONArray(USER_INVITED_TO_EVENTS);
								JSONArray newInvitedTo=new JSONArray();
								for(int i=0;i<invitedTo.length();i++){
									if(invitedTo.getInt(i)==eventId)
										continue;
									else
										newInvitedTo.put(invitedTo.getInt(i));
								}
								user.put(USER_INVITED_TO_EVENTS, newInvitedTo);
							}
							//save the new user object(w/o the eventId in dep-1 or dep-2)
							user.saveInBackground();
						}

					}catch(Exception err){
						err.printStackTrace();
					}
				}
			});
			//delete the event from events table
			obj.deleteInBackground();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of deleteProcedure

	/**
	 * this method takes the parameters from newEvent, updates them in oldEvent
	 * and saves the oldEvent to parse
	 * @param oldEvent
	 * @param newEvent
	 */
	public static void updateEvent(ParseObject oldEvent,final WhoozEvent newEvent) {
		//starting with gathering the new information and putting it in the old event's parse object
		//invited friends...
		JSONArray oldInvited = oldEvent.getJSONArray(EVENT_INVITED);//invited ppl from old event
		//old invited from json to list
		List<String> oldInvitedList = new ArrayList<String>();
		for(int i=0;i<oldInvited.length();i++){
			try {
				oldInvitedList.add(oldInvited.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//adding to them  the new invited friends w/o duplications.
		JSONArray invitedIdsArr = oldInvited;
		for(int i=0;i<newEvent.getInvitedIds().size();i++){
			if(!oldInvitedList.contains((newEvent.getInvitedIds()).get(i).toString()))
				invitedIdsArr.put((newEvent.getInvitedIds()).get(i).toString());
		}
		//cover photo...
		Bitmap bmp = newEvent.getPic();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();

		ParseFile file = new ParseFile(byteArray);
		file.saveInBackground();
		oldEvent.put(EVENT_NAME, newEvent.getHeader());
		oldEvent.put(EVENT_DESCRIPTION,newEvent.getDescrip());
		oldEvent.put(EVENT_PLACE, newEvent.getPlace().getGeoPoint());
		oldEvent.put(EVENT_PLACE_DESCRIPTION,newEvent.getPlace().getDescription());
		oldEvent.put(EVENT_DATE,newEvent.getDate());
		oldEvent.put(EVENT_PRIVATE, newEvent.getIsPrivate());
		oldEvent.put(EVENT_PHOTO, file);
		oldEvent.put(EVENT_INVITED, invitedIdsArr);

		//the old event is no more needed(all needed info is already been taken), therefore we can save
		//!!!!NOTE: if in future updates you're about to use more information from old event, 
		//move the save line after that use...(otherwise you will get info from the UPDATED event)
		oldEvent.saveInBackground();

		//save the event id to every ***NEW*** invited user
		for (int i=0;i<invitedIdsArr.length();i++){
			try {
				if(oldInvitedList.contains(invitedIdsArr.get(i).toString()))
					continue;

				ParseQuery<ParseObject> query = ParseQuery.getQuery(USER_CLASS_NAME);
				query.whereEqualTo(USER_ID, invitedIdsArr.get(i));
				query.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> users, ParseException e) {
						// TODO Auto-generated method stub
						for(ParseObject user : users){
							JSONArray eventsInvitedTo = user.getJSONArray(USER_INVITED_TO_EVENTS);
							eventsInvitedTo.put(newEvent.getId());
							user.put(USER_INVITED_TO_EVENTS, eventsInvitedTo);
							user.saveInBackground();
						}
					}
				});
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		//send push notifications
		try{
			if (invitedIdsArr != null) {
				//to new invited
				for (int i=0;i<invitedIdsArr.length();i++) {
					if(!oldInvitedList.contains(invitedIdsArr.get(i).toString())){
						ParseQuery query = ParseInstallation.getQuery();
						query.whereEqualTo("userId", invitedIdsArr.get(i).toString());
						ParsePush push = new ParsePush();
						push.setQuery(query);
						push.setMessage(MainActivity.currUser.getName()
								+ "has invited you to his event!");
						push.sendInBackground();
					}
				}
				//to old invited
				for(String friendId:oldInvitedList){
					ParseQuery query = ParseInstallation.getQuery();
					query.whereEqualTo("userId", friendId);
					ParsePush push = new ParsePush();
					push.setQuery(query);
					push.setMessage(MainActivity.currUser.getName()
							+ "has updated his event, you should check it out!");
					push.sendInBackground();
				}
			}
		}catch(JSONException e2){
			e2.printStackTrace();
		}
		//Until here
	}


}


