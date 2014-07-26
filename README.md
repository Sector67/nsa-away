# nsa-away


NSA Away Android application repository for the Hackaday prize

## Build

 - clone the repo
 - from within the Android SDK:
   - File &raquo; Import &raquo; Existing Projects into Workspace
 - build and run!

 
Some design notes:

 - This application targets Android 4.x.  This keeps prototyping simple, 
   and as Android may not be the ultimate destination platform, investing 
   a lot of effort into compatibility did not make sense
 - File access: Our goal is to store keys on external SD cards.  Google 
   has greatly complicated secondary storage access with different approaches 
   for the various 4.x versions.  Some discussion of that can be found at:
 
    http://www.androidpolice.com/2014/02/17/external-blues-google-has-brought-big-changes-to-sd-cards-in-kitkat-and-even-samsung-may-be-implementing-them/
    http://www.doubleencore.com/2014/03/android-external-storage/
 
   Since we need to be able to clear used keys, write permission is needed.  
   Our initial strategy is for the application to request:
   
     android.permission.WRITE_EXTERNAL_STORAGE
     android.permission.READ_EXTERNAL_STORAGE
   
   and to allow the user to specify a file location for key storage.  For KitKat 
   the approach that makes the most sense will likely be to read and write keys 
   in something like:
    
   /storage/extSdCard/Android/data/org.sector67.nsaaway/files/ 
   
   this feels like a hack but should work well enough for the prototype.  This will
   require some coordination with the hardware key generator such that it will
   generate keys in this location.
  