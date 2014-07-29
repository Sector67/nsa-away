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

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
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
