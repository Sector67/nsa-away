/*
 * Copyright 2011 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sector67.nsaaway.ocr.complex;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Installs the language data required for OCR, and initializes the OCR engine using a background 
 * thread.
 */
final class OcrInitAsyncTask extends AsyncTask<String, String, Boolean> {
  private static final String TAG = OcrInitAsyncTask.class.getSimpleName();

  /** Suffixes of required data files for Cube. */
  private static final String[] CUBE_DATA_FILES = {
    ".cube.bigrams",
    ".cube.fold", 
    ".cube.lm", 
    ".cube.nn", 
    ".cube.params", 
    ".cube.size", // This file is not available for Hindi
    ".cube.word-freq", 
    ".tesseract_cube.nn", 
    ".traineddata"
  };

  private CaptureActivity activity;
  private Context context;
  private TessBaseAPI baseApi;
  private ProgressDialog dialog;
  private ProgressDialog indeterminateDialog;
  private final String languageCode;
  private String languageName;
  private int ocrEngineMode;

  /**
   * AsyncTask to asynchronously download data and initialize Tesseract.
   * 
   * @param activity
   *          The calling activity
   * @param baseApi
   *          API to the OCR engine
   * @param dialog
   *          Dialog box with thermometer progress indicator
   * @param indeterminateDialog
   *          Dialog box with indeterminate progress indicator
   * @param languageCode
   *          ISO 639-2 OCR language code
   * @param languageName
   *          Name of the OCR language, for example, "English"
   * @param ocrEngineMode
   *          Whether to use Tesseract, Cube, or both
   */
  OcrInitAsyncTask(CaptureActivity activity, TessBaseAPI baseApi, ProgressDialog dialog, 
      ProgressDialog indeterminateDialog, String languageCode, String languageName, 
      int ocrEngineMode) {
    this.activity = activity;
    this.context = activity.getBaseContext();
    this.baseApi = baseApi;
    this.dialog = dialog;
    this.indeterminateDialog = indeterminateDialog;
    this.languageCode = languageCode;
    this.languageName = languageName;
    this.ocrEngineMode = ocrEngineMode;
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    dialog.setTitle("Please wait");
    dialog.setMessage("Checking for data installation...");
    dialog.setIndeterminate(false);
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setCancelable(false);
    dialog.show();
    activity.setButtonVisibility(false);
  }

  /**
   * In background thread, perform required setup, and request initialization of
   * the OCR engine.
   * 
   * @param params
   *          [0] Pathname for the directory for storing language data files to the SD card
   */
  protected Boolean doInBackground(String... params) {
    // Check whether we need Cube data or Tesseract data.
    // Example Cube data filename: "tesseract-ocr-3.01.eng.tar"
    // Example Tesseract data filename: "eng.traineddata"
    String sourceFilenameBase = languageCode + ".traineddata";
    boolean isCubeSupported = false;
    for (String s : CaptureActivity.CUBE_SUPPORTED_LANGUAGES) {
      if (s.equals(languageCode)) {
        isCubeSupported = true;   
        //destinationFilenameBase = "tesseract-ocr-3.02." + languageCode + ".tar"; 
        //sourceFilenameBase = "tesseract-ocr-3.02." + languageCode; 
        sourceFilenameBase = "eng.cube"; 
      }
    }
    
    Log.e(TAG, "Is CUBE supported: " + isCubeSupported);

    // Check for, and create if necessary, folder to hold model data
    String destinationDirBase = params[0]; // The storage directory, minus the
                                           // "tessdata" subdirectory
    File tessdataDir = new File(destinationDirBase + File.separator + "tessdata");
    if (!tessdataDir.exists() && !tessdataDir.mkdirs()) {
      Log.e(TAG, "Couldn't make directory " + tessdataDir);
      return false;
    }

    File tesseractTestFile = new File(tessdataDir, languageCode + ".traineddata");

    // Check whether all Cube data files have already been installed
    boolean isAllCubeDataInstalled = false;
    if (isCubeSupported) {
      boolean isAFileMissing = false;
      File dataFile;
      for (String s : CUBE_DATA_FILES) {
        dataFile = new File(tessdataDir.toString() + File.separator + languageCode + s);
        if (!dataFile.exists()) {
          isAFileMissing = true;
        }
      }
      isAllCubeDataInstalled = !isAFileMissing;
    }

    // If language data files are not present, install them
    boolean installSuccess = false;
    //TODO: confirm path
    File sourceFile = new File("tessdata/");
    if (!tesseractTestFile.exists()
        || (isCubeSupported && !isAllCubeDataInstalled)) {
      Log.d(TAG, "Language data for " + languageCode + " not found in " + tessdataDir.toString());
      deleteCubeDataFiles(tessdataDir);

      // Check assets for language data to install.
      try {
        Log.e(TAG, "Checking for language data (" + sourceFilenameBase
            + ".zip) in application assets...");
        // Check for a file like "eng.traineddata.zip" or "tesseract-ocr-3.01.eng.tar.zip"
        installSuccess = installFromAssets(sourceFilenameBase + ".zip", tessdataDir, 
            sourceFile);
      } catch (IOException e) {
        Log.e(TAG, "IOException", e);
      } catch (Exception e) {
        Log.e(TAG, "Got exception", e);
      }

      if (!installSuccess) {
         Log.e(TAG, "Tessdata install from assets failed");
         return false;
      }

    } else {
      Log.d(TAG, "Language data for " + languageCode + " already installed in " 
          + tessdataDir.toString());
      installSuccess = true;
    }

    // If OSD data file is not present, download it
    File osdFile = new File(tessdataDir, CaptureActivity.OSD_FILENAME_BASE);
    boolean osdInstallSuccess = false;
    if (!osdFile.exists()) {
      // Check assets for language data to install. If not present, download from Internet
      languageName = "orientation and script detection";
      try {
        // Check for, and delete, partially-downloaded OSD files
        String[] badFiles = { CaptureActivity.OSD_FILENAME + ".gz.download", 
            CaptureActivity.OSD_FILENAME + ".gz", CaptureActivity.OSD_FILENAME };
        for (String filename : badFiles) {
          File file = new File(tessdataDir, filename);
          if (file.exists()) {
            file.delete();
          }
        }
        
        Log.d(TAG, "Checking for OSD data (" + CaptureActivity.OSD_FILENAME_BASE
            + ".zip) in application assets...");
        // Check for "osd.traineddata.zip"
        osdInstallSuccess = installFromAssets(CaptureActivity.OSD_FILENAME_BASE + ".zip", 
            tessdataDir, new File(CaptureActivity.OSD_FILENAME));
      } catch (IOException e) {
        Log.e(TAG, "IOException", e);
      } catch (Exception e) {
        Log.e(TAG, "Got exception", e);
      }

      if (!osdInstallSuccess) {
            Log.e(TAG, "OSD install from assets failed");
            return false;
      }

    } else {
      Log.d(TAG, "OSD file already present in " + tessdataDir.toString());
      osdInstallSuccess = true;
    }
    
    // Dismiss the progress dialog box, revealing the indeterminate dialog box behind it
    try {
      dialog.dismiss();
    } catch (IllegalArgumentException e) {
      // Catch "View not attached to window manager" error, and continue
    }

    // Initialize the OCR engine
    if (baseApi.init(destinationDirBase + File.separator, languageCode, ocrEngineMode)) {
      return installSuccess && osdInstallSuccess;
    }
    return false;
  }

  /**
   * Delete any existing data files for Cube that are present in the given directory. Files may be 
   * partially uncompressed files left over from a failed install, or pre-v3.01 traineddata files.
   * 
   * @param tessdataDir
   *          Directory to delete the files from
   */
  private void deleteCubeDataFiles(File tessdataDir) {
    File badFile;
    for (String s : CUBE_DATA_FILES) {
      badFile = new File(tessdataDir.toString() + File.separator + languageCode + s);
      if (badFile.exists()) {
        Log.d(TAG, "Deleting existing file " + badFile.toString());
        badFile.delete();
      }
      badFile = new File(tessdataDir.toString() + File.separator + "tesseract-ocr-3.01." 
          + languageCode + ".tar");
      if (badFile.exists()) {
        Log.d(TAG, "Deleting existing file " + badFile.toString());
        badFile.delete();
      }
    }
  }



  /**
   * Install a file from application assets to device external storage.
   * 
   * @param sourceFilename
   *          File in assets to install
   * @param modelRoot
   *          Directory on SD card to install the file to
   * @param destinationFile
   *          File name for destination, excluding path
   * @return True if installZipFromAssets returns true
   * @throws IOException
   */
  private boolean installFromAssets(String sourceFilename, File modelRoot,
      File destinationFile) throws IOException {
    Log.d(TAG, "Installing file from assets: " + sourceFilename + " to " + destinationFile);

    String extension = sourceFilename.substring(sourceFilename.lastIndexOf('.'), 
        sourceFilename.length());
    try {
      if (extension.equals(".zip")) {
        return installZipFromAssets(sourceFilename, modelRoot, destinationFile);
      } else {
        throw new IllegalArgumentException("Extension " + extension
            + " is unsupported.");
      }
    } catch (FileNotFoundException e) {
      Log.d(TAG, "Language not packaged in application assets: " + sourceFilename);
    }
    return false;
  }

  /**
   * Unzip the given Zip file, located in application assets, into the given
   * destination file.
   * 
   * @param sourceFilename
   *          Name of the file in assets
   * @param destinationDir
   *          Directory to save the destination file in
   * @param destinationFile
   *          File to unzip into, excluding path
   * @return
   * @throws IOException
   * @throws FileNotFoundException
   */
  private boolean installZipFromAssets(String sourceFilename,
      File destinationDir, File destinationFile) throws IOException,
      FileNotFoundException {
    // Attempt to open the zip archive
    publishProgress("Uncompressing data for " + languageName + "...", "0");
    ZipInputStream inputStream = new ZipInputStream(context.getAssets().open(sourceFilename));

    // Loop through all the files and folders in the zip archive (but there should just be one)
    for (ZipEntry entry = inputStream.getNextEntry(); entry != null; entry = inputStream
        .getNextEntry()) {
      destinationFile = new File(destinationDir, entry.getName());

      if (entry.isDirectory()) {
        destinationFile.mkdirs();
      } else {
        // Note getSize() returns -1 when the zipfile does not have the size set
        long zippedFileSize = entry.getSize();

        // Create a file output stream
        FileOutputStream outputStream = new FileOutputStream(destinationFile);
        final int BUFFER = 8192;

        // Buffer the output to the file
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BUFFER);
        int unzippedSize = 0;

        // Write the contents
        int count = 0;
        Integer percentComplete = 0;
        Integer percentCompleteLast = 0;
        byte[] data = new byte[BUFFER];
        while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
          bufferedOutputStream.write(data, 0, count);
          unzippedSize += count;
          percentComplete = (int) ((unzippedSize / (long) zippedFileSize) * 100);
          if (percentComplete > percentCompleteLast) {
            publishProgress("Uncompressing data for " + languageName + "...", 
                percentComplete.toString(), "0");
            percentCompleteLast = percentComplete;
          }
        }
        bufferedOutputStream.close();
      }
      inputStream.closeEntry();
    }
    inputStream.close();
    return true;
  }

  /**
   * Update the dialog box with the latest incremental progress.
   * 
   * @param message
   *          [0] Text to be displayed
   * @param message
   *          [1] Numeric value for the progress
   */
  @Override
  protected void onProgressUpdate(String... message) {
    super.onProgressUpdate(message);
    int percentComplete = 0;

    percentComplete = Integer.parseInt(message[1]);
    dialog.setMessage(message[0]);
    dialog.setProgress(percentComplete);
    dialog.show();
  }

  @Override
  protected void onPostExecute(Boolean result) {
    super.onPostExecute(result);
    
    try {
      indeterminateDialog.dismiss();
    } catch (IllegalArgumentException e) {
      // Catch "View not attached to window manager" error, and continue
    }

    if (result) {
      // Restart recognition
      activity.resumeOCR();
      activity.showLanguageName();
    } else {
      activity.showErrorMessage("Error", "The appropriate language data could not be installed to the device.  Please make sure it is supplied with the project in the appropriate location.");
    }
  }
}