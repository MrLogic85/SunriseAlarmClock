package com.sleepyduck.sunrisealarmclock;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import com.sleepyduck.xmlandroidlib.XMLElement;
import com.sleepyduck.xmlandroidlib.XMLElementFactory;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;

public abstract class HandleSunTimeRequest implements Runnable {
	protected Location mLocation;
	
	public void setLocation (Location location) {
		mLocation = location;
	}

	protected String getSunriseData(Activity activity) {
		InputStream is = null;
		try {
			ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
			int counter = 0;
			while (counter < 30 && !cm.getActiveNetworkInfo().isConnected()) {
				try {
					counter++;
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					if (activity.isFinishing()) {
						return null;
					}
				}
			}
			if (cm.getActiveNetworkInfo().isConnected()) {
				URL url = new URL("http://www.earthtools.org/sun/"
						+ mLocation.getLatitude() + "/"
						+ mLocation.getLongitude() + "/25/6/99/1");
				is = url.openStream();
				List<XMLElement> xmlElemts = XMLElementFactory
						.BuildFromReader(new InputStreamReader(is));
				if (xmlElemts.size() > 0) {
					XMLElement head = xmlElemts.get(0);
					XMLElement sunriseElement = head.getElement("sunrise");
					String sunriseData = sunriseElement.getData();
					return sunriseData;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}
}
