package com.whooz;

import java.util.ArrayList;
import java.util.List;

import com.facebook.widget.ProfilePictureView;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;


public class UserAadapter extends ArrayAdapter<String> {
	private Context context;
	private ArrayList<String> users;

	public UserAadapter(Context context, int resource,
			List<String> values) {
		super(context, resource, values);
		this.context =context;
		this.users = (ArrayList<String>) values;


	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub


		View view = convertView;
		String currUser = users.get(position);
		if (view == null) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.event_participant_item, parent,false);
			ProfilePictureView profilePictureView = (ProfilePictureView) view.findViewById(R.id.profile);
			profilePictureView.setCropped(true);
			profilePictureView.setProfileId(currUser);
			//profilePictureView.setRotation(-90);
			
		}
		return view;
	}
}
