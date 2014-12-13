package com.whooz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.whooz.MainActivity.eventsCompare;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class InvitedToEventsFragment extends Fragment {
	static int curPosPressed;
	public static boolean refreshed=false;
	private ArrayAdapter<WhoozEvent> eventsListAdap=null;
	private SwipeRefreshLayout swipeLayout;

	public InvitedToEventsFragment(ProfileMainActivityO profileMainActivity) {
		super();
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// refreshContent();
		super.onActivityCreated(savedInstanceState);
		ActionBar bar = getActivity().getActionBar();
		bar.setTitle("My Area");
		System.gc();
		MainActivity.curEventLayoutIsLookAround = false;
		MainActivity.curEventLayoutIsWhatsNext=false;
		MainActivity.curEventLayoutIsInvitedTo=true;
		ListView eventsList = (ListView) getView().findViewById(
				R.id.invited_to_events);
		eventsListAdap = new WhatsNextAdapter(getActivity(),
				R.layout.whats_next_item, MainActivity.invitedToEvts);
		eventsList.setAdapter(eventsListAdap);
		eventsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*curPosPressed = position-1;
			    if(curPosPressed<0){
			    	curPosPressed = MainActivity.invitedToEvts.size() -1;
			    }*/
				curPosPressed = position;
				Toast.makeText(getActivity(), MainActivity.invitedToEvts.get(position).getHeader(), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(),ShowEventFromInvitedActivity.class);
				startActivity(intent);
			}
		});

		
		swipeLayout = (SwipeRefreshLayout) getView()
				.findViewById(R.id.swipe_container_next);
		swipeLayout
		.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshContent();
			}
		});
		
		
		refreshContent();
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.invited_to_events_frag,
				container, false);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		MainActivity.dialog.dismiss();
		System.gc();

	}

	private void refreshContent() {
		MainActivity.dialog.dismiss();
		MainActivity.dialog = new ProgressDialog(getActivity(),
				ProgressDialog.THEME_HOLO_DARK);
		MainActivity.dialog.setMessage("loading events ...");
		MainActivity.dialog.setCancelable(false);
		MainActivity.dialog.setInverseBackgroundForced(false);
		MainActivity.dialog.setProgressStyle(R.drawable.jkb);
		MainActivity.dialog.setTitle("Whooz");
		MainActivity.dialog.show();
		//profileMainActivity.loadProflileEvents();
		load();
		swipeLayout.setRefreshing(false);
		eventsListAdap.clear();
		eventsListAdap.addAll(MainActivity.invitedToEvts);
		eventsListAdap.notifyDataSetChanged();

	}

	private void load() {
		if(!MainActivity.canRefresh){
			return;
		}
		MainActivity.canRefresh = false;
		MainActivity.updateGPSLoc(getActivity());
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseManager.USER_CLASS_NAME);
		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());

		query.findInBackground(new FindCallback<ParseObject>() {

			@SuppressWarnings("unchecked")
			@Override
			public void done(List<ParseObject> users, ParseException arg1) {
				InsertEvents insEv = new InsertEvents();
				insEv.execute(users);
			}

		});

	}

	public static void loadEvents() {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseManager.USER_CLASS_NAME);

		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());
		ParseObject user;
		try {
			user = query.find().get(0);
			JSONArray arr = user
					.getJSONArray(ParseManager.USER_INVITED_TO_EVENTS);
			MainActivity.invitedToEvts = new ArrayList<WhoozEvent>();
			if (arr == null) {
				return;
			}
			if (arr.length() == 0) {
				return;
			}
			ParseQuery<ParseObject> mainQuery = getEventsQuery(arr);
			List<ParseObject> events = mainQuery.find();
			for (ParseObject evt : events) {

				MainActivity.invitedToEvts.add(extractEvent(evt));
			}
			// MainActivity.dialog.hide();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}






	private class InsertEvents extends AsyncTask<List<ParseObject>, Void, Void> {

		@Override
		protected Void doInBackground(List<ParseObject>... users) {
			ParseObject user = users[0].get(0);
			JSONArray arr = user
					.getJSONArray(ParseManager.USER_INVITED_TO_EVENTS);
			MainActivity.invitedToEvts = new ArrayList<WhoozEvent>();
			if (arr == null) {
				return null;
			}
			if (arr.length() == 0) {
				return null;
			}

			List<ParseObject> events=null;
			ParseQuery<ParseObject> eventsQuery = getEventsQuery(arr);
			try {
				events = eventsQuery.find();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(events!=null){
				for (ParseObject evt : events) {
					MainActivity.invitedToEvts.add( extractEvent(evt));				
				}
			}

			return null;
		}


		@Override
		protected void onPostExecute(Void v) {
			MainActivity.canRefresh = true;
			Collections.sort(MainActivity.invitedToEvts, new eventsCompare());				
			/*Fragment fragment = new InvitedToEventsFragment(profileMainActivity);
			MainActivity.dialog.hide();
			FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
			.replace(R.id.pager,fragment )
			.addToBackStack(null).commit();	*/
			MainActivity.dialog.hide();
			eventsListAdap.notifyDataSetChanged();
		}

	}


	public static WhoozEvent extractEvent(ParseObject evt){
		byte[] byArr = null;

		Bitmap bmp = null;
		if (evt.getParseFile("coverPhoto") != null) {
			try {
				byArr = evt.getParseFile("coverPhoto").getData();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inMutable = true;
			bmp = BitmapFactory.decodeByteArray(byArr, 0, byArr.length,
					options);
		}
		long eventTimeLong = evt.getNumber("date").longValue();
		WhoozLocation wLoc = new WhoozLocation(
				evt.getParseGeoPoint("place"),
				evt.getString("placeDescription"));
		WhoozEvent event = new WhoozEvent(evt
				.getString("name"), evt.getString("description"), evt
				.getBoolean("isPrivate"), eventTimeLong, wLoc, bmp, evt
				.getObjectId(), evt.getJSONArray("takingPart"));

		return event;
	}


	public static ParseQuery<ParseObject> getEventsQuery(JSONArray arr){
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





}





