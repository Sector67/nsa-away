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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class EnterCiphertextActivity extends Activity implements KeyChooserDialogFragment.KeyChooserDialogListener{
	
	private String keyName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_ciphertext);
				
        	Button chooseKeyForDecryptionButton = (Button) findViewById(R.id.chooseKeyForDecryptionButton);
        	Button decryptTextButton = (Button) findViewById(R.id.decryptTextButton);
        	Button buttonPasteFromClipboard = (Button) findViewById(R.id.buttonPasteFromClipboard);

        	decryptTextButton.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View arg0) {
                		Intent nextScreen = new Intent(getApplicationContext(), DisplayPlaintextActivity.class);
                
                		EditText txtInput = (EditText)findViewById(R.id.ciphertextEditText);
                		String ciphertext = txtInput.getText().toString();
                		EditText offsetInput = (EditText)findViewById(R.id.keyOffsetEditText);
                		Integer offset = Integer.parseInt(offsetInput.getText().toString());
                
                		nextScreen.putExtra(MainActivity.CIPHERTEXT_KEY, ciphertext);
                		nextScreen.putExtra(MainActivity.KEYNAME_KEY, keyName);
                		nextScreen.putExtra(MainActivity.OFFSET_KEY, offset);
                		startActivity(nextScreen); 
            		}
        	});
        	
        	//Listen for a button event
        	buttonPasteFromClipboard.setOnClickListener(new View.OnClickListener() {
            		public void onClick(View arg0) {
            			// Gets a handle to the clipboard service.
            			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    	ClipData clip = clipboard.getPrimaryClip();
                    	
                    
            			EditText txtInput = (EditText)findViewById(R.id.ciphertextEditText);
            			txtInput.setText(clip.getItemAt(0).getText().toString());
    					//Toast.makeText(getApplicationContext(), getString(R.string.message_copied_to_clipboard), Toast.LENGTH_SHORT).show();

            		}
        	});
        	

        	chooseKeyForDecryptionButton.setOnClickListener(new View.OnClickListener() {
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
    	TextView keyString = (TextView)findViewById(R.id.keyNameValue);
		this.keyName = name;
		keyString.setText(name);
	}


}
