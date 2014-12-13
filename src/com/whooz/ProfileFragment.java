package com.whooz;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.facebook.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("ValidFragment")
public class ProfileFragment extends Fragment {
	private boolean canSave = false;
	@SuppressLint("ValidFragment")
	private ProfileMainActivityO profileMainActivity;
	private String userId;

	public ProfileFragment(String userId) {
		super();
		this.userId = userId;
	}

	public ProfileFragment(String userId, MainActivity mainActivity,
			ProfileMainActivityO profileMainActivity) {
		super();
		this.userId = userId;
		this.profileMainActivity = profileMainActivity;
	}
	public ProfileFragment(){
		super();
	}

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		load();

	}

	public void load() {
		//		ProfileMainActivity.profiledialog = new ProgressDialog(getActivity(),
		//				ProgressDialog.THEME_HOLO_DARK);
		//		ProfileMainActivity.profiledialog.setMessage("loading your profile...");
		//		ProfileMainActivity.profiledialog.setCancelable(false);
		//		ProfileMainActivity.profiledialog.setInverseBackgroundForced(false);
		//		ProfileMainActivity.profiledialog.setProgressStyle(R.drawable.jkb);
		//		ProfileMainActivity.profiledialog.setTitle("Whooz");
		//		ProfileMainActivity.profiledialog.show();
		ProfileMainActivityO.profiledialog = new ProgressDialog(getActivity(),
				ProgressDialog.THEME_HOLO_DARK);
		ProfileMainActivityO.profiledialog.setMessage("loading profile ...");
		ProfileMainActivityO.profiledialog.setCancelable(false);
		ProfileMainActivityO.profiledialog.setInverseBackgroundForced(false);
		ProfileMainActivityO.profiledialog.setProgressStyle(R.drawable.jkb);
		ProfileMainActivityO.profiledialog.setTitle("Whooz");
		ProfileMainActivityO.profiledialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
		final String userId = this.userId;
		query.whereEqualTo(ParseManager.USER_ID, userId);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> arg0, ParseException arg1) {
				// LoadProfile load = new LoadProfile();
				// load.execute(arg0);
				ProfileMainActivityO.profiledialog.hide();
				ParseObject obj = arg0.get(0);
				final EditText firstName = (EditText) getView().findViewById(
						R.id.firstName);
				final EditText secondName = (EditText) getView().findViewById(
						R.id.secondName);
				//				final EditText email = (EditText) getView().findViewById(
				//						R.id.emailAddress);
				final EditText age = (EditText) getView()
						.findViewById(R.id.age);
				final EditText gender = (EditText) getView().findViewById(
						R.id.gender);
				final EditText interstedIn = (EditText) getView().findViewById(
						R.id.intrestedIn);
				final EditText realtionsheep = (EditText) getView()
						.findViewById(R.id.realtionsheepStatus);
				final EditText about = (EditText) getView().findViewById(
						R.id.about);
				final Button saveBtt = (Button) getView().findViewById(
						R.id.save);
				final ProfilePictureView profilePictureView = (ProfilePictureView) getView()
						.findViewById(R.id.users_profile_pic);


				profilePictureView.setCropped(true);
				final JSONArray proFriends = obj.getJSONArray("friends");
				ArrayList<String> friendsList = new ArrayList<String>();
				for (int i =0 ;i<proFriends.length();i++){

					try {

						friendsList.add(proFriends.getString(i));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}


				firstName.setText(obj.getString(ParseManager.USER_FIRST_NAME));
				secondName.setText(obj.getString(ParseManager.USER_SECOND_NAME));
				profilePictureView.setProfileId(userId);
				//				email.setText(obj.getString(ParseManager.USER_EMAIL_ADDRESS));
				age.setText(obj.getInt(ParseManager.USER_AGE) + "");
				gender.setText(obj.getString(ParseManager.USER_GENDER));
				interstedIn.setText(obj
						.getString(ParseManager.USER_INTERSTED_IN));
				realtionsheep.setText(obj
						.getString(ParseManager.USER_REALTIONSHEEP_STATUS));
				about.setText(obj.getString(ParseManager.USER_ABOUT));
				Button edtBtt = (Button) getView().findViewById(R.id.edt);

				if (MainActivity.currUser.getId().equals(userId)) {
					edtBtt.setOnClickListener(new View.OnClickListener() {


						/**
						 * 
						 */


						@Override
						public void onClick(View v) {
							firstName.setFocusableInTouchMode(true);
							secondName.setFocusableInTouchMode(true);
							//							email.setFocusableInTouchMode(true);
							age.setFocusableInTouchMode(true);
							about.setBackgroundColor(Color.LTGRAY);
							gender.setFocusableInTouchMode(true);
							gender.setBackgroundColor(Color.LTGRAY);
							interstedIn.setBackgroundColor(Color.LTGRAY);
							realtionsheep.setBackgroundColor(Color.LTGRAY);
							interstedIn.setFocusableInTouchMode(true);
							realtionsheep.setFocusableInTouchMode(true);
							about.setFocusableInTouchMode(true);
							saveBtt.setVisibility(View.VISIBLE);
							canSave = true;
						}
					});
					saveBtt.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (canSave) {
								String fn = firstName.getText().toString();
								String sn = secondName.getText().toString();
								//								String e = email.getText().toString();
								int a = Integer.parseInt(age.getText()
										.toString());
								about.setBackgroundColor(Color.TRANSPARENT);
								realtionsheep.setBackgroundColor(Color.TRANSPARENT);
								interstedIn.setBackgroundColor(Color.TRANSPARENT);
								gender.setBackgroundColor(Color.TRANSPARENT);
								String g = gender.getText().toString();
								String i = interstedIn.getText().toString();
								String r = realtionsheep.getText().toString();
								String ab = about.getText().toString();
								ParseManager.updateUserInfo(fn, sn, a, g, i,
										r, ab);
								firstName.setFocusable(false);
								secondName.setFocusable(false);
								//								email.setFocusable(false);
								age.setFocusable(false);
								gender.setFocusable(false);
								interstedIn.setFocusable(false);
								realtionsheep.setFocusable(false);
								about.setFocusable(false);
								saveBtt.setVisibility(View.INVISIBLE);
								canSave = false;

							}
						}
					});
				} else {
					edtBtt.setVisibility(View.INVISIBLE);

				}					
			}

		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_profile, container, false);
	}



}
