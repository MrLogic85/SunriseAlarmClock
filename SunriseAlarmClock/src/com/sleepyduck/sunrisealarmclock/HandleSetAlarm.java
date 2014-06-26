package com.sleepyduck.sunrisealarmclock;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;

public abstract class HandleSetAlarm implements Runnable {
	protected Calendar mSunrise;

	public void setSunrise(Calendar sunrise) {
		mSunrise = sunrise;
	}
	
	protected void setAlarm(Context context, boolean on) {
		if (on) {
			setAlarmOn(context);
		} else {
			setAlarmOff();
		}
	}

	private void setAlarmOff() {
	}

	private void setAlarmOn(Context context) {
		Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
        openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, mSunrise.get(Calendar.HOUR_OF_DAY));
        openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, mSunrise.get(Calendar.MINUTE));
        context.startActivity(openNewAlarm);
	}
}
