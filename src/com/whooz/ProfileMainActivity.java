package com.whooz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ProfileMainActivity extends FragmentActivity {

	ViewPager Tab;
	TabPagerAdapter TabAdapter;
	ActionBar actionBar;
	MainActivity mainActivity;
	private boolean canLoad = true;
	public static ProgressDialog profiledialog;
	
	private boolean doneLoading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context c = getApplicationContext();
		Toast.makeText(getApplicationContext(), "profileActivitiyStarted", Toast.LENGTH_SHORT).show();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_main_activity);		
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle("My Area");
		actionBar.setDisplayHomeAsUpEnabled(true);
		Tab = (ViewPager) findViewById(R.id.pager);
		TabAdapter = new TabPagerAdapter(getSupportFragmentManager(),
				mainActivity, Tab,this);
		Tab.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Toast.makeText(c, "start0", Toast.LENGTH_SHORT).show();
				actionBar.setSelectedNavigationItem(position);
				Toast.makeText(c, "ennnnd0dd", Toast.LENGTH_SHORT).show();
			}
		});
		Tab.setAdapter(TabAdapter);
		// Enable Tabs on Action Bar
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabReselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				Toast.makeText(getApplicationContext(), "start with "+tab.getPosition() , Toast.LENGTH_SHORT).show();
				Tab.setCurrentItem(tab.getPosition());
				Toast.makeText(getApplicationContext(), "end", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onTabUnselected(android.app.ActionBar.Tab tab,
					FragmentTransaction ft) {
				// TODO Auto-generated method stub
			}
		};
		// Add New Tab
		actionBar.addTab(actionBar.newTab().setText("Profile")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Invited to")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Create Event")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("My Events")
				.setTabListener(tabListener));		

		if(savedInstanceState==null){
			Bundle extras = getIntent().getExtras();
			if(extras!=null){
				loadProflileEvents();
				int tab = extras.getInt("tab");
				Tab.setCurrentItem(tab);
			}
		}
	}

	public boolean isCanLoad() {
		return canLoad;
	}

	public boolean isDoneLoading() {
		return doneLoading;
	}

	public void loadProflileEvents() {
		if(!canLoad){
			return;
		}		
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseManager.USER_CLASS_NAME);

		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					InsertEvents insEv = new InsertEvents();
					insEv.execute(users);					
				}
			}
		});
	}
		
	
	private class InsertEvents extends AsyncTask<List<ParseObject>, Void, Void> {

		@Override
		protected Void doInBackground(List<ParseObject>... users) {
			// TODO Auto-generated method stub
			ParseObject user = users[0].get(0);
			loadInvitedToEvents(user);
			loadCreatedEvents(user);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void v) {
			doneLoading = true;
			MainActivity.dialog.hide();
			new java.util.Timer().schedule( 
					new java.util.TimerTask() {
						@Override
						public void run() {
							canLoad = true;
						}
					}, 

					//120000
					10000//amount of time to wait before can update again
					);
		}
		
	}
	
	private void loadInvitedToEvents(ParseObject user){		
		if(!canLoad){
			return;
		}
		JSONArray arr = user
				.getJSONArray(ParseManager.USER_INVITED_TO_EVENTS);
		MainActivity.invitedToEvts = new ArrayList<WhoozEvent>();
		if (arr == null) {
			return;
		}
		if (arr.length() == 0) {
			return;
		}
		ParseQuery<ParseObject> eventsQuery = getEventsQuery(arr);
		eventsQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> events,
					ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					for (ParseObject evt : events) {
						MainActivity.invitedToEvts.add( extractEvent(evt));				
					}
				}
			}
		});
	}
	
	private void loadCreatedEvents(ParseObject user){
		JSONArray arr = user
				.getJSONArray(ParseManager.USER_CREATED_EVENTS);
		if(arr==null){
			return;
		}
		if(arr.length()==0){
			return;
		}
		MainActivity.userCreatedEvents = new ArrayList<WhoozEvent>();
		ParseQuery<ParseObject> eventsQuery = getEventsQuery(arr);
		eventsQuery.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> events,
					ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					for (ParseObject evt : events) {
						MainActivity.userCreatedEvents.add( extractEvent(evt));				
					}
				}
			}
		});
	}
	
	private ParseQuery<ParseObject> getEventsQuery(JSONArray arr){
		List<ParseQuery<ParseObject>> invitedToEventsQuery = new ArrayList<ParseQuery<ParseObject>>();
		ParseQuery<ParseObject> parseEvents;
		String arrStr = arr.toString();
		arrStr = arrStr.replace("\"", "");
		arrStr = arrStr.replace("[", "");
		arrStr = arrStr.replace("]", "");
		String[] spltArr = arrStr.split(",");
		for (int i = 0; i < spltArr.length; i++) {
			int evtId = Integer.parseInt(spltArr[i]);
			parseEvents = ParseQuery.getQuery("Events");
			parseEvents.whereEqualTo(ParseManager.EVENT_ID, evtId);
			invitedToEventsQuery.add(parseEvents);
		}
		ParseQuery<ParseObject> mainQuery = ParseQuery
				.or(invitedToEventsQuery);
		return mainQuery;
	}
	
	private WhoozEvent extractEvent(ParseObject evt){
		byte[] byArr = null;

		Bitmap bmp = null;
		if (evt.getParseFile("coverPhoto") != null) {
			try {
				byArr = evt.getParseFile(
						"coverPhoto").getData();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			bmp = BitmapFactory
					.decodeByteArray(byArr, 0,
							byArr.length, options);
		}
		long eventTimeLong = evt.getNumber("date")
				.longValue();
		WhoozLocation wLoc = new WhoozLocation(evt
				.getParseGeoPoint("place"), evt
				.getString("placeDescription"));
		return new WhoozEvent(evt.getInt(ParseManager.EVENT_ID),
				evt.getString("name"), evt
						.getString("description"),
				evt.getBoolean("isPrivate"),
				eventTimeLong, wLoc, bmp, evt
						.getObjectId(), evt
						.getJSONArray("takingPart"));
	}

}
