package com.tv.seekers.pushnotification;

import android.util.Log;

import java.util.Enumeration;
import java.util.Vector;

public class GcmCommunicator  {


	// Creating instance variable of class
	private static GcmCommunicator ref = null;
	private Vector<GcmNotificationResponse> _iCallBack = new Vector<GcmNotificationResponse>();
	
	private String msg;
	private String notfi_id;
	private String keyword;
	private String key;
	private String game_id;
	/**
	 * Method returns object of class
	 */
	public static GcmCommunicator getInstance() {
		if (ref == null) {
			ref = new GcmCommunicator();

		}

		return ref;
	}
	
		
	
		


	public void handle(String msg,String notfi_id,String keyword,String key, String game_id) {
		this.msg=msg;
		this.notfi_id=notfi_id;
		this.keyword=keyword;
		this.key=key;
		this.game_id=game_id;
		notifyRegistered();
	
		
	}
		 /*
		  *  Register to recieve gcm response
		  *  .*/
		 
		
		public void registerForGcmResponse(GcmNotificationResponse callback) {
			_iCallBack.addElement(callback);
		
			
		}
		/**
		 * 
		 * Notify all registered users about server response with corresponding
		 * process id.
		 */
		public void notifyRegistered() {
			try {
				Enumeration<GcmNotificationResponse> enumeration = _iCallBack.elements();
				while (enumeration.hasMoreElements()) {
					GcmNotificationResponse callback = enumeration.nextElement();
					callback.gcmResponse(msg,notfi_id,keyword,key,game_id);
				}
				
			} catch (Exception e) {
				// DebugLog.LOGE("Exception: In notifyRegisteredUser " +
				String str = e.toString();
				Log.e("excep", str);
				System.out.println(e.toString());
			}
		}
		/**
		 * Deregister to recieve server response
		 */
		public void deRegisterForGcmResponse(GcmNotificationResponse callback) {
			_iCallBack.removeElement(callback);
		}

}
