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
package org.sector67.nsaaway.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
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
