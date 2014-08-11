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

import java.util.List;

import org.sector67.nsaaway.android.AlertUtils;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class KeyChooserDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //obtain an array of key names
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        KeyStore ks = KeyUtils.getKeyStore(getActivity().getApplicationContext());
	        List<String> keyNames = ks.listKeys();
	        final String[] keyNamesArray = new String[keyNames.size()];
	        for (int i=0; i < keyNames.size(); i++) {
	        	keyNamesArray[i] = keyNames.get(i);
			}
	        builder.setTitle(R.string.title_choose_a_key)
	               .setItems(keyNamesArray, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int which) {
		                   // The 'which' argument contains the index position
		                   // of the selected item
	                	   mListener.onKeyChoice(keyNamesArray[which]);
	               }
	        });
	        return builder.create();
        } catch (KeyException e) {
        	e.printStackTrace();//display an error alert instead
        	AlertDialog dialog = AlertUtils.createAlert("Key Error", e.getMessage(), getActivity());
        	return dialog;
        }
    }
    
    /**
     * An listener interface to provide events back
     */
    public interface KeyChooserDialogListener {
        public void onKeyChoice(String name);
    }
    
    // Use this instance of the interface to deliver action events
    KeyChooserDialogListener mListener;
    
    // Override the Fragment.onAttach() method to instantiate the KeyChooserDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (KeyChooserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement KeyChooserDialogListener");
        }
    }
}
