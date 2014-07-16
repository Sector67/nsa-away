package org.sector67.nsaaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
