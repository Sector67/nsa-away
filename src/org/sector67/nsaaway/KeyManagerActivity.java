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

import java.util.Iterator;
import java.util.List;

import org.sector67.otp.key.FileKeyStore;
import org.sector67.otp.key.KeyException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class KeyManagerActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_manager);

        Button createTestKeys = (Button) findViewById(R.id.createTestKeysButton);
        Button listKeys = (Button) findViewById(R.id.listKeysButton);
        
		// Listen for a button event
        createTestKeys.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
					createTestKeys();
					Toast.makeText(getApplicationContext(), getString(R.string.message_test_keys_created), Toast.LENGTH_SHORT).show();
				} catch (KeyException e) {
					//Toast.makeText(getApplicationContext(), "Error creating test keys: " + e.getMessage(), Toast.LENGTH_LONG).show();
					createAlert(getString(R.string.title_test_keys_creation_error), getString(R.string.message_test_keys_creation_error) + ": " + e.getMessage(), KeyManagerActivity.this);
				}
			}
		});

		// Listen for a button event
        listKeys.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
					List<String> keys = listKeys();
					String result = "";
					for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
						String key = (String) iterator.next();
						result = result + key + "\n";
					}
					createAlert(getString(R.string.title_key_list), result, KeyManagerActivity.this);
				} catch (KeyException e) {
					//Toast.makeText(getApplicationContext(), "Error creating test keys: " + e.getMessage(), Toast.LENGTH_LONG).show();
					createAlert(getString(R.string.title_key_list_error), getString(R.string.message_key_list_error) + ": " + e.getMessage(), KeyManagerActivity.this);
				}
			}
		});
	}
	
	private void createAlert(String title, String message, Context context) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
		builder1.setTitle(title);
		builder1.setMessage(message);
		builder1.setCancelable(true);
		builder1.setNeutralButton(android.R.string.ok,
		        new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int id) {
		        dialog.cancel();
		    }
		});
		AlertDialog alert11 = builder1.create();
		alert11.show(); 

	}

	private List<String> listKeys() throws KeyException {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String keyStorePath = sharedPref.getString(SettingsActivity.KEY_PREF_KEYSTORE_PATH, "");


    	FileKeyStore store = new FileKeyStore(keyStorePath);
   		store.init();
   		List<String> keys = store.listKeys();
   		return keys;
	}

	
	private void createTestKeys() throws KeyException {
		// /storage/extSdCard/Android/data/org.sector67.nsaaway/files/
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String keyStorePath = sharedPref.getString(SettingsActivity.KEY_PREF_KEYSTORE_PATH, "");


    	FileKeyStore store = new FileKeyStore(keyStorePath);
   		store.init();
   		store.deleteKey("encrypt-key");
   		store.deleteKey("decrypt-key");
   		store.deleteKey("alice-key");
   		store.deleteKey("bob-key");
		store.generateKey("encrypt-key", 1000);
   		store.copyKey("encrypt-key", "decrypt-key");
		store.generateKey("alice-key", 1000);
		store.generateKey("bob-key", 1000);
	}
}
