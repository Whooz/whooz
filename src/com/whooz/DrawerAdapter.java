package com.whooz;


import java.util.ArrayList;

import com.facebook.widget.ProfilePictureView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DrawerAdapter extends BaseAdapter {




	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;

	public DrawerAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {       
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position!=1){
			if (convertView == null) {			
				LayoutInflater mInflater = (LayoutInflater)
						context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.nav_drawer_row, null);
			}

			ImageView imgIcon = (ImageView) convertView.findViewById(R.id.navDraw_row_icon);
			TextView txtTitle = (TextView) convertView.findViewById(R.id.navDraw_row_title);


			txtTitle.setTextColor(Color.WHITE);
			imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
			txtTitle.setText(navDrawerItems.get(position).getTitle());
		}else{
			if (convertView == null) {			
				LayoutInflater mInflater = (LayoutInflater)
						context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.user_row, null);
			}
			// Find the user's profile picture custom view
			ProfilePictureView profilePictureView = (ProfilePictureView) convertView.findViewById(R.id.selection_profile_pic);
			profilePictureView.setCropped(true);
			profilePictureView.setProfileId(navDrawerItems.get(position).getUserId());

			// Find the user's name view
			TextView userNameView = (TextView) convertView.findViewById(R.id.selection_user_name);
			userNameView.setText(navDrawerItems.get(position).getUsername());
			userNameView.setTextColor(Color.WHITE);
		}



		return convertView;
	}

}

