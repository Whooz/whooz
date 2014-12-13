package com.whooz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


@SuppressLint("ValidFragment")
public class CreateEventFragment extends Fragment {
	private MainActivity mainActivity;




	protected static final int REQUEST_CAMERA = 1;
	protected static final int SELECT_FILE = 2;
	final int DP_RET_CODE=11;//date picker return code
	final int PP_RET_CODE=12;//place picker return code
	final int FP_RET_CODE=13;//friends picker return code
	//protected ImageView ivlImage;
	protected ImageView selectImage;
	private int ivlRotation;
	private Boolean isPrivate;
	private long dateMilli=-1;
	private 	Bitmap cover = null;
	private String dateStr="";
	private int hour=0;
	private int minute=0;
	private GraphPlace selectedFacebookPlace=null;
	private List<GraphUser> selectedFriends=null;
	private boolean isFacebookLoc=false;
	private boolean isGoogleLoc=false;
	private List<String> invitedIds;	
	TextView description;
	TextView evHeader;
	private static final String LOG_TAG = "WhoozAppError";
	AutoCompleteTextView autoCompView;
	private WhoozLocation selectedLocation;
	private String googleLocationDescription="";
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	private static final String API_KEY = "AIzaSyBi-W1nFzYCU4_7DyyLhYit17F2PyNTS3g";
	private boolean isUpdate = false;
	private WhoozEvent eventToUpdate=null;
	private String eventTitle;
	private String eventInfo;
	public CreateEventFragment(Boolean isUpdate,WhoozEvent event) {
		super();
		this.isUpdate = true;
		this.eventToUpdate=event;
	}

	public CreateEventFragment(MainActivity mainActivity) {
		super();
		this.mainActivity = mainActivity;
	}

	public CreateEventFragment(){
		super();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.create_event, container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.create_event);
		//isPrivate = false;
		String s="<b>Create Event</b>";
		String footer = "make your own event";
		RadioGroup radioEveTypeGroup = (RadioGroup) getView().findViewById(R.id.eveType);
		radioEveTypeGroup.check(R.id.pubRadio);
		ColorDrawable cd = new ColorDrawable(0xFFC70707);
		ActionBar bar = getActivity().getActionBar();
		bar.setBackgroundDrawable(cd);
		bar.setTitle(Html.fromHtml(s));
		bar.setSubtitle(footer);
		Button dateP = (Button) getView().findViewById(R.id.pickDate);
		description = (TextView) getView().findViewById(R.id.info);
		evHeader = (TextView) getView().findViewById(R.id.eventName);
		Button location = (Button) getView().findViewById(R.id.curLoc);
		Button sendRequestButton = (Button) getView().findViewById(R.id.sendRequestButton);
		sendRequestButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//sendRequestDialog();
				startPickerActivity(PickerActivity.FRIEND_PICKER, FP_RET_CODE);
				//Intent in = new Intent(getActivity(),InviteFriends.class);
				//startActivity(in);
			}
		});

		autoCompView = (AutoCompleteTextView) getView().findViewById(R.id.placesAutoCompleteBox);
		autoCompView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
		autoCompView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				isGoogleLoc=true;
				isFacebookLoc=false;
				googleLocationDescription = (String) adapterView.getItemAtPosition(position);
			}

		});
		location.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startPickerActivity(PickerActivity.PLACE_PICKER, PP_RET_CODE);

			}
		});



		//ivlImage = (ImageView) getView().findViewById(R.id.ivImage); 
		//Button selIm = (Button) getView().findViewById(R.id.selPictures);
		//	lnrImages = (LinearLayout)getView().findViewById(R.id.lnrImages);
		/*selIm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
*/
		selectImage = (ImageButton) getView().findViewById(R.id.photoSelector);
		selectImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();

			}
		});

		dateP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent in = new Intent(getActivity(),DatePick.class);
				startActivityForResult(in, DP_RET_CODE);

			}
		});


		Button create = (Button) getView().findViewById(R.id.create);

		create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isPrivate=isPrivateSelected();
				eventTitle = evHeader.getText().toString();
				eventInfo = description.getText().toString();
				assert(isFacebookLoc && isGoogleLoc);
				invitedIds=new ArrayList<String>();
				//In case the user didn't upload a cover photo use the default one
				if(cover==null){
					cover = BitmapFactory.decodeResource(getResources(), R.drawable.photo_default);					
				}
				if (selectedFriends != null) {
					for (GraphUser u : selectedFriends) {
						invitedIds.add(u.getId());
					}
				}
				if(isFacebookLoc){
					selectedLocation=new WhoozLocation(selectedFacebookPlace.getLocation().getLatitude(), selectedFacebookPlace.getLocation().getLongitude(), selectedFacebookPlace.getName());
				}else if(isGoogleLoc){
					List<Address> addresses = null;
					try{
						addresses = (new Geocoder(getActivity())).getFromLocationName(googleLocationDescription, Integer.MAX_VALUE);
						selectedLocation = new WhoozLocation(addresses.get(0).getLatitude(),addresses.get(0).getLongitude(), googleLocationDescription);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				//check if the event is valid
				if(!isValid()){
					return;
				}
				//check for invited friends according the event type and procced with create event accordingly
				checkPrivateFriends();
			}

		});
		if(isUpdate)
			updateEvent(eventToUpdate);


	}

	//check if the user entered all the necessary event's fields
	private boolean isValid(){
		String problem = "You must enter:";
		boolean valid = true;
		if(eventTitle.equals("")){
			valid = false;
			problem += " event title,";
		}
		if(selectedLocation==null){
			valid = false;
			problem += " event location,";
		}
		if(dateMilli==-1){
			valid = false;
			problem += " event time&date,";
		}
		problem = problem.substring(0,problem.length()-1);
		if (!valid) {
			new AlertDialog.Builder(getActivity()).setTitle("Errors in create")
			.setMessage(problem)
			.setPositiveButton("Ok", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub

				}
			}).create().show();
		}
		return valid;
	}
	//Check if the event is private, if so check if thier are invited guests and ask the user to invite friends if none
	private void checkPrivateFriends(){		
		if(isPrivate&&invitedIds.size()==0){
			new AlertDialog.Builder(getActivity()).setTitle("Invite friends")
			.setMessage("Your event is private but you have not invited any friends. Would you like to invite some friends?")
			.setPositiveButton("Yes sure!", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub					
					startPickerActivity(PickerActivity.FRIEND_PICKER, FP_RET_CODE);					
				}
			}).setNegativeButton("No", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					createEvent();
				}
			}).create().show();
		}else{
			createEvent();
		}
	}
	private void createEvent(){
		CreateEv newEv = new CreateEv();
		if (mainActivity != null) {
			newEv.execute();
			Fragment fragment = new WhoozMainActivity(mainActivity);
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frameContainer, fragment)
			.addToBackStack(null).commit();
		}
	}
	private class CreateEv extends AsyncTask<Void, Void, WhoozEvent>  {

		@Override
		protected WhoozEvent doInBackground(Void... input) {

			long timeAndDateLong = dateMilli+hour*3600000+minute*60000;			
			WhoozEvent newEvent = new WhoozEvent(eventTitle,eventInfo,
					isPrivate,timeAndDateLong,selectedLocation,cover,MainActivity.currUser.getId(),invitedIds);
			ParseManager.addEvent(newEvent);
			//send push notifications to invited friends
			if (selectedFriends != null) {
				for (GraphUser friend : selectedFriends) {
					ParseQuery query = ParseInstallation.getQuery();
					query.whereEqualTo("userId", friend.getId());
					ParsePush push = new ParsePush();
					push.setQuery(query);
					push.setMessage(MainActivity.currUser.getName()
							+ " has invited you to his event!");
					push.sendInBackground();
				}
			}
			return newEvent;

		}
		@Override
		protected void onPostExecute(WhoozEvent result) {

			super.onPostExecute(result);
			MainActivity.lookArondEvents.add(result);
			Collections.sort(MainActivity.lookArondEvents, new MainActivity.eventsCompare());
		}


	}


	private void sendRequestDialog() {
		Bundle params = new Bundle();
		params.putString("message", "Invite friends");

		WebDialog requestsDialog = (
				new WebDialog.RequestsDialogBuilder(getActivity(),
						Session.getActiveSession(),
						params))
						.setOnCompleteListener(new OnCompleteListener() {

							@Override
							public void onComplete(Bundle values, FacebookException error) {
								// TODO Auto-generated method stub
								if (error != null) {
									if (error instanceof FacebookOperationCanceledException) {
										Toast.makeText(getActivity().getApplicationContext(), 
												"Request cancelled", 
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getActivity().getApplicationContext(), 
												"Network Error", 
												Toast.LENGTH_SHORT).show();
									}
								} else {
									final String requestId = values.getString("request");
									if (requestId != null) {
										Toast.makeText(getActivity().getApplicationContext(), 
												"Request sent",  
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getActivity().getApplicationContext(), 
												"Request cancelled", 
												Toast.LENGTH_SHORT).show();
									}
								}   


							}
						})
						.build();
		requestsDialog.show();
	}
	private Boolean isPrivateSelected() {
		RadioGroup radioEveTypeGroup = (RadioGroup) getView().findViewById(R.id.eveType);
		int selectedId = radioEveTypeGroup.getCheckedRadioButtonId();
		RadioButton radioEveTypeButton =(RadioButton) getView().findViewById(selectedId);
		String text = ""+radioEveTypeButton.getText();
		if (text.equals("Private")){
			return true;
		}
		return false;
	}


	private void selectImage() {
		final CharSequence[] items = { "Take Photo", "Choose from Library",
		"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, REQUEST_CAMERA);
				} else if (items[item].equals("Choose from Library")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");

					startActivityForResult(
							Intent.createChooser(intent, "Select File"),
							SELECT_FILE);
					//					Intent intent = new Intent(getActivity(),CustomPhotoGalleryActivity.class);
					//					startActivityForResult(intent,SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==Activity.RESULT_OK){
			if(requestCode == FP_RET_CODE){
				//nothing for now
				selectedFriends = ((ScrumptiousApplication) getActivity().getApplication()).getSelectedUsers();
				/*for(GraphUser u:selectedFriends){
					Toast.makeText(getActivity(), ""+u.getName(), Toast.LENGTH_LONG).show();
				}*/
				return;
			}
			else if(requestCode==DP_RET_CODE){
				long currentDate = (new Date()).getTime();
				dateMilli = data.getLongExtra("dateMilli", 0);
				dateStr = data.getStringExtra("dateStr");
				hour = data.getIntExtra("hour", 0);
				minute = data.getIntExtra("minute", 0);
				if((dateMilli+hour*3600000+minute*60000)<=currentDate){
					Toast.makeText(getActivity(), "please choose a vaild date", Toast.LENGTH_SHORT).show();
					dateMilli=0;
					dateStr="";
					hour=0;
					minute=0;
					
				}else{
					TextView dateDisplay = (TextView) getView().findViewById(R.id.dateDisplay);
					dateDisplay.setText(dateStr+" "+hour+":"+minute);


					Log.d("~~~theDate and time~~~", ""+dateStr+" ||| "+hour+"::"+minute);
				}
				return;



			}

			else if(requestCode==PP_RET_CODE){
				selectedFacebookPlace = ((ScrumptiousApplication) getActivity()
						.getApplication()).getSelectedPlace();
				EditText placeName = (EditText) getView().findViewById(R.id.placesAutoCompleteBox);
				if(selectedFacebookPlace!=null){
					placeName.setText(selectedFacebookPlace.getName());
					isFacebookLoc=true;
					isGoogleLoc=false;
				}else{
					placeName.setText("");
					placeName.setHint(R.string.Add);
				}
			}


			else if (requestCode == REQUEST_CAMERA) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}

				SelectImage selIm = new SelectImage();
				getImgRotation(f.getAbsolutePath());
				selIm.execute(f.getAbsolutePath());

				//					ImageView imageView = new ImageView(getActivity());
				//					imageView.setImageBitmap(bm);
				//					imageView.setRotation(90);
				//					imageView.setPadding(30, 0,0,0);
				//					imageView.setAdjustViewBounds(true);
				//					lnrImages.addView(imageView);
				//
				//					String path = android.os.Environment
				//							.getExternalStorageDirectory()
				//							+ File.separator
				//							+ "Phoenix" + File.separator + "default";
				//					f.delete();
				//					OutputStream fOut = null;
				//					File file = new File(path, String.valueOf(System
				//							.currentTimeMillis()) + ".jpg");
				//					try {
				//						fOut = new FileOutputStream(file);
				//						bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
				//						fOut.flush();
				//						fOut.close();
				//					} catch (FileNotFoundException e) {
				//						e.printStackTrace();
				//					} catch (IOException e) {
				//						e.printStackTrace();
				//					} catch (Exception e) {
				//						e.printStackTrace();
				//					}

			}
			else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();
				SelectImage selIm = new SelectImage();
				String tempPath = getPath(selectedImageUri, getActivity());
				getImgRotation(tempPath);
				selIm.execute(tempPath);

				//				imagesPathList = new ArrayList<String>();
				//				String[] imagesPath = data.getStringExtra("data").split("\\|");
				//								try{
				//									lnrImages.removeAllViews();
				//								}catch (Throwable e){
				//									e.printStackTrace();
				//								}
				//				for (int i=0;i<imagesPath.length;i++){
				//					imagesPathList.add(imagesPath[i]);
				//					yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
				//					resized=    Bitmap.createScaledBitmap(yourbitmap, 200,300, true);
				//					ImageView imageView = new ImageView(getActivity());
				//					imageView.setImageBitmap(resized);
				//
				//					imageView.setPadding(30, 0,0,0);
				//					imageView.setAdjustViewBounds(true);
				//					lnrImages.addView(imageView);
			}
		}
		else if(requestCode == Activity.RESULT_CANCELED){
			return;
		}
	}


	private void getImgRotation(String path){
		ExifInterface exif;
		try {
			exif = new ExifInterface(path);
			int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL); 
			ivlRotation = exifToDegrees(rotation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private class SelectImage extends AsyncTask<String, Void, Bitmap>  {


		@Override
		protected Bitmap doInBackground(String... selectedImageUri) {

			BitmapFactory.Options opts = new BitmapFactory.Options(); 
			opts.inDither = true; // we're using RGB_565, dithering improves this 

			opts.inPreferredConfig = Bitmap.Config.RGB_565; 
			opts.inSampleSize = 8;
			//	btmapOptions.inJustDecodeBounds = true;
			Matrix matrix = new Matrix();

			matrix.postRotate(ivlRotation);

			try {
				cover =  BitmapFactory.decodeStream(new FileInputStream(selectedImageUri[0]),null,opts);
				cover = Bitmap.createBitmap(cover , 0, 0, cover.getWidth(), cover.getHeight(), matrix, true);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return cover;

		}
		@Override
		protected void onPostExecute(Bitmap result) {

			super.onPostExecute(result);
			//ivlImage.setImageBitmap(result);
			selectImage.setScaleType(ScaleType.CENTER_CROP);
			selectImage.setImageBitmap(result);
			selectImage.setPadding(0, 0, 0, 0);
			//selectImage.setAdjustViewBounds(true);
			//	ivlImage.setRotation(ivlRotation);
		}


	}

	private static int exifToDegrees(int exifOrientation) {        
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; } 
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; } 
		else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }            
		return 0;    
	}

	public String getPath(Uri uri, Activity activity) {
		String[] projection = { MediaColumns.DATA };
		@SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	private void startPickerActivity(Uri data, int requestCode) {
		Intent intent = new Intent();
		intent.setData(data);
		intent.setClass(getActivity(), PickerActivity.class);
		startActivityForResult(intent, requestCode);

	}

	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					}
					else {
						notifyDataSetInvalidated();
					}
				}};
				return filter;
		}
	}

	private int findEventIndex(int eventId){		
		for(int i=0;i<MainActivity.userCreatedEvents.size();i++){
			if(eventId==MainActivity.userCreatedEvents.get(i).getId()){
				return i;
			}
		}
		return -1;
	}

	private void updateEvent(final WhoozEvent event) {
		//get some evet's info
		selectedLocation = event.getPlace();
		invitedIds = event.getInvitedIds();
		dateMilli = event.getDate();
		hour = event.getHour();
		minute = event.getMinute();
		cover = event.getPic();
		//set info in corresponding fields
		this.evHeader.setText(event.getHeader());
		this.autoCompView.setText(event.getPlace().getDescription());
		RadioGroup radioGroup = (RadioGroup) getView().findViewById(R.id.eveType);
		if(event.getIsPrivate()){
			radioGroup.check(R.id.privRadio);
		}else{
			radioGroup.check(R.id.pubRadio);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		Date date = new Date(event.getDate());
		TextView dateDisplay = (TextView) getView().findViewById(R.id.dateDisplay);
		dateDisplay.setText(sdf.format(date));
		this.description.setText(event.getDescrip());		
		//this.ivlImage.setImageBitmap(event.getPic());
		selectImage.setScaleType(ScaleType.CENTER_CROP);
		selectImage.setImageBitmap(event.getPic());
		selectImage.setPadding(0, 0, 0, 0);

		//update buttons
		Button CreateButt = (Button)getView().findViewById(R.id.create);
		CreateButt.setText("UPDATE");

		Button bttDel = (Button)getView().findViewById(R.id.delete);
		bttDel.setVisibility(View.VISIBLE);

		//delete the event
		bttDel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseManager.EVENT_CLASS_NAME);				
				query.whereEqualTo(ParseManager.EVENT_ID, event.getId());
				int indexToDel = findEventIndex(event.getId());
				MainActivity.userCreatedEvents.remove(indexToDel);
				query.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> users, ParseException e) {
						ParseObject event=users.get(0);
						ParseManager.deleteProcedure(event);
						Intent in = new Intent(getActivity(),ProfileMainActivityO.class);
						in.putExtra("tab", 2);
						startActivity(in);	
					}
				});
			}
		});

		//update event's info
		CreateButt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//get new information
				isPrivate=isPrivateSelected();
				assert(isFacebookLoc && isGoogleLoc);
				invitedIds=new ArrayList<String>();
				if (selectedFriends != null) {
					for (GraphUser u : selectedFriends) {
						if(!invitedIds.contains(u.getId()))
							invitedIds.add(u.getId());
					}
				}
				if(isFacebookLoc){
					selectedLocation=new WhoozLocation(selectedFacebookPlace.getLocation().getLatitude(), selectedFacebookPlace.getLocation().getLongitude(), selectedFacebookPlace.getName());
				}else if(isGoogleLoc){
					List<Address> addresses = null;
					try{
						addresses = (new Geocoder(getActivity())).getFromLocationName(googleLocationDescription, Integer.MAX_VALUE);
						selectedLocation = new WhoozLocation(addresses.get(0).getLatitude(),addresses.get(0).getLongitude(), googleLocationDescription);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				final long timeAndDateLong = dateMilli+hour*3600000+minute*60000;	
				final WhoozEvent newEvent = new WhoozEvent(event.getId(), evHeader.getText().toString(),
						description.getText().toString(),isPrivate, timeAndDateLong,
						selectedLocation, cover, event.getOwnerId(), invitedIds);
				int indexToUpdate = findEventIndex(event.getId());
				MainActivity.userCreatedEvents.get(indexToUpdate).updateEvent(newEvent);

				//get the old  event's parse object
				ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseManager.EVENT_CLASS_NAME);				
				query.whereEqualTo(ParseManager.EVENT_ID, event.getId());
				query.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> users, ParseException e) {
						ParseObject oldEvent=users.get(0);
						//create a new event (with same id), and update the old parse object to contain the
						//new events information (everything changes except event's id and owner )

						ParseManager.updateEvent(oldEvent,newEvent);

						Intent in = new Intent(getActivity(),ProfileMainActivityO.class);
						in.putExtra("tab", 2);
						startActivity(in);	

					}
				});

			}
		});



	}

}
