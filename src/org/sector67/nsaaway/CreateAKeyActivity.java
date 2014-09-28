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

		Button createNewKeyButton = (Button) findViewById(R.id.createNewKeyButton);

		createNewKeyButton.setOnClickListener(new View.OnClickListener() {
			@SuppressLint("TrulyRandom")
			public void onClick(View arg0) {
				try {
					Random sr = null;
					//TODO bounds and null checking
					EditText newKeyNameEditText = (EditText) findViewById(R.id.newKeyNameEditText);
					String keyName = newKeyNameEditText.getText().toString();
					if (keyName == null || keyName.equals("")) {
						throw new IllegalArgumentException("You must specify a key name");
					}
					EditText newKeySizeEditText = (EditText) findViewById(R.id.newKeySizeEditText);
					int keyLength = Integer.parseInt(newKeySizeEditText.getText().toString());
					if (keyLength < 1) {
						throw new IllegalArgumentException("You must specify a valid key length");
					}
					EditText newKeySeedEditText = (EditText) findViewById(R.id.newKeySeedEditText);
					String newKeySeedText = newKeySeedEditText.getText().toString();
					if (newKeySeedText == null || newKeySeedText.equals("")) {
						sr = new SecureRandom();						
					} else {
						//This is for testing, SecureRandom produces different randomness even with
						//the same seed.  Again for testing only, not secure
						int keySeed = Integer.parseInt(newKeySeedText);
						sr = new Random(keySeed);						
					}
					int keyOffset = 0;
					EditText newKeyOffsetEditText = (EditText) findViewById(R.id.newKeyOffsetEditText);
					String newKeyOffsetText = newKeyOffsetEditText.getText().toString();
					if (newKeyOffsetText != null && !newKeyOffsetText.equals("")) {
						keyOffset = Integer.parseInt(newKeyOffsetText);
					}

					byte[] keyBytes = new byte[keyLength];
					sr.nextBytes(keyBytes);
					KeyStore ks = KeyUtils.getKeyStore(getApplicationContext());
					ks.addKey(keyName, keyBytes, keyOffset);
					Toast.makeText(getApplicationContext(), getString(R.string.new_key_created_message), Toast.LENGTH_SHORT).show();
				} catch (IllegalArgumentException e) {
					//this also catches NumberFormatException
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
					AlertUtils
					.createAlert(
							getString(R.string.error_creating_key_message),
							e.getMessage(),
							CreateAKeyActivity.this).show();
					return;
				} catch (KeyException e) {
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
