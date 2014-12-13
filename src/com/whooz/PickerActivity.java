package com.whooz;

import com.facebook.FacebookException;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.PickerFragment;
import com.facebook.widget.PlacePickerFragment;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

public class PickerActivity extends FragmentActivity {
	public static final Uri PLACE_PICKER = Uri.parse("picker://place");
	public static final Uri FRIEND_PICKER = Uri.parse("picker://friend");
	private PlacePickerFragment placePickerFragment;
	private FriendPickerFragment friendPickerFragment;
	private LocationListener locationListener;
	private static final int SEARCH_RESULT_LIMIT = 50;
	private static final Location JERUSALEM_LOCATION = new Location("") {
		{
			setLatitude(31.7820);
			setLongitude(35.219684);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pickers);

		Bundle args = getIntent().getExtras();
		FragmentManager manager = getSupportFragmentManager();
		Uri intentUri = getIntent().getData();
		Fragment fragmentToShow = null;

		if (PLACE_PICKER.equals(intentUri)) {
			if (savedInstanceState == null) {
				placePickerFragment = new PlacePickerFragment(args);
			} else {
				placePickerFragment = (PlacePickerFragment) manager
						.findFragmentById(R.id.picker_fragment);
			}
			placePickerFragment
			.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
				@Override
				public void onSelectionChanged(
						PickerFragment<?> fragment) {
					finishActivity(); // call finish since you can only
					// pick one place
				}
			});

			placePickerFragment
			.setOnErrorListener(new PickerFragment.OnErrorListener() {

				@Override
				public void onError(PickerFragment<?> fragment,
						FacebookException error) {
					// TODO Auto-generated method stub
					PickerActivity.this.onError(error);
				}
			});
			placePickerFragment
			.setOnDoneButtonClickedListener(new PickerFragment.OnDoneButtonClickedListener() {
				@Override
				public void onDoneButtonClicked(
						PickerFragment<?> fragment) {
					finishActivity();
				}
			});
			fragmentToShow = placePickerFragment;
		}
		else if(FRIEND_PICKER.equals(intentUri)){
			if (savedInstanceState == null) {
				friendPickerFragment = new FriendPickerFragment(args);
			} else {
				friendPickerFragment = 
						(FriendPickerFragment) manager.findFragmentById(R.id.picker_fragment);
			}
			// Set the listener to handle errors
			friendPickerFragment.setOnErrorListener(new PickerFragment.OnErrorListener() {
				@Override
				public void onError(PickerFragment<?> fragment,
						FacebookException error) {
					PickerActivity.this.onError(error);
				}
			});
			// Set the listener to handle button clicks
			friendPickerFragment.setOnDoneButtonClickedListener(
					new PickerFragment.OnDoneButtonClickedListener() {
						@Override
						public void onDoneButtonClicked(PickerFragment<?> fragment) {
							finishActivity();
						}
					});
			fragmentToShow = friendPickerFragment;
		}else {
			// Nothing to do, finish
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		manager.beginTransaction()
		.replace(R.id.picker_fragment, fragmentToShow).commit();

	}

	private void onError(Exception error) {
		onError(error.getLocalizedMessage(), false);
	}

	private void onError(String error, final boolean finishActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error_dialog_title)
		.setMessage(error)
		.setPositiveButton(R.string.error_dialog_button_text,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(
					DialogInterface dialogInterface, int i) {
				if (finishActivity) {
					finishActivity();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (FRIEND_PICKER.equals(getIntent().getData())) {
			try {
				friendPickerFragment.loadData(false);
			} catch (Exception ex) {
				onError(ex);
			}
		}
		else if (PLACE_PICKER.equals(getIntent().getData())) {
			try {
				Location location = new Location(JERUSALEM_LOCATION);
				GPSTracker gps = new GPSTracker(this);
				if (gps.canGetLocation()) {
					location.setLongitude(gps.getLongitude());
					location.setLatitude(gps.getLatitude());					
				}

				// Configure the place picker: search center, radius,
				// query, and maximum results.
				placePickerFragment.setLocation(location);
				MainActivity.setUserLoc(location);
				// placePickerFragment.setRadiusInMeters(SEARCH_RADIUS_METERS);
				// placePickerFragment.setSearchText(SEARCH_TEXT);
				placePickerFragment.setResultsLimit(SEARCH_RESULT_LIMIT);
				// Start the API call
				placePickerFragment.loadData(true);
			} catch (Exception ex) {
				onError(ex);
			}
		}
	}

	protected void onStop() {
		super.onStop();
		if (locationListener != null) {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// Remove updates for the location listener
			locationManager.removeUpdates(locationListener);
			locationListener = null;
		}
	}

	private void finishActivity() {
		ScrumptiousApplication app = (ScrumptiousApplication) getApplication();
		if (PLACE_PICKER.equals(getIntent().getData())) {
			if (placePickerFragment != null) {
				app.setSelectedPlace(placePickerFragment.getSelection());
			}
		}
		else if(FRIEND_PICKER.equals(getIntent().getData())){
			if(friendPickerFragment!=null){
				app.setSelectedUsers(friendPickerFragment.getSelection());
			}
		}
		setResult(RESULT_OK, null);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.picker, menu);
		return true;
	}

}
