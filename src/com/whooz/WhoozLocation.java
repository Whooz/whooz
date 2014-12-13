package com.whooz;

import com.parse.ParseGeoPoint;

public class WhoozLocation {

	String description;
	ParseGeoPoint geoPoint;
	
	public WhoozLocation(double latitude,double longitude, String description){
		this.geoPoint=new ParseGeoPoint(latitude, longitude);
		this.description=description;
	}
	public WhoozLocation(ParseGeoPoint geoPoint, String description){
		this.geoPoint=new ParseGeoPoint(geoPoint.getLatitude(),geoPoint.getLongitude());
		this.description=description;
	}
	
	
	public String getDescription() {
		return description;
	}
	public double getLongitude(){
		return geoPoint.getLongitude();
	}
	public double getLatitude(){
		return geoPoint.getLatitude();
	}
	public ParseGeoPoint getGeoPoint() {
		return geoPoint;
	}


	
	
}
