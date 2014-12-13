package com.whooz;

import org.json.JSONArray;
import org.json.JSONException;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;

@SuppressLint("ValidFragment")
public class UpdateEventFragment extends FragmentActivity {

	private Intent intent=null;
	public UpdateEventFragment(){
		super();
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.intent = getIntent();
		WhoozLocation loc = new WhoozLocation(this.intent.getDoubleExtra("latitude",-1),
				this.intent.getDoubleExtra("longitude",-1), this.intent.getStringExtra("placeDesc"));
		
		try {
			byte[] byteArray = getIntent().getByteArrayExtra("pic");
			Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
			WhoozEvent event = new WhoozEvent(this.intent.getIntExtra("id",0), this.intent.getStringExtra("name"),
					this.intent.getStringExtra("info"), this.intent.getBooleanExtra("isPrivate",false),
					this.intent.getLongExtra("date",-1), loc,
					bmp,this.intent.getStringExtra("objId"),
					new JSONArray(this.intent.getStringExtra("selectedFriends")));
			
			Fragment fragment = new CreateEventFragment(true,event);
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
			.replace(R.id.frameContainer, fragment).commit();
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


/*	private void initiateEvent() {
		//initiate variables
		this.name  = intent.getStringExtra("name");
		this.placeDesc = intent.getStringExtra("place");
		this.info = intent.getStringExtra("info");
		this.isPrivate = intent.getBooleanExtra("isPrivate",false);
		this.date = intent.getLongExtra("date", 0);
		this.photo=(Bitmap) intent.getParcelableExtra("pic");
		
		//show event details
		this.eventName = (EditText) this.findViewById(R.id.eventName);
		this.eventPlace = (AutoCompleteTextView) findViewById(R.id.placesAutoCompleteBox);
		this.eventInfo = (EditText) findViewById(R.id.info);
		this.eventImg = (ImageView) findViewById(R.id.ivImage);
		this.eventDate = (TextView) findViewById(R.id.dateDisplay);

		this.eventName.setText(this.name);
		this.eventPlace.setText(this.placeDesc);
		this.eventInfo.setText(this.info);
		this.eventImg.setImageBitmap(this.photo);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		Date date = new Date(this.date);
		this.eventDate.setText(sdf.format(date));
		



	}*/


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
