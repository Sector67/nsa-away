package org.sector67.nsaaway;

import org.sector67.otp.key.FileKeyStore;
import org.sector67.otp.key.KeyException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
					Toast.makeText(getApplicationContext(), "Error creating test keys: " + e.getMessage(), Toast.LENGTH_LONG).show();
					//createAlert("Test Key Creation Error", "An error occured while creating test keys: " + e.getMessage());
				}
			}
		});
				
	}
	
	private void createAlert(String title, String message) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
		//TODO: extract string
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
	
	private void createTestKeys() throws KeyException {

    	FileKeyStore store = new FileKeyStore("/storage/extSdCard/Android/data/org.sector67.nsaaway/files/");
   		store.init();
   		store.deleteKey("encrypt-key");
   		store.deleteKey("decrypt-key");
		store.generateKey("encrypt-key", 1000);
   		store.copyKey("encrypt-key", "decrypt-key");
	}
}
