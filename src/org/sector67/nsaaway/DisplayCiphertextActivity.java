package org.sector67.nsaaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
