package com.sleepyduck.sunrisealarmclock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import android.content.Context;

public abstract class HandleSunTimeData implements Runnable {
	private static final String DATA_FILE = "data.txt";
	protected String mSunrise;

	public void setSunrise(String sunrise) {
		mSunrise = sunrise;
	}

	protected Calendar getNextSunrise(Context context) {
		if (mSunrise == null) {
			loadCachedSunrise(context);
		}

		cacheSunrise(context);
		
		String[] time = mSunrise.split(":");
		int hour = Integer.valueOf(time[0]);
		int minute = Integer.valueOf(time[1]);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY)*60 + calendar.get(Calendar.MINUTE) <= hour*60 + minute) {
			calendar.add(Calendar.DATE, 1);
		}
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		
		return calendar;
	}

	private void cacheSunrise(Context context) {
		File file = new File(DATA_FILE);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		BufferedWriter bw = null;
		try {
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(mSunrise);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void loadCachedSunrise(Context context) {
		File file = new File(DATA_FILE);
		if (file.exists()) {
			FileInputStream fis = null;
			try {
				fis = context.openFileInput(DATA_FILE);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);
				mSunrise = br.readLine();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
					}
				}
			}
		}

		if (mSunrise == null || mSunrise.length() != 8) {
			mSunrise = "06:00:00";
		}
	}
}
