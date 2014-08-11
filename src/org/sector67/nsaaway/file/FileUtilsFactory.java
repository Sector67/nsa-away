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

import android.content.Context;
import android.os.Build;

/**
 * This factory provides a build-appropriate file utilities implementation.  For more
 * details, see: 
 * 
 * http://stackoverflow.com/questions/11592820/writing-backwards-compatible-android-code
 * 
 * @author scott.hasse@gmail.com
 *
 */
public class FileUtilsFactory {
    public static FileUtils getBuildAppropriateFileUtils(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            return new KitKatFileUtils();
        } else {
            return new LegacyFileUtils();
        }

    }
}
