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
import org.sector67.otp.envelope.EnvelopeUtils;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author scott.hasse@gmail.com
 * 
 */
public class DisplayCiphertextActivity extends Activity {
	// Class variables
	private String keyName;
	private String ciphertext = null;
	private String envelope = null;
	private int offset;
	private int length;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_display_ciphertext);
		
		// Grab all the buttons
		Button sendAsKeystrokes = (Button) findViewById(R.id.sendAsKeystrokesButton);
		Button copyToKeyboardButton = (Button) findViewById(R.id.copyToKeyboardButton);
		Button eraseKeyAndContinueButton = (Button) findViewById(R.id.eraseKeyAndContinueButton);

		if (ciphertext == null) {
			Intent i = getIntent();
			ciphertext = i.getStringExtra(MainActivity.CIPHERTEXT_KEY);
			length = i.getIntExtra(MainActivity.LENGTH_KEY, -1);
			offset = i.getIntExtra(MainActivity.OFFSET_KEY, -1);
			keyName = i.getStringExtra(MainActivity.KEYNAME_KEY);
			//TODO: move the new lines to a configuration or the EnvelopeUtils
			TextView ciphertextView = (TextView) findViewById(R.id.ciphertext);
			String result = EnvelopeUtils.getEnvelopeHeader();
			result = result + EnvelopeUtils.formatHeader("Offset", Integer.toString(offset));
			result = result + EnvelopeUtils.getBodySeparator();
			result = result + "\n";
			result = result + ciphertext;
			result = result + "\n";
			result = result + EnvelopeUtils.getEnvelopeFooter();
			envelope = result;
			ciphertextView.setText(result);
		}

		// Listen for a button event
		sendAsKeystrokes.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent nextScreen = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(nextScreen);
			}
		});

		// Listen for a button event
		copyToKeyboardButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Gets a handle to the clipboard service.
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData
						.newPlainText("simple text", envelope);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(getApplicationContext(),
						getString(R.string.message_copied_to_clipboard),
						Toast.LENGTH_SHORT).show();

			}
		});

		// Listen for a button event on the `erase key and continue` button.
		eraseKeyAndContinueButton
				.setOnClickListener(new View.OnClickListener() {
					public void onClick(View arg0) {
						try {
							KeyStore ks = KeyUtils.getKeyStore(getApplicationContext());
							ks.eraseKeyBytes(keyName, offset, length);
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.message_key_bytes_erased),
									Toast.LENGTH_SHORT).show();
							Intent nextScreen = new Intent(
									getApplicationContext(), MainActivity.class);
							startActivity(nextScreen);
						} catch (KeyException e) {
							AlertUtils
									.createAlert(
											getString(R.string.error_erasing_key_bytes),
											e.getMessage(),
											DisplayCiphertextActivity.this).show();
						}

					}
				});

	}

}
