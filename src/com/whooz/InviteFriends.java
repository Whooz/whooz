package com.whooz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * 
 */
public class InviteFriends extends Activity {
	public static ArrayList<String> invitedFriends = new ArrayList<String>();;
	private ArrayList<String> allFriends = new ArrayList<String>();;
	private InviteFriends that = this;
	public InviteFriends() {
		// Required empty public constructor
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_invite_friends);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseManager.USER_CLASS_NAME);		
		query.whereEqualTo(ParseManager.USER_ID, MainActivity.currUser.getId());
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> users, ParseException e) {
				final ParseObject user=users.get(0);
				JSONArray friends = user.getJSONArray(ParseManager.USER_FRIENDS);
				for(int i=0;i<friends.length();i++){
					try {
						allFriends.add(friends.getString(i));
					} catch (JSONException ex) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ListView friendsList = (ListView)findViewById(
						R.id.selection_list);
				//usersList.setRotation(90);
				ListAdapter friendsAdapter = new FriendsAdapter(that,
						R.layout.friends_item, allFriends);
				friendsList.setAdapter(friendsAdapter);
			}
		});			

		
		
		
		Button ok = (Button) findViewById(R.id.ok_invite);		
		
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
						        
		        Intent returnIntent = new Intent();
//				returnIntent.putExtra("dateMilli",dateVal);
//				returnIntent.putExtra("dateStr", dateText);
//				returnIntent.putExtra("hour", hour);
//				returnIntent.putExtra("minute", minute);
				
				setResult(RESULT_OK,returnIntent);
				finish();
			}
		});			
	}



}
