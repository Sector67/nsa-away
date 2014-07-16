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
import android.widget.TextView;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class DisplayCiphertextActivity extends Activity {
	
	private String plaintext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_ciphertext);
		
        Button sendAsKeystrokes = (Button) findViewById(R.id.sendAsKeystrokesButton);

        Intent i = getIntent();
        plaintext = i.getStringExtra("PLAINTEXT");

        TextView ciphertext = (TextView)findViewById(R.id.ciphertext);
        ciphertext.setText("AA BB CC (" + plaintext + ")");
		
        //Listen for a button event
        sendAsKeystrokes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                
                startActivity(nextScreen); 
            }
        });
	}
}
