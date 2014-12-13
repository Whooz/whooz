package com.whooz;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class FriendsAdapter extends ArrayAdapter<String>{
	private ArrayList<String> friends;
	private Context context;
	public FriendsAdapter(Context context, int resource,
			List<String> values) {
		super(context, resource, values);		
		this.friends = (ArrayList<String>) values;
		this.context =context;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub


		View view=convertView;;
		String currUser = friends.get(position);
		if(view==null){

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.friends_item, parent,false);
			final TextView name = (TextView)view.findViewById(R.id.selection_friend_name);
			Button selectBtt = (Button)view.findViewById(R.id.selection_friend_select);
			selectBtt.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					InviteFriends.invitedFriends.add(friends.get(position));
				}
			});
			ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.friend_profile_pic);
			profilePictureView.setCropped(true);
			profilePictureView.setProfileId(currUser);
			ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseManager.USER_CLASS_NAME);
			
			query.whereEqualTo(ParseManager.USER_ID, currUser);
			query.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> users, ParseException e) {
					ParseObject user=users.get(0);
					name.setText(user.getString(ParseManager.USER_FIRST_NAME)+" "+user.getString(ParseManager.USER_SECOND_NAME));
			//profilePictureView.setRotation(-90);
				}
			});
		}	
		return view;
	}
}
