package com.whooz;

import java.util.Arrays;

import com.facebook.widget.LoginButton;

import android.app.ActionBar;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link LoginFragment.OnFragmentInteractionListener}
 * interface to handle interaction events. Use the
 * {@link LoginFragment#newInstance} factory method to create an instance of
 * this fragment.
 * 
 */
public class LoginFragment extends Fragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ColorDrawable cd = new ColorDrawable(0xFF222222);
		ActionBar bar = getActivity().getActionBar();
		bar.setBackgroundDrawable(cd);
		bar.setTitle("Login");
		bar.setSubtitle("");
		bar.setDisplayHomeAsUpEnabled(false);
        bar.setHomeButtonEnabled(false);
        
        //clear backstack
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
            fm.popBackStack();
        }
        
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_login, container, false);
		LoginButton login = (LoginButton)view.findViewById(R.id.login_button);
		//login.setFragment(this);
		login.setReadPermissions(Arrays.asList("public_profile","email","user_birthday","user_friends"));
		return view;
	}
	

}
