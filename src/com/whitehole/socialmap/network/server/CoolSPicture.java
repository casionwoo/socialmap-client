package com.whitehole.socialmap.network.server;

import java.util.ArrayList;

import com.whitehole.socialmap.network.SPicture;

public class CoolSPicture extends SPicture  {
	public ArrayList<SPicture> photos;
	public String location;

	public CoolSPicture(ArrayList<SPicture> photos, String location){
		this.photos = photos;
		this.location = location;
	}
	
	public CoolSPicture(SPicture photos){
		this.photos.add(photos);
	}
	
	public CoolSPicture(ArrayList<SPicture> photos){
		this.photos = photos;
	}
	
	public ArrayList<SPicture> getPhotos(){
		return photos;
	}

	public String getLocation(){
		return location;
	}
	
	public void setPhotos(ArrayList<SPicture> photos){
		this.photos = photos;
	}
	
	public void setLocation(String location){
		this.location = location;
	}

	@Override
	public String toString() {
		return "CoolSPicture [photos=" + photos.size() + ", location=" + location + "]";
	}
	
}
