/*
 * Author: Aidan Evans (Stackoverflow)
 * Date: 3/23/2019
 */


package commands;


import com.cholnhial.mediaokraclient.MediaOkraClient;

import static com.cholnhial.mediaokraclient.MediaOkraClient.isRunningOnWindows;

public class MediaKeys {
	
	
	//loads library from "MediaKeys.dll"
	static {
		if(isRunningOnWindows()) {
			MediaOkraClient.loadFromJar();
		} else {
			MediaOkraClient.copyMediaOkraPythonScriptForMacToTmpDir();
		}
	}
	

	
	public static native void volumeMute();
	
	public static native void volumeDown();
	
	public static native void volumeUp();
	
	
	public static native void songPrevious();
	
	public static native void songNext();
	
	public static native void songPlayPause();
	
	


}
