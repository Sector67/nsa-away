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
package org.sector67.nsaaway.file;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

/**
 * 
 * @author scott.hasse@gmail.com
 *
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class KitKatFileUtils implements FileUtils {
	
	@Override
	public File[] getExternalStorageDirs(Context context) {
    	File[] externalDirs = context.getExternalFilesDirs(null);
    	return externalDirs;
	}

	@Override
	public String getBuild() {
		// TODO Auto-generated method stub
		return "KitKat";
	}
}
