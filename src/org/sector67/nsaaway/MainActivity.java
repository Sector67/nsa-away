/*
 * Copyright 2014 individual contributors as indicated by the @author 
 * tags
 * 
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.sector67.nsaaway;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class MainActivity extends Activity {

	//constants that various activities can use to reference passed data
	public static final String PLAINTEXT_KEY = "PLAINTEXT";
	public static final String CIPHERTEXT_KEY = "CIPHERTEXT";
	public static final String KEYNAME_KEY = "KEYNAME";
	public static final String OFFSET_KEY = "OFFSET";
	public static final String LENGTH_KEY = "LENGTH";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		setContentView(R.layout.activity_main);

		/*
		if (savedInstanceState == null) {

			getFragmentManager().beginTransaction()
					.add(R.id.mainRelativeLayout, new PlaceholderFragment()).commit();
		}
		*/
		
        Button startEncryptionButton = (Button) findViewById(R.id.chooseKeyForEncryptionButton);
        Button startDecryptionButton = (Button) findViewById(R.id.startDecryptionButton);
        Button startKeyManagementButton = (Button) findViewById(R.id.startKeyManagementButton);
        Button startOCRButton = (Button) findViewById(R.id.startOCRButton);
        

		
	    // Listen for a button click on the encryption button
		startEncryptionButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View arg0) {
	                Intent nextScreen = new Intent(getApplicationContext(), EnterPlaintextActivity.class); 
	                startActivity(nextScreen); 
	            }
	    });
		
	    // Listen for a button click on the decryption button
		startDecryptionButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View arg0) {
	                Intent nextScreen = new Intent(getApplicationContext(), EnterCiphertextActivity.class); 
	                startActivity(nextScreen); 
	            }
	    });
		
		// Listen for a button click on the key-manager button
		startKeyManagementButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent nextScreen = new Intent(getApplicationContext(), KeyManagerActivity.class);
				startActivity(nextScreen);
			}
		});

		// Listen for a button click on the key-manager button
		startOCRButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent nextScreen = new Intent(getApplicationContext(), SimpleOCRActivity.class);
				startActivity(nextScreen);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent preferencesIntent = new Intent(this, SettingsActivity.class);
			startActivity(preferencesIntent);			
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
