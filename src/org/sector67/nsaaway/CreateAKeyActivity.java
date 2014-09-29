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

import java.security.SecureRandom;
import java.util.Random;
import org.sector67.nsaaway.android.AlertUtils;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author scott.hasse@gmail.com
 * 
 */
public class CreateAKeyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_a_key);
		
		// Create a reference to the button to create a new key
		Button createNewKeyButton = (Button) findViewById(R.id.createNewKeyButton);
		
		// On clicking the `create new key` button
		createNewKeyButton.setOnClickListener(new View.OnClickListener() {
			// Don't complain about it not being a secure random
			@SuppressLint("TrulyRandom")
			public void onClick(View arg0) {
				try {
					// New variable to store the random object
					Random sr = null;
					//TODO bounds and null checking
					
					// Get the text editor field for the key name
					EditText newKeyNameEditText = (EditText) findViewById(R.id.newKeyNameEditText);
					String keyName = newKeyNameEditText.getText().toString(); // Make it into a string
					// If there's no string
					if (keyName == null || keyName.equals("")) { 
						// You need to specify a name
						throw new IllegalArgumentException("You must specify a key name");
					}
					// Get text field for key size
					EditText newKeySizeEditText = (EditText) findViewById(R.id.newKeySizeEditText);
					// Get the key length in an int
					int keyLength = Integer.parseInt(newKeySizeEditText.getText().toString());
					// If the key length is < 1
					if (keyLength < 1) {
						// It needs to be more
						throw new IllegalArgumentException("You must specify a valid key length");
					}
					// Get text field for key seed
					EditText newKeySeedEditText = (EditText) findViewById(R.id.newKeySeedEditText);
					String newKeySeedText = newKeySeedEditText.getText().toString(); // Get key seed in a string
					// If it's null
					if (newKeySeedText == null || newKeySeedText.equals("")) {
						// We don't need a seed
						sr = new SecureRandom();						
					} else {
						//This is for testing, SecureRandom produces different randomness even with
						//the same seed.  Again for testing only, not secure
						int keySeed = Integer.parseInt(newKeySeedText);
						sr = new Random(keySeed);						
					}
					// Zero the key offset
					int keyOffset = 0;
					// Get new key offset text field
					EditText newKeyOffsetEditText = (EditText) findViewById(R.id.newKeyOffsetEditText);
					String newKeyOffsetText = newKeyOffsetEditText.getText().toString(); // Put key offset in a string
					// If the key offset's not nothing
					if (newKeyOffsetText != null && !newKeyOffsetText.equals("")) {
						// Parse it
						keyOffset = Integer.parseInt(newKeyOffsetText);
					}

					// Variable for the bytes of the key
					byte[] keyBytes = new byte[keyLength];
					// Fill it up with random bytes
					sr.nextBytes(keyBytes);
					// Put it in the keystore
					KeyStore ks = KeyUtils.getKeyStore(getApplicationContext());
					ks.addKey(keyName, keyBytes, keyOffset);
					// Pop toast notification about newly created message
					Toast.makeText(getApplicationContext(), getString(R.string.new_key_created_message), Toast.LENGTH_SHORT).show();
				} catch (IllegalArgumentException e) {
					//this also catches NumberFormatException
					// Do an error toast on IllegalArgumentExecption
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					AlertUtils
					.createAlert(
							getString(R.string.error_creating_key_message),
							e.getMessage(),
							CreateAKeyActivity.this).show();
					return;
				} catch (KeyException e) {
					// Do an error toast on KeyException
					AlertUtils
					.createAlert(
							getString(R.string.error_creating_key_message),
							e.getMessage(),
							CreateAKeyActivity.this).show();
					return;
				}
				//Intent nextScreen = new Intent(getApplicationContext(),
				//		KeyManagerActivity.class);
								
				//startActivity(nextScreen);
			}
		});

	}



}
