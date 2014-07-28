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

import org.sector67.nsaaway.android.AlertUtils;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.key.FileKeyStore;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

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
        
		// Listen for a button event
        createTestKeys.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
					createTestKeys();
					Toast.makeText(getApplicationContext(), "Test keys created", Toast.LENGTH_SHORT).show();
				} catch (KeyException e) {
					//Toast.makeText(getApplicationContext(), "Error creating test keys: " + e.getMessage(), Toast.LENGTH_LONG).show();
					AlertUtils.createAlert("Test Key Creation Error", "An error occured while creating test keys: " + e.getMessage(), KeyManagerActivity.this).show();
				}
			}
		});
				
	}
	
	private void createTestKeys() throws KeyException {
		KeyStore store = KeyUtils.getKeyStore(this);
   		store.deleteKey("encrypt-key");
   		store.deleteKey("decrypt-key");
		store.generateKey("encrypt-key", 1000);
   		store.copyKey("encrypt-key", "decrypt-key");
	}
}
