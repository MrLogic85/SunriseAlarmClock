package com.sleepyduck.sunrisealarmclock;

import java.util.Calendar;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.Toast;

public class AlarmActivity extends Activity {

	private Handler mHandler;

	private HandleSunTimeRequest mHandleSunTimeRequest = new HandleSunTimeRequest() {

		@Override
		public void run() {
			String sunriseData = getSunriseData(AlarmActivity.this);
			toast("Sunrise at " + sunriseData);
			mHandleSunTimeData.setSunrise(sunriseData);
			mHandler.post(mHandleSunTimeData);
		}
	};

	private HandleSunTimeData mHandleSunTimeData = new HandleSunTimeData() {
		@Override
		public void run() {
			Calendar nextSunrise = getNextSunrise(AlarmActivity.this);
			toast("Sunrise at " + nextSunrise.get(Calendar.HOUR_OF_DAY) + ":"
					+ nextSunrise.get(Calendar.MINUTE));
			mHandleSetAlarm.setSunrise(nextSunrise);
			mHandler.post(mHandleSetAlarm);
		}
	};
	
	private HandleSetAlarm mHandleSetAlarm = new HandleSetAlarm() {
		@Override
		public void run() {
			setAlarm(AlarmActivity.this, true);
		}
	};

	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			toast("Location is " + location.getLatitude() + ", "
					+ location.getLongitude());

			mHandleSunTimeRequest.setLocation(location);
			mHandler.post(mHandleSunTimeRequest);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		HandlerThread handlerThread = new HandlerThread("background");
		handlerThread.start();
		mHandler = new Handler(handlerThread.getLooper());
		getLocation(mLocationListener);
	}

	private void getLocation(LocationListener locationListener) {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location lastLocation = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (lastLocation != null) {
			locationListener.onLocationChanged(lastLocation);
		} else {
			locationManager.requestSingleUpdate(
					LocationManager.NETWORK_PROVIDER, locationListener, null);
		}
	}

	protected void toast(final String message) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(AlarmActivity.this, message, Toast.LENGTH_LONG)
						.show();
			}
		});
	}
}
