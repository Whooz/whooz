package com.whooz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

public class FragmentSwapperActivity extends FragmentActivity {

	static MainActivity mainActivity;
	static String activityName="";
	Intent intent;
	static void updatebeforeSwap(MainActivity main, String name){
		mainActivity = main;
		activityName=name;
	}
	protected void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.activity_main);
		if(activityName == "WhatsNext"){
			Fragment fragment = new WhatsNextFragment(mainActivity);
			MainActivity.dialog.hide();
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction().replace(R.id.frameContainer,fragment)
					.addToBackStack(null).commit();
		}
		
		
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
