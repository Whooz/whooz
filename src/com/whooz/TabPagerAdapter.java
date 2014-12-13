package com.whooz;

import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
	private ViewPager viewPager;
	private CreateEventFragment createEventFragment;
	private ProfileMainActivity profileMainActivity;
	public static Dialog profileDialog;
	public TabPagerAdapter(FragmentManager fm, MainActivity mainActivity, ViewPager pager,ProfileMainActivity profileMainActivity) {
		super(fm);
		this.profileMainActivity = profileMainActivity;
		viewPager = pager;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int i) {
		//Toast.makeText(profileMainActivity, i+" has been chosen",Toast.LENGTH_SHORT).show();

		switch (i) {
		case 0:
			//Toast.makeText(profileMainActivity, "mainPressed", Toast.LENGTH_SHORT).show();

			return new ProfileFragment(MainActivity.currUser.getId());
		case 1:		
			//Toast.makeText(profileMainActivity, "invitePressed", Toast.LENGTH_SHORT).show();
			//return new InvitedToEventsFragment(profileMainActivity);		
		case 2:
			//Toast.makeText(profileMainActivity, "createPressed", Toast.LENGTH_SHORT).show();
			createEventFragment = new CreateEventFragment();
			return createEventFragment;
		case 3:
			//Toast.makeText(profileMainActivity, "myEventsPressed", Toast.LENGTH_SHORT).show();
			return new UserCreatedEventsFragment(viewPager,createEventFragment,profileMainActivity);
			//return new InvitedToEventsFragment();
			
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4; // No of Tabs
	}
}