package com.whitehole.socialmap.more;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.whitehole.socialmap.R;

public class sub_addaccount_activity extends Activity {
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_addaccount_name);
	}
}


class AddaccountListItem {
	AddaccountListItem(int aType, String aNameText, int aImgplus, int aImglogo, int aImgprofile, String aAccountText){
		Type		= aType;
		NameText	= aNameText;
		Imgplus		= aImgplus;
		Imglogo		= aImglogo;
		Imgprofile	= aImgprofile;
		AccountText	= aAccountText;
	}
	
	int Type;
	String NameText;
	int Imgplus;
	int Imglogo;
	int Imgprofile;
	String AccountText;
}

class MultiAdapter extends BaseAdapter {
	LayoutInflater	mInflater;
	ArrayList<AddaccountListItem> arSrc;
	
	public MultiAdapter(Context context, ArrayList<AddaccountListItem> arItem){
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arSrc.size();
	}
	@Override
	public AddaccountListItem getItem(int position) {
		// TODO Auto-generated method stub
		return arSrc.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	} 
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}