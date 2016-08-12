package com.whitehole.socialmap.network.server;

import java.util.ArrayList;

import com.whitehole.socialmap.network.SPicture;

public class SSPicture extends SPicture {
	public ArrayList<SPicture> data;
	public ArrayList<String> sdata;

	public SSPicture() {
	}

	public SSPicture(int i, ArrayList<SPicture> data) {
		super();
		this.data = data;
	}
	
	public SSPicture(ArrayList<String> sdata){
		this.sdata = sdata;
	}
	
	public ArrayList<SPicture> getData() {
		return data;
	}

	public void setData(ArrayList<SPicture> data) {
		this.data = data;
	}

}
