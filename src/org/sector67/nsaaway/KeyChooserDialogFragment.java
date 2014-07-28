package org.sector67.nsaaway;

import java.util.List;

import org.sector67.nsaaway.android.AlertUtils;
import org.sector67.nsaaway.key.KeyUtils;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class KeyChooserDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //obtain an array of key names
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        KeyStore ks = KeyUtils.getKeyStore(getActivity().getApplicationContext());
	        List<String> keyNames = ks.listKeys();
	        String[] keyNamesArray = (String[]) keyNames.toArray();
	        builder.setTitle(R.string.title_choose_a_key)
	               .setItems(keyNamesArray, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int which) {
	                   // The 'which' argument contains the index position
	                   // of the selected item
	               }
	        });
	        return builder.create();
        } catch (KeyException e) {
        	//display an error alert instead
        	AlertDialog dialog = AlertUtils.createAlert("Key Error", e.getMessage(), getActivity().getApplicationContext());
        	return dialog;
        }
    }
}
