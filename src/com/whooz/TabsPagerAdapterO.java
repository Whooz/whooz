package com.whooz;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapterO extends FragmentPagerAdapter {

	ProfileMainActivityO profileMainActivity;
	
	public TabsPagerAdapterO(FragmentManager fm,ProfileMainActivityO profileMainActivity) {
		super(fm);
		this.profileMainActivity = profileMainActivity;
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new ProfileFragment(MainActivity.currUser.getId()) ;
		case 1:
			// Games fragment activity
			return new InvitedToEventsFragment(profileMainActivity);
		case 2:
			return new UserCreatedEventsFragment();
		}
		return null;
	}


	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
