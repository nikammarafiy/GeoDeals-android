/**
 * GeoMoby Pty Ltd
 * http://www.geomoby.com
 * Copyright GeoMoby Pty Ltd 2013
 * 
 * @author Chris Baudia
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.geomoby.geodeals;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.geomoby.logic.GeomobyStartService;
import com.geomoby.logic.GeomobyStopService;

public class DemoService extends Activity {

	public final static String TAG = "** MainActivity **";
	private static final String PREF = "GeoMobyPrefs";
	final String SETTING_TAGS = "tags";

	boolean isCheckedStatus;
	final String PREF_NAME="checked";
	public SharedPreferences spref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CompoundButton toggle = (CompoundButton) findViewById(R.id.togglebutton); // You can also use a normal "ToggleButton" - ToggleButton toggle = (ToggleButton) findViewById(R.id.togglebutton);

		spref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		isCheckedStatus = spref.getBoolean("check", false);  //default is false

		/*
		 * Set up toggle status
		 */
		if (!isCheckedStatus) 
			toggle.setChecked(false);
		else
			toggle.setChecked(true);

		/*
		 *  Save the tags in the GeoMoby shared preferences in private mode for the user. These tags will be used
		 *  to segment your audience when creating your proximity alerts. Please mase sure that they match with
		 *  the ones configured on your personal Admin Panel - www.geomoby.com/admin/account -> Business Tags
		 */
		SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		Log.d(TAG, mySharedPreferences.getAll().toString());
		SharedPreferences settingsActivity = getSharedPreferences(PREF, MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = settingsActivity.edit();
		// Build the string of tags
		String tags = mySharedPreferences.getString("gender_pref", "unknown")+","+mySharedPreferences.getString("age_pref", "unknown");
		// Commit the string
		prefEditor.putString(SETTING_TAGS, tags);
		prefEditor.commit();

		/*
		 *  Monitor the toggle - Our SDK will ensure that all services are running/stopping properly
		 */
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


				if (isChecked == false) {
					Log.d(TAG,"Stopping Notify Service");	
					// Stop the GeoMoby tracking service
					startService(new Intent(DemoService.this, GeomobyStopService.class));

					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", false);
					editor.commit();

				}else{
					// Start the GeoMoby tracking service
					startService(new Intent(DemoService.this, GeomobyStartService.class));
					Toast.makeText(getApplicationContext(), "Well Done! The service is now running in the background. You can leave the app!",Toast.LENGTH_LONG).show();
					SharedPreferences.Editor editor = spref.edit();
					editor.putBoolean("check", true);
					editor.commit();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		finish();
	}

	@Override
	protected void onPause() {
		onDestroy();
	}

	@Override
	public void onBackPressed() {
		onDestroy();
	}
}
