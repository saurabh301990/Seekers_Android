package com.tv.seekers.pushnotification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tv.seekers.menu.MainActivity;

import java.io.IOException;

public class GCMPushNotifHandler {

	private static final String TAG = "PushNotifTest";

	// TODO - this must be in a common single place in the app
	// private static final String SERVER_BASE_URL =
	// "https://slbt-staging.appspot.com";

	// private static final String REGISTER_SERVER_URL = SERVER_BASE_URL +
	// "/registerpushnotifid";
	// private static final String UNREGISTER_SERVER_URL = SERVER_BASE_URL +
	// "/unregisterpushnotifid";

	// TODO - for test only
//	private static final String USER_ID = "EM8847022711166435";
//	private static final String SECRET_CODE = "6da4a255d3324757ffc604def841edb06c861001";

	private static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	
	private static final String GAE_SENDER_ID = "48082391846";

	private GoogleCloudMessaging gcm;
	private Activity context;

	public GCMPushNotifHandler(Activity context) {
		this.context = context;
		this.gcm = GoogleCloudMessaging.getInstance(context);
	}

	// TODO - to be invoked when the application launches if it already has
	// UserID and SecretCode,
	// or just after success login, when it gets the User ID and SecreteCode
	public void register() {
		deleteRegIdFromSharedPref();
		String regId = getRegistrationId();

		if (regId == null || regId.isEmpty()) {
			Log.d(TAG,
					"Reg ID not found on shard pref - registering in background.");
			registerInBackground();
		} else {
			Log.d(TAG, "Registration found on local storage.");
			showToastMsg("App already registered");
		}
	}

	// TODO - to be invoked when user does a logout

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}

				int retryCount = 0;
				boolean success = false;

				while (!success) {
					try {
						String regId = gcm.register(GAE_SENDER_ID);
						storeRegistrationId(regId);

						success = true;

					} catch (IOException e) {
						if (retryCount < ServerUtil.RETRY_COUNT) {
							Log.w(TAG, "IOException - (" + e.toString()
									+ ") - Retrying soon...");
							try 
							{
								Thread.sleep(ServerUtil.RETRY_INTERVAL);
							} 
							catch (InterruptedException ex)
							{
							}
						} 
						else
						{
							Log.e(TAG, "IOException - All retries failed.");
							// showToastMsg("ERROR : Could not get Registration ID");
							break;
						}
						retryCount++;
					}
				}

				return null;
			}
		}.execute(null, null, null);
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one. If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or null if there is no existing registration ID.
	 */

	public String getRegistrationId() {
		final SharedPreferences prefs = getGcmPreferences();
		String registrationId = prefs.getString(PROPERTY_REG_ID, null);
		if (registrationId == null || registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return null;
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			deleteRegIdFromSharedPref();
			return null;
		}
		return registrationId;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 */
	private void storeRegistrationId(String regId) {
		final SharedPreferences prefs = getGcmPreferences();
		int appVersion = getAppVersion();
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private void deleteRegIdFromSharedPref() {
		final SharedPreferences prefs = getGcmPreferences();
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(PROPERTY_REG_ID);
		editor.remove(PROPERTY_APP_VERSION);
		editor.commit();
	}

	private int getAppVersion() {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	// /////change
	private SharedPreferences getGcmPreferences()
	{
		return context.getSharedPreferences(
				MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private void showToastMsg(final String msg) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
			}
		});
	}
}
