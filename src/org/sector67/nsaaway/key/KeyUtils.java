package org.sector67.nsaaway.key;

import org.sector67.nsaaway.SettingsActivity;
import org.sector67.nsaaway.file.FileUtils;
import org.sector67.nsaaway.file.FileUtilsFactory;
import org.sector67.otp.key.FileKeyStore;
import org.sector67.otp.key.KeyException;
import org.sector67.otp.key.KeyStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KeyUtils {
	public static KeyStore getKeyStore(Context context) throws KeyException {
		// /storage/extSdCard/Android/data/org.sector67.nsaaway/files/
		//Seems like this API needs to be called before directories can be created
		FileUtils fileUtils = FileUtilsFactory.getBuildAppropriateFileUtils(context);
		fileUtils.getExternalStorageDirs(context);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String keyStorePath = sharedPref.getString(SettingsActivity.KEY_PREF_KEYSTORE_PATH, "");


    	FileKeyStore store = new FileKeyStore(keyStorePath);
   		store.init();
   		return store;
	}
}
