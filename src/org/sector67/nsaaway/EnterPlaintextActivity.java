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
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author scott.hasse@gmail.com
 * 
 */
public class EnterPlaintextActivity extends Activity implements
		KeyChooserDialogFragment.KeyChooserDialogListener {

	private String keyName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_plaintext);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String defaultEncryptKey = sharedPref.getString(
				SettingsActivity.KEY_PREF_DEFAULT_ENCRYPT_KEY, "");
		onKeyChoice(defaultEncryptKey);
		
		Button chooseKeyForEncryptionButton = (Button) findViewById(R.id.chooseKeyForEncryptionButton);
		Button encryptTextButton = (Button) findViewById(R.id.encryptTextButton);

		encryptTextButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent nextScreen = new Intent(getApplicationContext(),
						DisplayCiphertextActivity.class);

				EditText txtInput = (EditText) findViewById(R.id.plaintextEditText);
				String plaintext = txtInput.getText().toString();

				nextScreen.putExtra(MainActivity.PLAINTEXT_KEY, plaintext);
				nextScreen.putExtra(MainActivity.KEYNAME_KEY, keyName);
				startActivity(nextScreen);
			}
		});

		chooseKeyForEncryptionButton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						showKeyChooserDialog();
					}
				});

	}

	private void showKeyChooserDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new KeyChooserDialogFragment();
		dialog.show(getFragmentManager(), "NoticeDialogFragment");
	}

	/*
	 * This method is called when a key is chosen
	 */
	@Override
	public void onKeyChoice(String name) {
		TextView keyString = (TextView) findViewById(R.id.keyNameValue);
		this.keyName = name;
		keyString.setText(name);
	}

}
