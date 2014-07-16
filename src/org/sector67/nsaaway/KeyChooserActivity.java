package org.sector67.nsaaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class KeyChooserActivity extends Activity {

	private String plaintext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_key_chooser);
		
        Button encryptButton = (Button) findViewById(R.id.encryptButton);
        Intent i = getIntent();
        plaintext = i.getStringExtra("PLAINTEXT");
        
        //Listen for a button event
		encryptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent nextScreen = new Intent(getApplicationContext(), DisplayCiphertextActivity.class);
                
                nextScreen.putExtra("PLAINTEXT", plaintext);
                startActivity(nextScreen); 
            }
        });
	}
}
