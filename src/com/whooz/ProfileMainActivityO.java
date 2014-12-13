package com.whooz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class ProfileMainActivityO extends FragmentActivity implements TabListener {
	private ViewPager viewPager;
	private TabsPagerAdapterO mAdapter;
	private ActionBar actionBar;
	private boolean canLoad = true;
	private boolean doneLoading = false;
	public static ProgressDialog profiledialog;
	// Tab titles
	private String[] tabs = { "PROFILE","INVITED TO","MY EVENTS" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_main_activity);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapterO(getSupportFragmentManager(),this);

		viewPager.setAdapter(mAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		if(savedInstanceState==null){
			Bundle extras = getIntent().getExtras();
			if(extras!=null){
				if(getIntent().hasExtra("tab")){
					loadProflileEvents();
					int tab = extras.getInt("tab");
					viewPager.setCurrentItem(tab);
				}
			}
		}

	}


	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

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

			@SuppressWarnings("unchecked")
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
