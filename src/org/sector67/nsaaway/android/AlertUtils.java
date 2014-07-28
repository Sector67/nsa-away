package org.sector67.nsaaway.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertUtils {

	public static AlertDialog createAlert(String title, String message, Context context) {
		AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
		return alert11; 

	}
	
}
