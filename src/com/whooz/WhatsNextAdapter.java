package com.whooz;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.location.Location;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class WhatsNextAdapter extends ArrayAdapter<WhoozEvent> {

	private Context context;
	private ArrayList<WhoozEvent> events;

	public WhatsNextAdapter(Context context, int resource,
			List<WhoozEvent> values) {
		super(context, resource, values);
		this.context =context;
		this.events = (ArrayList<WhoozEvent>) values;


	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub


		View view = convertView;
		viewHolder myHold;
		WhoozLocation wLoc = events.get(position).getPlace();
		
		float dist = calcDistance(wLoc);

		if (view == null) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.whats_next_item, parent,false);

			myHold =  new viewHolder();
			//			myHold.pictures[0] = (ProfilePictureView) view.findViewById(R.id.pic1);
			//			myHold.pictures[1] = (ProfilePictureView) view.findViewById(R.id.pic2);
			//			myHold.pictures[2] = (ProfilePictureView) view.findViewById(R.id.pic3);
			//			myHold.pictures[3] = (ProfilePictureView) view.findViewById(R.id.pic4);
			//			myHold.pictures[4] = (ProfilePictureView) view.findViewById(R.id.pic5);

			//myHold.placeName = (TextView) view.findViewById(R.id.placeName);
			myHold.img = (ImageView) view.findViewById(R.id.eventCoverphoto);
			myHold.desc = (TextView) view.findViewById(R.id.desc);
			//myHold.partic = (LinearLayout) view.findViewById(R.id.peoplesInEve);
			myHold.distance = (TextView) view.findViewById(R.id.distance);
			//myHold.timeAndDate = (TextView) view.findViewById(R.id.LATimeAndDate);
			//myHold.checkinBtt = (Button)view.findViewById(R.id.checkin);
			myHold.header = (TextView) view.findViewById(R.id.header);
			//myHold.evePepole = (LinearLayout) view.findViewById(R.id.peoplesInEve);
			myHold.placeAndDate=(TextView) view.findViewById(R.id.placeAndDate);
			view.setTag(myHold);
		}
		else{
			myHold =(viewHolder) view.getTag();
		}
		/*myHold.evePepole.removeAllViews();
		myHold.checkinBtt.setVisibility(View.INVISIBLE);
		myHold.checkinBtt.setText("Check In");
		myHold.checkinBtt.setBackgroundResource(R.drawable.checkinbtt);
		myHold.checkinBtt.setClickable(true);
		myHold.checkinBtt.setEnabled(true);
		JSONArray users = events.get(position).getUsers();
		for(int i = 0;i<5;i++){
			if(i>=users.length()){
				break;
			}
			try {
				
                ProfilePictureView pic = new ProfilePictureView(context);
                pic.setPresetSize(ProfilePictureView.SMALL);                
				pic.setProfileId(users.getString(i));
				myHold.evePepole.addView(pic);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(events.get(position).getUsers().toString().contains(MainActivity.currUser.getId())){
			myHold.checkinBtt.setClickable(false);
			myHold.checkinBtt.setEnabled(false);
			myHold.checkinBtt.setBackgroundResource(R.drawable.checkedinbtt);
			myHold.checkinBtt.setText("");
		}
		if(dist<0.2){
			myHold.checkinBtt.setVisibility(View.VISIBLE);
			
		}



		myHold.checkinBtt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				events.get(position).addUser(MainActivity.currUser.getId());
				ParseQuery<ParseObject> q = ParseQuery.getQuery("Events");
				q.getInBackground(events.get(position).getObjectId(), new GetCallback<ParseObject>() {

					@Override
					public void done(ParseObject arg0, ParseException arg1) {
						JSONArray evUsers = arg0.getJSONArray("takingPart");
						
						evUsers.put(MainActivity.currUser.getId());
						arg0.put("takingPart",evUsers );
						arg0.saveInBackground();
						AroundMeAdapter.this.notifyDataSetChanged();
					}
				});
			}
		});*/

		if(events.get(position).getPic()!=null){
			myHold.img.setImageBitmap(events.get(position).getPic());

		}
		myHold.header.setText((events.get(position)).getHeader());
		//myHold.placeName.setText("("+wLoc.getDescription()+")");
		myHold.desc.setText(events.get(position).getDescrip());
		myHold.distance.setText(String.format("%.2f",dist)+"km");
		Date date = new Date(events.get(position).getDate());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		String dateText = sdf.format(date);

		Log.i("*////////**********",events.get(position).getUsers().toString());

		int hour = events.get(position).getHour();
		int minute = events.get(position).getMinute();

		String hourS = Integer.toString(hour);
		String minS = Integer.toString(minute);
		if(hour<=9)
		{

			hourS = "0"+hourS;
		}

		if(minute<=9)
		{

			minS = "0"+minS;
		}
		String place = events.get(position).getPlace().getDescription();
		String placeDateTime = "At "+place+"\nOn "+dateText+" "+hourS+":"+minS;
		myHold.placeAndDate.setText(placeDateTime);


		//distace.setText(Double.toString(events.get(position).getPlace().getLocation().getLatitude()));
		//		if(events.get(position).getPlace()!=null){
		//			place.setText(events.get(position).getPlace().getName());
		//		}
		//		else{
		//			place.setText("");
		//		}
		//myHold.timeAndDate.setText(dateText+" "+hourS+":"+minS);


		//take some pictures
		return view;
	}

	static float calcDistance(WhoozLocation wLoc){
		float[] distResults= new float[1];
		Location.distanceBetween(MainActivity.userLoc.getLatitude(), MainActivity.userLoc.getLongitude(), wLoc.getLatitude(), wLoc.getLongitude(), distResults);
		float dist = distResults[0]/1000;
		return dist;
	}
	private static class viewHolder{
		public ImageView img ;
		public TextView desc ;
		public TextView distance;
		public TextView header ;
		public TextView placeAndDate;



	}
}
