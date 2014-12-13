package com.whooz;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TimePicker;

public class DatePick extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calender);

		setTitle("Choose a date");
		
		
		Button ok = (Button) findViewById(R.id.ok);
		Button cancel = (Button) findViewById(R.id.cancel);
		
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CalendarView calview = (CalendarView) findViewById(R.id.calendarView1);
				long dateVal = calview.getDate();
				Date date=new Date(dateVal);
		        
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
		        
		        try {
					date = sdf.parse(sdf.format(date));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        dateVal = date.getTime();//this gives the milli until the date at midnight 
		        String dateText = sdf.format(date);
				
		        TimePicker time = (TimePicker) findViewById(R.id.timePick);
		        int hour = time.getCurrentHour();
		        int minute = time.getCurrentMinute(); 
		        
		        
		        
		        Intent returnIntent = new Intent();
				returnIntent.putExtra("dateMilli",dateVal);
				returnIntent.putExtra("dateStr", dateText);
				returnIntent.putExtra("hour", hour);
				returnIntent.putExtra("minute", minute);
				
				setResult(RESULT_OK,returnIntent);
				finish();
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent returnIntent = new Intent();
				setResult(RESULT_CANCELED,returnIntent);
				finish();
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.date_pick, menu);
		return true;
	}

}
