package com.tv.seekers.pushnotification;

public interface GcmNotificationResponse {
	public void gcmResponse(String msg, String notification_id, String keyword, String key, String game_id);

}
