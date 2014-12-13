package com.whooz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;



import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class EventFragment extends Fragment {

	private Bitmap imageToShow;
	private ImageView coverPhoto;
	private ArrayList<WhoozComment> comments ;
	private ArrayList<String> usersIds; 
	private CommentsAdapter commAdap;
	private WhoozEvent curEvent;
	private final int DELETE = 1;

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		MainActivity.fragment_resume = null;
		super.onActivityCreated(savedInstanceState);			
		if(MainActivity.curEventLayoutIsLookAround){
			curEvent  = MainActivity.lookArondEvents.get(AroundMeFragment.curPosPressed);
		}
		else if(MainActivity.curEventLayoutIsInvitedTo){
			curEvent = MainActivity.invitedToEvts.get(InvitedToEventsFragment.curPosPressed);
		}
		else{
			/*here*/curEvent  = MainActivity.futureEvents.get(WhatsNextFragment.curPosPressed);
		}


		JSONArray users = curEvent.getUsers();
		usersIds = new ArrayList<String>();
		for(int i = 0;i<users.length();i++){
			try {
				usersIds.add(users.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ListView usersList = (ListView) getView().findViewById(
				R.id.eve_participants);
		//usersList.setRotation(90);
		ListAdapter usersAdapter = new UserAadapter(getActivity(),
				R.layout.event_participant_item, usersIds);
		usersList.setAdapter(usersAdapter);

		usersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				Fragment fragment = new ProfileFragment(usersIds.get(position));
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.frameContainer, fragment)
				.addToBackStack(null).commit();
			}
		});

		coverPhoto = (ImageView) getView().findViewById(R.id.eve_cover_photo);
		TextView eveDescr = (TextView) getView().findViewById(R.id.eve_desc);
		TextView eveHeader = (TextView) getView().findViewById(R.id.eve_header);
		final EditText newComment = (EditText) getView().findViewById(R.id.event_newcomment);
		Button addComment = (Button) getView().findViewById(R.id.eve_addnewcomment);
		ListView eveComments = (ListView) getView().findViewById(R.id.eve_comments);		
		comments = new ArrayList<WhoozComment>();
		ParseQuery<ParseObject> getComments = ParseQuery.getQuery("Comments");


		getComments.whereEqualTo("eventId", curEvent.getObjectId());
		getComments.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {

				for(ParseObject obj : arg0){
					WhoozComment com = new WhoozComment(obj.getString("comment"), obj.getString("ownerId"),obj.getString("ownerName"));
					comments.add(com);
					commAdap.notifyDataSetChanged();
				}


			}


		});


		commAdap = new CommentsAdapter(getActivity(), R.layout.event_fragment_item, comments);

		addComment.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!newComment.getText().toString().equals("")){
					String userCom = newComment.getText().toString();
					String ownerId = MainActivity.currUser.getId();
					String ownerName = MainActivity.currUser.getFirstName() + " " + MainActivity.currUser.getLastName();
					comments.add(new WhoozComment(userCom,ownerId,ownerName));
					final int position = comments.size()-1;
					newComment.setText("");
					commAdap.notifyDataSetChanged();
					final ParseObject newComObj = new ParseObject("Comments");
					newComObj.put("comment", userCom);
					newComObj.put("ownerId", ownerId);
					newComObj.put("ownerName", ownerName);
					newComObj.put("eventId",curEvent.getObjectId());
					newComObj.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException arg0) {
							// TODO Auto-generated method stub
							comments.get(position).setObjectId(newComObj.getObjectId());
						}
					});
				}

			}
		});
		//Get the display dimensions
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);


		//Create the new image to be shown using the new dimensions 
		/*Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = (width*curEvent.getPic().getHeight())/curEvent.getPic().getWidth();
		Bitmap imageToShow = Bitmap.createScaledBitmap(curEvent.getPic(), width, height, true);
		
		coverPhoto.setImageBitmap(imageToShow);*/
		coverPhoto.setScaleType(ScaleType.CENTER_CROP);
		coverPhoto.setImageBitmap(curEvent.getPic());
		System.gc();

		eveDescr.setText("\""+curEvent.getDescrip()+"\"");
		eveDescr.setTextSize(15);
		eveHeader.setText(curEvent.getHeader() + " - " + curEvent.getPlace().getDescription());
		eveHeader.setTextSize(18);
		eveComments.setOnTouchListener(new OnTouchListener() {


			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		eveComments.setAdapter(commAdap);
		registerForContextMenu(eveComments );


	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		return inflater.inflate(R.layout.event_fragment, container, false);
	}
	@Override
	public void onPause() {
		super.onPause();

		if(imageToShow!=null){
			imageToShow.recycle();
			imageToShow=null;
		}
		System.gc();


	}

	@Override
	public void onCreateContextMenu(ContextMenu menu,
			View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo Info = (AdapterContextMenuInfo) menuInfo;

		Log.i("******************curUser", MainActivity.currUser.getId());
		Log.i("******************userIDcomm", comments.get(Info.position).getUserId());
		if(comments.get(Info.position).getUserId().equals(MainActivity.currUser.getId())){

			menu.add(0, 1, 0,"delete");
		}
	}




	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case DELETE:
			AdapterContextMenuInfo Info = (AdapterContextMenuInfo) item.getMenuInfo();
			String objectId = comments.get(Info.position).getObjectId();
			comments.remove(Info.position);
			commAdap.notifyDataSetChanged();

			ParseQuery<ParseObject> q = ParseQuery.getQuery("Comments");
			q.whereEqualTo("objectId", objectId);
			q.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> arg0, ParseException arg1) {

					arg0.get(0).deleteInBackground();
				}
			});
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}	
}
