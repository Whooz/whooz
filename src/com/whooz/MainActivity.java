package com.whooz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

public class MainActivity extends FragmentActivity {
	public static GraphUser currUser;
	public static boolean onStop = false;
	public static Fragment fragment_resume;
	public static boolean canRefresh = true; 
	public static boolean loadEventsAgain = false; 
	public static boolean curEventLayoutIsLookAround = true;
	public static boolean curEventLayoutIsWhatsNext = false;
	public static boolean curEventLayoutIsInvitedTo = false;
	public static ArrayList<WhoozEvent> lookArondEvents;
	public static ArrayList<WhoozEvent> futureEvents;
	public static ArrayList<WhoozEvent> invitedToEvts;
	public static ArrayList<WhoozEvent> userCreatedEvents;
	public static ProgressDialog dialog;

	public static Location userLoc = new Location("") {
		{
			setLatitude(31.7820);
			setLongitude(35.219684);
		}
	};

	private boolean checkUser = true;
	private MainActivity that = this;
	private boolean checked = false;
	private String[] navDrawerTitles;
	private DrawerLayout drawerLayout;
	private ArrayList<NavDrawerItem> navDrawItems;
	private ListView drawerList;
	private boolean isResumed = false;	
	private UiLifecycleHelper uiHelper;
	private Context context;	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};		

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onStop=false;
		PushService.setDefaultPushCallback(this, MainActivity.class);
		curEventLayoutIsLookAround = true;
		setContentView(R.layout.activity_main);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		initUiHelper(savedInstanceState);		
		initEventsArrays();
		initGPS();
		initFragment();		
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	private void initFragment() {
		context = MainActivity.this;		
		Fragment fragment = new WhoozMainActivity(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.frameContainer, fragment).commit();
	}

	private void initGPS() {
		GPSTracker gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			userLoc.setLongitude(gps.getLongitude());
			userLoc.setLatitude(gps.getLatitude());
		}else{
			Toast.makeText(getApplicationContext(), "can't get location. using last location or default location(jerusalem)", 
					Toast.LENGTH_LONG).show();
		}
	}

	private void initUiHelper(Bundle savedInstanceState) {
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	private void initEventsArrays() {
		lookArondEvents = new ArrayList<WhoozEvent>();
		futureEvents = new ArrayList<WhoozEvent>();
		invitedToEvts = new ArrayList<WhoozEvent>();
		userCreatedEvents = new ArrayList<WhoozEvent>();
	}


	public void load(Fragment fragment) {				
		if (canRefresh) {
			canRefresh = false;
			ParseQuery<ParseObject> parseEvents = ParseQuery.getQuery("Events");
			parseEvents.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> arg0, ParseException arg1) {
					InsertEvents insEv = new InsertEvents();
					insEv.execute(arg0);
				}

			});
		}

	}

	private class InsertEvents extends AsyncTask<List<ParseObject>, Void, Void> {

		@Override
		protected Void doInBackground(List<ParseObject>... arg0) {
			lookArondEvents = new ArrayList<WhoozEvent>();
			futureEvents = new ArrayList<WhoozEvent>();			
			long currTimeLong = System.currentTimeMillis();
			if (arg0.length!=0)
			{
				/*here*/for (ParseObject obj : arg0[0]) {
					addEvent(currTimeLong, obj);
				}
			}
			return null;
		}

		private void addEvent(long currTimeLong, ParseObject obj) {
			long eventTimeLong = obj.getNumber("date").longValue();
			long timeDeltaLong = eventTimeLong - currTimeLong;
			double milliInHour = 3600000;
			double deltaInHours = (double) (timeDeltaLong / milliInHour);				
			byte[] byArr = null;
			//Check wether to display the event or not according to canAccess specification
			if(!canAccess(obj)){
				return;
			}
			try {
				Bitmap bmp = getBitmap(obj);
				WhoozLocation wLoc = new WhoozLocation(obj.getParseGeoPoint("place"), obj.getString("placeDescription"));
				float dist = getDistanceFromEvent(wLoc);
				boolean entered = false;
				WhoozEvent currentEvent = new WhoozEvent(obj.getString("name"),obj.getString("description"), obj
						.getBoolean("isPrivate"),eventTimeLong, wLoc, bmp, obj.getObjectId(), obj.getJSONArray("takingPart"));
				assignEventToRelevantArrayOfEvents(obj, deltaInHours, dist, entered, currentEvent);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private void assignEventToRelevantArrayOfEvents(ParseObject obj,
				double deltaInHours, float dist, boolean entered,
				WhoozEvent currentEvent) {
			if (deltaInHours > 0) {// future events
				entered =true;
				addEventToList(futureEvents, currentEvent);				
			}
			else if (deltaInHours <= 0 && deltaInHours > -12) {// events now, for 12 hours
				if (dist <= 3000) {
					addEventToList(lookArondEvents, currentEvent);
				} else {// future events
					if(!entered)
						addEventToList(futureEvents, currentEvent);							
				}
			} else if(deltaInHours<0){// events older then 12 hours. to be deleted				
				ParseManager.deleteProcedure(obj);
			}
		}

		private void addEventToList(ArrayList<WhoozEvent> arrayToEnterTo, WhoozEvent eventToEnter)
		{
			arrayToEnterTo.add(eventToEnter);
		}

		private float getDistanceFromEvent(WhoozLocation wLoc) {
			float[] distResults = new float[1];
			Location.distanceBetween(
					MainActivity.userLoc.getLatitude(),
					MainActivity.userLoc.getLongitude(),
					wLoc.getLatitude(), wLoc.getLongitude(),
					distResults);
			float dist = distResults[0];
			return dist;
		}

		private Bitmap getBitmap(ParseObject obj) throws ParseException {
			byte[] byArr;
			Bitmap bmp = null;
			if (obj.getParseFile("coverPhoto") != null) {
				byArr = obj.getParseFile("coverPhoto").getData();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inMutable = true;
				bmp = BitmapFactory.decodeByteArray(byArr, 0,
						byArr.length, options);
			}
			return bmp;
		}

		//This method checks the access permission of the user to the event. The event can be accessed if it is public
		// or the event is private but the user has been invited to the event.
		private boolean canAccess(ParseObject obj){
			JSONArray jsonInvited = obj.getJSONArray(ParseManager.EVENT_INVITED);
			boolean isPrivate = obj.getBoolean(ParseManager.EVENT_PRIVATE);			
			boolean isInvited = false;
			String OwnerId = obj.getString(ParseManager.EVENT_OWNER);
			//String curr = currUser.getId();
			while (OwnerId==null || currUser==null){}
			if(OwnerId.equals(currUser.getId())){
				return true;
			}
			for(int i=0;i<jsonInvited.length();i++){
				try {
					if(jsonInvited.getString(i).equals(currUser.getId())){
						isInvited = true;
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(isPrivate&&!isInvited){
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Void v) {
			canRefresh = true;
			sortArraysByDistance();
			if (fragment_resume != null) {
				if(!onStop){
							dialog.hide();
							FragmentManager fragmentManager = getSupportFragmentManager();
							/*here*/fragmentManager.popBackStack();
							fragmentManager.beginTransaction()
							.replace(R.id.frameContainer, fragment_resume)
							.addToBackStack(null).commit();
							fragment_resume = null;
				}					
			}else if(dialog!=null){				
				dialog.hide();				
			}

			//wait two minutes before loading again
			new java.util.Timer().schedule( 
					new java.util.TimerTask() {
						@Override
						public void run() {
							fullLoad(false);
						}
					}, 
					//120000
					10000//amount of time to wait before can update again - 10 seconds
					);
		}

		private void sortArraysByDistance() {
			Collections.sort(lookArondEvents, new eventsCompare());	
			Collections.sort(futureEvents, new eventsCompare());		
			Collections.sort(invitedToEvts,new eventsCompare());
		}

	}

	public static class eventsCompare implements Comparator<WhoozEvent> {

		@Override
		public int compare(WhoozEvent lhs, WhoozEvent rhs) {
			// TODO Auto-generated method stub

			float compare = WhatsNextAdapter.calcDistance(lhs.getPlace())
					- WhatsNextAdapter.calcDistance(rhs.getPlace());
			if (compare > 0.0000001) {

				return 1;
			} else if (compare < 0.0000001) {
				return -1;

			} else {
				return 0;
			}
		}

	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);

		}
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 0:
			setFragment_resume(null);
			fragment = new WhoozMainActivity(this);
			break;
		case 1:
			dialog=new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
			dialog.setMessage("loading profile events...");
			dialog.setCancelable(false);
			dialog.setInverseBackgroundForced(false);
			dialog.setProgressStyle(R.drawable.jkb);
			dialog.setTitle("Whooz");
			dialog.show();
			loadEventsAgain = true;
			fragment_resume = null;
			loadProflileEvents();
			return;			
		case 2:
			fragment = new AroundMeFragment(this);					
			break;
		case 3:
			fragment = new WhatsNextFragment(this);						
			break;
		case 4:
			fragment_resume = null;
			fragment = new CreateEventFragment();
			break;
		case 5:
			Session session = Session.getActiveSession();
			DrawerLayout dr = (DrawerLayout) findViewById(R.id.drawer_layout);
			dr.closeDrawers();
			dr.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			if (session != null) {

				if (!session.isClosed()) {
					session.closeAndClearTokenInformation();
					// clear your preferences if saved
				}
			} else {

				session = new Session(context);
				Session.setActiveSession(session);
				session.closeAndClearTokenInformation();
				// clear your preferences if saved

			}

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frameContainer, fragment)
			.addToBackStack(null).commit();

			// update selected item and title, then close the drawer
			drawerList.setItemChecked(position, true);
			drawerList.setSelection(position);
			drawerLayout.closeDrawer(drawerList);

		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	public void setFragment_resume(Fragment resume) {
		fragment_resume = resume;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}


	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		// Only make changes if the activity is visible
		if (isResumed) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			if (state.isOpened()) {
				makeMeRequest(session);
				// If the session state is open:
				// Show the authenticated page
				loadWhoozMainActivity(fragmentManager);
			} else {
				loadLoginFragment(fragmentManager);
			}
		}
	}

	private void loadLoginFragment(FragmentManager fragmentManager) {
		Fragment fragment = new LoginFragment();
		fragmentManager.beginTransaction()
		.replace(R.id.frameContainer, fragment, "login")
		.commit();
	}

	private void loadWhoozMainActivity(FragmentManager fragmentManager) {
		fullLoad(true);
		Fragment fragment = new WhoozMainActivity(this);
		fragmentManager.beginTransaction()
		.replace(R.id.frameContainer, fragment, "main")
		.commit();
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();
		FragmentManager fragmentManager = getSupportFragmentManager();
		if (session != null && session.isOpened()) {
			// if the session is already open,
			// try to show the selection fragment
			// Fragment fragment = new WhoozMainActivity();
			// fragmentManager.beginTransaction().replace(R.id.frameContainer,
			// fragment).commit();
		} else {
			loadLoginFragment(fragmentManager);
		}
	}

	public void makeMeRequest(final Session session) {
		// Make an API call to get user data and define a
		// new callback to handle the response.
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						// Set the id for the ProfilePictureView
						// view that in turn displays the profile
						// picture.
						currUser = user;
						saveUserToParse();
						if (checkUser) {
							ParseManager.addUser(currUser);
							checkUser = false;
						}
						if(!checked){
							ParseManager.checkFriendRequest(context);
							checked =true;
						}
						Request.newMyFriendsRequest(session, new GraphUserListCallback() {
							@Override
							public void onCompleted(List<GraphUser> users, Response response) {
								users.size(); 
								if (users != null) {
									List<String> friendsList = new ArrayList<String>();
									for (GraphUser user : users) {
										friendsList.add(user.getId());
									}
								}
							}
						}).executeAsync();
						initNavagationDrawer(user);
					}
				}
				if (response.getError() != null) {
					// Handle errors, will do so later.
				}
			}

			private void initNavagationDrawer(GraphUser user) {
				navDrawItems = new ArrayList<NavDrawerItem>();
				addTheNavagationOptions(user);
				DrawerLayout dr = (DrawerLayout) findViewById(R.id.drawer_layout);
				dr.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				// set navigation drawer adapter
				navDrawerTitles = getResources()
						.getStringArray(R.array.navDrawerTitles);
				drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
				drawerList = (ListView) findViewById(R.id.left_drawer);

				drawerList.setAdapter(new DrawerAdapter(
						context, navDrawItems));
				// end navigation drawer
				drawerList
				.setOnItemClickListener(new SlideMenuClickListener());
			}

			private void addTheNavagationOptions(GraphUser user) {
				navDrawItems.add(new NavDrawerItem("Whooz",
						R.drawable.jkb));

				navDrawItems.add(new NavDrawerItem(user
						.getName(), user.getId()));
				navDrawItems.add(new NavDrawerItem("Around Me",
						R.drawable.green));
				navDrawItems.add(new NavDrawerItem(
						"What's Next", R.drawable.yellow));
				navDrawItems.add(new NavDrawerItem(
						"Create Event", R.drawable.red));
				navDrawItems.add(new NavDrawerItem("Logout",
						R.drawable.ic_launcher));
			}

			private void saveUserToParse() {
				ParseInstallation installation = ParseInstallation
						.getCurrentInstallation();
				installation.put("userId", currUser.getId());
				installation.saveInBackground();
			}
		});
		Request.executeBatchAsync(request);
	}

	public static void setUserLoc(Location loc) {
		userLoc = loc;
	}	


	public void loadProflileEvents() {

		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseManager.USER_CLASS_NAME);

		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());
		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> users, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					InsertProfileEvents insEv = new InsertProfileEvents();
					insEv.execute(users);					
				}
			}
		});
	}


	private class InsertProfileEvents extends AsyncTask<List<ParseObject>, Void, Void> {

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
			MainActivity.dialog.hide();
			Intent in = new Intent(that,ProfileMainActivityO.class);						
			startActivity(in);
		}

	}

	private void loadInvitedToEvents(ParseObject user){		

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
		//ParseQuery<ParseObject> parseEvents;
		String arrStr = getClearStringArray(arr);
		String[] spltArr = arrStr.split(",");
		for (int i = 0; i < spltArr.length; i++) {
			addEventToQuery(invitedToEventsQuery, spltArr, i);
		}
		ParseQuery<ParseObject> mainQuery = ParseQuery
				.or(invitedToEventsQuery);
		return mainQuery;
	}

	private String getClearStringArray(JSONArray arr) {
		String arrStr = arr.toString();
		arrStr = arrStr.replace("\"", "");
		arrStr = arrStr.replace("[", "");
		arrStr = arrStr.replace("]", "");
		return arrStr;
	}

	private void addEventToQuery(
			List<ParseQuery<ParseObject>> invitedToEventsQuery,
			String[] spltArr, int i) {
		ParseQuery<ParseObject> parseEvents;
		int evtId = Integer.parseInt(spltArr[i]);
		parseEvents = ParseQuery.getQuery("Events");
		parseEvents.whereEqualTo(ParseManager.EVENT_ID, evtId);
		invitedToEventsQuery.add(parseEvents);
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

	//This method is responsible for loading all the app data (all the events according to thier type)
	public void fullLoad(boolean showDialog) {
		if (showDialog) {
			dialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
			dialog.setMessage("Get ready to party...");
			dialog.setCancelable(false);
			dialog.setInverseBackgroundForced(false);
			dialog.setProgressStyle(R.drawable.jkb);
			dialog.setTitle("Whooz");
			dialog.show();
			dialog.dismiss();
		}		
		load(fragment_resume);		
	}
	public static void updateGPSLoc(Context con){
		GPSTracker gps = new GPSTracker(con);
		if (gps.canGetLocation()) {
			userLoc.setLongitude(gps.getLongitude());
			userLoc.setLatitude(gps.getLatitude());
		}else{
			Toast.makeText(con, "can't get location. using last location or default location(jerusalem)", Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public void onStop() {
		super.onStop();
		onStop=true;

	}
	@Override
	public void onStart(){
		super.onStart();
		onStop=false;
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		onStop=false;
		isResumed = true;
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		onStop=true;
		isResumed = false;
	}


}
