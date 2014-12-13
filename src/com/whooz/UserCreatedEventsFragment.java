package com.whooz;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ValidFragment")
public class UserCreatedEventsFragment extends Fragment {
	static int curPosPressed;
	public UserCreatedEventsFragment(ViewPager viewPager, CreateEventFragment eventFragment, ProfileMainActivity activity) {
		super();
	}
	public UserCreatedEventsFragment(){
		super();
	}

	public static void setRefresh(boolean refresh) {
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		// refreshContent();

		super.onActivityCreated(savedInstanceState);

		System.gc();
		MainActivity.curEventLayoutIsLookAround = false;
		ListView eventsList = (ListView) getView().findViewById(
				R.id.user_created_events);
		if(eventsList==null){
			return;
		}
		ListAdapter eventsListAdap = new MyEventsAdapter(getActivity(),
				R.layout.user_created_events_item, MainActivity.userCreatedEvents);
		eventsList.setAdapter(eventsListAdap);
		eventsList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WhoozEvent currEvent = MainActivity.userCreatedEvents.get(position);
				
				
				Intent intent = new Intent(getActivity(),UpdateEventFragment.class);
				intent.putExtra("name",currEvent.getHeader());
				intent.putExtra("place",currEvent.getPlace().getDescription());
				intent.putExtra("isPrivate",currEvent.getIsPrivate());
				intent.putExtra("date",currEvent.getDate());
				intent.putExtra("info",currEvent.getDescrip());
				Bitmap b = currEvent.getPic();//adding image as byteArray. otherwise intent won't start
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				b.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				intent.putExtra("pic",byteArray);
				intent.putExtra("id",currEvent.getId());
				intent.putExtra("selectedFriends",currEvent.getUsers().toString());
				intent.putExtra("latitude",currEvent.getPlace().getLatitude());
				intent.putExtra("longitude", currEvent.getPlace().getLongitude());
				intent.putExtra("placeDesc",currEvent.getPlace().getDescription());
				intent.putExtra("objId", currEvent.getObjectId());
				startActivity(intent);

			
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.user_created_events_frag,
				container, false);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		System.gc();

	}

	public static void loadEvents() {
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery(ParseManager.USER_CLASS_NAME);

		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());
		ParseObject user;
		try {
			user = query.find().get(0);
			JSONArray arr = user.getJSONArray(ParseManager.USER_CREATED_EVENTS);
			if (arr == null) {
				return;
			}
			if (arr.length() == 0) {
				return;
			}
			MainActivity.userCreatedEvents = new ArrayList<WhoozEvent>();
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
			List<ParseObject> events = mainQuery.find();
			for (ParseObject evt : events) {

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
				MainActivity.userCreatedEvents.add(new WhoozEvent(evt
						.getString("name"), evt.getString("description"), evt
						.getBoolean("isPrivate"), eventTimeLong, wLoc, bmp, evt
						.getObjectId(), evt.getJSONArray("takingPart")));
			}
			// MainActivity.dialog.hide();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
