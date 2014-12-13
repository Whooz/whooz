package com.whooz;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.facebook.Session;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link WhoozMainActivity.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link WhoozMainActivity#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
@SuppressLint("ValidFragment")
public class WhoozMainActivity extends Fragment {
	private MainActivity mainActivity;

	public WhoozMainActivity(MainActivity mainActivity) {
		super();
		this.mainActivity = mainActivity;
	}
	public WhoozMainActivity(){
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		// Inflate the layout for this fragment
		Session session = Session.getActiveSession();
	    if (session != null && session.isOpened()) {
	        // Get the user's data
	        ((MainActivity)getActivity()).makeMeRequest(session);
	    }
		return inflater.inflate(R.layout.activity_whooz_main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		if(MainActivity.loadEventsAgain){
			MainActivity.loadEventsAgain = false;
			MainActivity.dialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_HOLO_DARK);
			MainActivity.dialog.setMessage("loading events...");
			MainActivity.dialog.setCancelable(false);
			MainActivity.dialog.setInverseBackgroundForced(false);
			MainActivity.dialog.setProgressStyle(R.drawable.jkb);
			MainActivity.dialog.setTitle("Whooz");
			MainActivity.dialog.show();
			mainActivity.load(null);
		}
		ColorDrawable cd = new ColorDrawable(0xFF222222);
		ActionBar bar = getActivity().getActionBar();
		bar.setBackgroundDrawable(cd);
		bar.setTitle("Whooz");
		bar.setSubtitle("");
		Button createEv = (Button) getView().findViewById(R.id.CreateEvent);
		createEv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mainActivity.setFragment_resume(null);
				Fragment fragment = new CreateEventFragment(mainActivity);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
				
			}
		});
		Button lookAr = (Button) getView().findViewById(R.id.LookAround);
		lookAr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Fragment fragment = new AroundMeFragment(mainActivity);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
			
			}
		});
		Button whatsNext=(Button) getView().findViewById(R.id.WhatsNext);
		whatsNext.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Fragment fragment = new WhatsNextFragment(mainActivity);
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frameContainer, fragment).addToBackStack(null).commit();
			}
		});
		
	}
}
