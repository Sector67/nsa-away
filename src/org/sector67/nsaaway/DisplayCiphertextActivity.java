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

import java.io.File;

import org.sector67.nsaaway.file.FileUtils;
import org.sector67.nsaaway.file.FileUtilsFactory;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.cipher.CipherException;
import org.sector67.otp.cipher.OneTimePadCipher;
import org.sector67.otp.encoding.EncodingException;
import org.sector67.otp.encoding.SimpleBase16Encoder;
import org.sector67.otp.key.FileKeyStore;
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

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class DisplayCiphertextActivity extends Activity {
	
	private String plaintext;
	private String keyName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String syncConnPref = sharedPref.getString(SettingsActivity.KEY_PREF_SYNC_CONN, "");
		
		setContentView(R.layout.activity_display_ciphertext);
		
        	Button sendAsKeystrokes = (Button) findViewById(R.id.sendAsKeystrokesButton);
		
        	Intent i = getIntent();
        	plaintext = i.getStringExtra(MainActivity.PLAINTEXT_KEY);
        	keyName = i.getStringExtra(MainActivity.KEYNAME_KEY);

        	TextView ciphertext = (TextView)findViewById(R.id.ciphertext);
        	String result = "UNKNOWN";
        	try {
				KeyStore ks = KeyUtils.getKeyStore(getApplicationContext());
				OneTimePadCipher cipher = new OneTimePadCipher(ks);
				byte[] encrypted = cipher.encrypt(keyName, plaintext);
				SimpleBase16Encoder encoder = new SimpleBase16Encoder();
				encoder.setMinorChunkSeparator(" ");
				result = encoder.encode(encrypted);
			} catch (KeyException e) {
				result = e.getMessage();
			} catch (CipherException e) {
				result = e.getMessage();
			} catch (EncodingException e) {
				result = e.getMessage();
			}
           	/*
        	FileUtils fileUtils = FileUtilsFactory.getBuildAppropriateFileUtils(getApplicationContext());
        	result = result + fileUtils.getBuild() + "\n";

        	for (int j = 0; j < dirs.length; j++) {
    			result = result +  "\n" + dirs[j].getAbsolutePath();
    		}
    		*/
        	ciphertext.setText(result);
		
        	//Listen for a button event
        	sendAsKeystrokes.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View arg0) {
                		Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                		startActivity(nextScreen); 
            		}
        	});
	}

}
