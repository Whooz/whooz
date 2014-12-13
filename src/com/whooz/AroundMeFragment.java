package com.whooz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.whooz.MainActivity.eventsCompare;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link AroundMeFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link AroundMeFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
@SuppressLint("ValidFragment")
public class AroundMeFragment extends Fragment {

	private MainActivity mainActivity;
	private ArrayAdapter<WhoozEvent> eventsListAdap = null;
	private SwipeRefreshLayout swipeLayout;
	public AroundMeFragment(MainActivity mainActivity) {
		super();
		this.mainActivity = mainActivity;
	}

	static int curPosPressed;
	public static void setRefresh(boolean refresh) {
	}

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Fragment fragment = new AroundMeFragment(mainActivity);
		mainActivity.setFragment_resume(fragment);
		String s = "<b>Look Around</b>";
		String footer = "Events around you now";
		ColorDrawable green = new ColorDrawable(0xFF29d329);
		ActionBar bar = getActivity().getActionBar();
		bar.setBackgroundDrawable(green);
		bar.setTitle(Html.fromHtml(s));
		bar.setSubtitle(footer);
		System.gc();
		//mainActivity.setAroundMe(true);
        MainActivity.curEventLayoutIsLookAround = true;
        MainActivity.curEventLayoutIsInvitedTo=false;
        MainActivity.curEventLayoutIsWhatsNext=false;
		ListView eventsList = (ListView) getView().findViewById(
				R.id.eventsAround);
		eventsListAdap = new LookAroundAdapter(getActivity(),
				R.layout.look_around_item, MainActivity.lookArondEvents);

		eventsList.setAdapter(eventsListAdap);

		eventsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				curPosPressed = position;
				
				Fragment fragment = new EventFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.addToBackStack(null).commit();
			}
		});
		swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_container_around);
		swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshContent(getView());
				swipeLayout.setRefreshing(false);
				
			}
		});        


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.look_around_frag, container,
				false);
		//refreshContent(view);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		System.gc();

	}

	private void refreshContent(View view) {
//		mainActivity.setAroundMe(true);
//
//		mainActivity.setAroundMe(true);
//		Fragment fragment = new AroundMeFragment(mainActivity);
//		GPSTracker gps = new GPSTracker(getActivity());
//		if (gps.canGetLocation()) {
//			MainActivity.userLoc.setLongitude(gps.getLongitude());
//			MainActivity.userLoc.setLatitude(gps.getLatitude());
//		}
//		mainActivity.setFragment_resume(fragment);
		MainActivity.dialog = new ProgressDialog(getActivity(),
				ProgressDialog.THEME_HOLO_DARK);
		MainActivity.dialog.setMessage("loading events arond you...");
		MainActivity.dialog.setCancelable(false);
		MainActivity.dialog.setInverseBackgroundForced(false);
		MainActivity.dialog.setProgressStyle(R.drawable.jkb);
		MainActivity.dialog.setTitle("Whooz");
		MainActivity.dialog.show();
		load();
		

	}
	
	
	
	public void load() {	
		if(!MainActivity.canRefresh){
			return;
		}
		MainActivity.canRefresh = false;
		MainActivity.updateGPSLoc(getActivity());
		ParseQuery<ParseObject> parseEvents = ParseQuery.getQuery("Events");
		parseEvents.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				InsertEvents insEv = new InsertEvents();
				insEv.execute(arg0);
			}

		});
	
}

private class InsertEvents extends AsyncTask<List<ParseObject>, Void, Void> {

	@Override
	protected Void doInBackground(List<ParseObject>... arg0) {
		MainActivity.lookArondEvents = new ArrayList<WhoozEvent>();
		MainActivity.futureEvents = new ArrayList<WhoozEvent>();			
		long currTimeLong = System.currentTimeMillis();
		for (ParseObject obj : arg0[0]) {
			long eventTimeLong = obj.getNumber("date").longValue();
			long timeDeltaLong = eventTimeLong - currTimeLong;
			double milliInHour = 3600000;
			double deltaInHours = (double) (timeDeltaLong / milliInHour);
			obj.getString("name");
			
			
			
			byte[] byArr = null;
			//Check wether to display the event or not according to canAccess specification
			if(!canAccess(obj)){
				continue;
			}
			try {
				Bitmap bmp = null;
				if (obj.getParseFile("coverPhoto") != null) {
					byArr = obj.getParseFile("coverPhoto").getData();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inMutable = true;
					bmp = BitmapFactory.decodeByteArray(byArr, 0,
							byArr.length, options);
				}

				WhoozLocation wLoc = new WhoozLocation(
						obj.getParseGeoPoint("place"),
						obj.getString("placeDescription"));
				float[] distResults = new float[1];
				Location.distanceBetween(
						MainActivity.userLoc.getLatitude(),
						MainActivity.userLoc.getLongitude(),
						wLoc.getLatitude(), wLoc.getLongitude(),
						distResults);
				float dist = distResults[0];

				boolean entered = false;
				if (deltaInHours > 0) {// future events
					entered =true;
				
					MainActivity.futureEvents.add(new WhoozEvent(obj.getString("name"),
							obj.getString("description"), obj
							.getBoolean("isPrivate"),
							eventTimeLong, wLoc, bmp, obj.getObjectId(),
							obj.getJSONArray("takingPart")));						
				}
				if (deltaInHours <= 0 && deltaInHours > -12) {// events now, for 12 hours
					if (dist <= 2000) {

						MainActivity.lookArondEvents.add(new WhoozEvent(obj
								.getString("name"), obj
								.getString("description"), obj
								.getBoolean("isPrivate"), eventTimeLong,
								wLoc, bmp, obj.getObjectId(), obj
								.getJSONArray("takingPart")));
					} else {// future events
						if(!entered)
							MainActivity.futureEvents.add(new WhoozEvent(obj
								.getString("name"), obj
								.getString("description"), obj
								.getBoolean("isPrivate"), eventTimeLong,
								wLoc, bmp, obj.getObjectId(), obj
								.getJSONArray("takingPart")));							
					}
				} else if(deltaInHours<0){// events older then 12 hours. to be deleted
					//obj.deleteInBackground();
					ParseManager.deleteProcedure(obj);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;

	}

	//This method checks the access permission of the user to the event. The event can be accessed if it is public
	// or the event is private but the user has been invited to the event.
	private boolean canAccess(ParseObject obj){
		JSONArray jsonInvited = obj.getJSONArray(ParseManager.EVENT_INVITED);
		boolean isPrivate = obj.getBoolean(ParseManager.EVENT_PRIVATE);			
		boolean isInvited = false;
		String OwnerId = obj.getString(ParseManager.EVENT_OWNER);
		if(OwnerId.equals(MainActivity.currUser.getId())){
			return true;
		}
		for(int i=0;i<jsonInvited.length();i++){
			try {
				if(jsonInvited.getString(i).equals(MainActivity.currUser.getId())){
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
		MainActivity.canRefresh = true;
		Collections.sort(MainActivity.lookArondEvents, new eventsCompare());	
		Collections.sort(MainActivity.futureEvents, new eventsCompare());			
			/*Fragment fragment = new AroundMeFragment(mainActivity);
			MainActivity.dialog.hide();
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
					.replace(R.id.frameContainer,fragment )
					.addToBackStack(null).commit();*/
		MainActivity.dialog.hide();
		eventsListAdap.notifyDataSetChanged();
	}

}
	

}
