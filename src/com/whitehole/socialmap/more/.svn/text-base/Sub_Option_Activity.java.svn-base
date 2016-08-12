package com.whitehole.socialmap.more;

import com.whitehole.socialmap.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Sub_Option_Activity extends PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    PreferenceManager prefMgr = getPreferenceManager();
	    prefMgr.setSharedPreferencesName("Pref");
	    prefMgr.setSharedPreferencesMode(MODE_WORLD_READABLE);

	    addPreferencesFromResource(R.xml.more_preferences);

    }
}
