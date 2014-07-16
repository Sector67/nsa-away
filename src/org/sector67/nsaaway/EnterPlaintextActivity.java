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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class EnterPlaintextActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_plaintext);
		
        Button chooseKeyForEncryptionButton = (Button) findViewById(R.id.chooseKeyForEncryptionButton);

        //Listen for a button event
        chooseKeyForEncryptionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), KeyChooserActivity.class);
                
                EditText txtInput = (EditText)findViewById(R.id.plaintextEditText);
                String plaintext = txtInput.getText().toString();
                
                nextScreen.putExtra("PLAINTEXT", plaintext);
                startActivity(nextScreen); 
            }
        });
	}
}
