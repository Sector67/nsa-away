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
import org.sector67.otp.cipher.CipherException;
import org.sector67.otp.cipher.OneTimePadCipher;
import org.sector67.otp.encoding.EncodingException;
import org.sector67.otp.encoding.SimpleBase16Encoder;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author scott.hasse@gmail.com
 * 
 */
public class DisplayPlaintextActivity extends Activity {

	private String ciphertext;
	private String keyName;
	private int offset;
	private int length;
	private KeyStore ks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		String syncConnPref = sharedPref.getString(
				SettingsActivity.KEY_PREF_SYNC_CONN, "");

		setContentView(R.layout.activity_display_plaintext);

		Button eraseKeyAndContinueButton = (Button) findViewById(R.id.eraseKeyAndContinueButton);

		Intent i = getIntent();
		ciphertext = i.getStringExtra(MainActivity.CIPHERTEXT_KEY);
		keyName = i.getStringExtra(MainActivity.KEYNAME_KEY);
		offset = i.getIntExtra(MainActivity.OFFSET_KEY, 0);

		TextView plaintextView = (TextView) findViewById(R.id.displayPlaintext);
		String result = "UNKNOWN";
		try {
			ks = KeyUtils.getKeyStore(getApplicationContext());
			OneTimePadCipher cipher = new OneTimePadCipher(ks);
			SimpleBase16Encoder encoder = new SimpleBase16Encoder();
			byte[] decoded = encoder.decode(ciphertext);
			length = decoded.length;
			result = cipher.decrypt(keyName, offset, decoded);
		} catch (KeyException e) {
			result = e.getMessage();
		} catch (CipherException e) {
			result = e.getMessage();
		} catch (EncodingException e) {
			result = e.getMessage();
		}
		/*
		 * FileUtils fileUtils =
		 * FileUtilsFactory.getBuildAppropriateFileUtils(getApplicationContext
		 * ()); result = result + fileUtils.getBuild() + "\n";
		 * 
		 * for (int j = 0; j < dirs.length; j++) { result = result + "\n" +
		 * dirs[j].getAbsolutePath(); }
		 */
		plaintextView.setText(result);

    	//Listen for a button event
    	eraseKeyAndContinueButton.setOnClickListener(new View.OnClickListener() {
        		public void onClick(View arg0) {
        			try {
						ks.eraseKeyBytes(keyName, offset, length);
    					Toast.makeText(getApplicationContext(), getString(R.string.message_key_bytes_erased), Toast.LENGTH_SHORT).show();
                		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                		startActivity(nextScreen); 
                	} catch (KeyException e) {
						AlertUtils.createAlert(getString(R.string.error_erasing_key_bytes), e.getMessage(), DisplayPlaintextActivity.this);
					}

        		}
    	});
	}

}
