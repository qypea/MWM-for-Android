                                                                     
                                                                     
                                                                     
                                             
 /*****************************************************************************
  *  Copyright (c) 2011 Meta Watch Ltd.                                       *
  *  www.MetaWatch.org                                                        *
  *                                                                           *
  =============================================================================
  *                                                                           *
  *  Licensed under the Apache License, Version 2.0 (the "License");          *
  *  you may not use this file except in compliance with the License.         *
  *  You may obtain a copy of the License at                                  *
  *                                                                           *
  *    http://www.apache.org/licenses/LICENSE-2.0                             *
  *                                                                           *
  *  Unless required by applicable law or agreed to in writing, software      *
  *  distributed under the License is distributed on an "AS IS" BASIS,        *
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
  *  See the License for the specific language governing permissions and      *
  *  limitations under the License.                                           *
  *                                                                           *
  *****************************************************************************/

 /*****************************************************************************
  * IntentReceiver.java                                                       *
  * IntentReceiver                                                            *
  * Notifications receiver                                                    *
  *                                                                           *
  *                                                                           *
  *****************************************************************************/

package org.metawatch.manager;

import java.util.TimeZone;

import org.damazio.notifier.event.receivers.mms.EncodedStringValue;
import org.damazio.notifier.event.receivers.mms.PduHeaders;
import org.damazio.notifier.event.receivers.mms.PduParser;
import org.metawatch.manager.MetaWatchService.Preferences;
import org.metawatch.manager.Monitors.WeatherData;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class IntentReceiver extends BroadcastReceiver {
		
	static String lastTimeZoneName = "";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();		
		if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): received intent, action='"+action+"'");
		
		Bundle b = intent.getExtras();
		if (b != null) {
			for (String key : b.keySet()) {
				if (Preferences.logging) Log.d(MetaWatch.TAG,
						"extra: " + key + " = '" + b.get(key) + "'");
			}
			String dataString = intent.getDataString();
			if (Preferences.logging) Log.d(MetaWatch.TAG, "dataString: "
					+ (dataString == null ? "null" : "'" + dataString + "'"));
		}
		
		if (action.equals("android.intent.action.PROVIDER_CHANGED")) {

			if (!MetaWatchService.Preferences.notifyGmail)
				return;

			if (!Utils.isGmailAccessSupported(context)) {
				Bundle bundle = intent.getExtras();

				/* Get recipient and count */
				String recipient = "You";
				if (bundle.containsKey("account"))
					recipient = bundle.getString("account");
				int count = bundle.getInt("count");

				/* What kind of update is this? */
				String tagLabel = bundle.getString("tagLabel");
				if (tagLabel.equals("^^unseen-^i")) {

					/* This is a new message notification. */
					if (count > 0) {
						NotificationBuilder.createGmailBlank(context,
								recipient, count);
						if (Preferences.logging) Log.d(MetaWatch.TAG,
								"Received Gmail new message notification; "
										+ count + " new message(s).");
					} else {
						if (Preferences.logging) Log.d(MetaWatch.TAG,
								"Ignored Gmail new message notification; no new messages.");
					}

				} else if (tagLabel.equals("^^unseen-^iim")) {

					/* This is a total unread count notification. */
					if (Preferences.logging) Log.d(MetaWatch.TAG,
							"IntentReceiver.onReceive(): Received Gmail notification: total unread count for '"
									+ recipient + "' is " + count + ".");

				} else {
					/* I have no idea what this is. */
					if (Preferences.logging) Log.d(MetaWatch.TAG,
							"Unknown Gmail notification: tagLabel is '"+tagLabel+"'");
				}

				Monitors.updateGmailUnreadCount(recipient, count);
				if (Preferences.logging) Log.d(MetaWatch.TAG,
						"IntentReceiver.onReceive(): Cached Gmail unread count for account '"
								+ recipient + "' is "
								+ Monitors.getGmailUnreadCount(recipient));
				
				Idle.updateIdle(context, true);
				
				return;
			}
		}
		else if (action.equals("android.provider.Telephony.SMS_RECEIVED")) {		
			if (!MetaWatchService.Preferences.notifySMS)
				return;
			
			Bundle bundle = intent.getExtras();
			if (bundle.containsKey("pdus")) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] smsMessage = new SmsMessage[pdus.length];
				String fullBody = "";
				String number = null;
				for (int i = 0; i < smsMessage.length; i++) {
					smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					number = smsMessage[i].getOriginatingAddress();
					String bodyPart = smsMessage[i].getDisplayMessageBody();
					
					if(!Preferences.stickyNotifications)
						NotificationBuilder.createSMS(context, number, bodyPart);
					else
						fullBody += bodyPart;
				}
				
				if(Preferences.stickyNotifications)
					NotificationBuilder.createSMS(context, number, fullBody);
			}
			return;
		}
		else if (action.equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) { // received MMS
			if (!MetaWatchService.Preferences.notifySMS)
				return;

			/*
			 *  The rows below are taken from AndroidNotifier
			 *  (http://code.google.com/p/android-notifier)
			 *  and adapted for MWM.
			 */
			if (!intent.getType().equals("application/vnd.wap.mms-message")) {
				if (Preferences.logging) Log.e(MetaWatch.TAG, "IntentReceiver.onReceive(): Got wrong data type for MMS: " + intent.getType());
				return;
			}

			// Parse the WAP push contents
			PduParser parser = new PduParser();
			PduHeaders headers = parser.parseHeaders(intent.getByteArrayExtra("data"));
			if (headers == null) {
				if (Preferences.logging) Log.e(MetaWatch.TAG, "IntentReceiver.onReceive(): Couldn't parse headers for WAP PUSH.");
				return;
			}

			int messageType = headers.getMessageType();
			if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): WAP PUSH message type: 0x" + Integer.toHexString(messageType));

			// Check if it's a MMS notification
			if (messageType == PduHeaders.MESSAGE_TYPE_NOTIFICATION_IND) {
				String fromStr = null;
				EncodedStringValue encodedFrom = headers.getFrom();
				if (encodedFrom != null) {
					fromStr = encodedFrom.getString();
				}
				/*
				 * End of code from AndroidNotifier.
				 */

				NotificationBuilder.createMMS(context, fromStr);
			}
		}
		else if (action.equals("android.intent.action.NEW_OUTGOING_CALL")) {
			if (Preferences.logging) Log.d(MetaWatch.TAG, "Detected outgoing call");
			Call.inCall = true;
			if(intent.hasExtra("android.intent.extra.PHONE_NUMBER"))
				Call.phoneNumber = intent.getStringExtra("android.intent.extra.PHONE_NUMBER");
			Idle.updateIdle(context, true);
			
		}
		else if (action.equals("com.fsck.k9.intent.action.EMAIL_RECEIVED")) {
			
			if (MetaWatchService.Preferences.notifyK9) {				
				Bundle bundle = intent.getExtras();				
				String subject = bundle.getString("com.fsck.k9.intent.extra.SUBJECT");
				String sender = bundle.getString("com.fsck.k9.intent.extra.FROM");
				String account = bundle.getString("com.fsck.k9.intent.extra.ACCOUNT");
				String folder = bundle.getString("com.fsck.k9.intent.extra.FOLDER");
				NotificationBuilder.createK9(context, sender, subject, account+":"+folder);
			}
			Utils.refreshUnreadK9Count(context);
			Idle.updateIdle(context, true);

			return;
		}
		else if (action.equals("windroid.SMART_DEVICE_UPDATE_EMAILS")) {
			// Nitrodesk TouchDown new email
			if (MetaWatchService.Preferences.notifyTD) {				
				Bundle bundle = intent.getExtras();	
				
				String title = null;
				if (bundle.containsKey("windroid.extra.SMARTWATCH_TITLE")) 
					title = bundle.getString("windroid.extra.SMARTWATCH_TITLE");
				
				String ticker = null;
				if (bundle.containsKey("windroid.extra.SMARTWATCH_TICKER")) 
					ticker = bundle.getString("windroid.extra.SMARTWATCH_TICKER");
				
				if (bundle.containsKey("windroid.extra.SMARTWATCH_COUNT")) {
					Monitors.TouchDownData.unreadMailCount = bundle.getInt("windroid.extra.SMARTWATCH_COUNT");
					Idle.updateIdle(context, true);
				}
				
				if(title!=null && ticker!=null)
					NotificationBuilder.createTouchdownMail(context, title, ticker);
			}
			
			return;
		}	
		else if (action.equals("com.android.alarmclock.ALARM_ALERT")
				|| action.equals("com.htc.android.worldclock.ALARM_ALERT")
				|| action.equals("com.android.deskclock.ALARM_ALERT")
				|| action.equals("com.motorola.blur.alarmclock.ALARM_ALERT")
				|| action.equals("com.motorola.blur.alarmclock.COUNT_DOWN")
				|| action.equals("com.sonyericsson.alarm.ALARM_ALERT")) {
			
			if (!MetaWatchService.Preferences.notifyAlarm)
				return;
			
			NotificationBuilder.createAlarm(context);
			return;
		}
		else if (action.equals("android.intent.action.BATTERY_LOW") ) {
			
			if (!MetaWatchService.Preferences.notifyBatterylow)
				return;
			
			NotificationBuilder.createBatterylow(context);
			return;
		}
		else if (action.equals("android.intent.action.TIME_SET") ) {
			
			if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): Received time set intent.");
			
			/* The time has changed, so notify the watch. */
			//Protocol.setNvalTime(context);
			Protocol.sendRtcNow(context);
			return;
		}		
		else if (action.equals("android.intent.action.TIMEZONE_CHANGED") ) {
			
			if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): Received timezone changed intent.");
			
			/*
			 * If we're in a new time zone, then the time has probably changed.
			 * Notify the watch.
			 */
			Protocol.sendRtcNow(context);
			
			/*
			 * Check that the timezone has actually changed, so that we don't
			 * spam the user with notifications.
			 */
			
			TimeZone tz = TimeZone.getDefault();
			if (!tz.getDisplayName().equals(lastTimeZoneName))
			{
				lastTimeZoneName = tz.getDisplayName();
				
				SharedPreferences sharedPreferences = PreferenceManager
						.getDefaultSharedPreferences(context);
				if (sharedPreferences.getBoolean("settingsNotifyTimezoneChange",
						false)) {
					NotificationBuilder.createTimezonechange(context);
				}				
			}
			return;
		}
		
		else if (intent.getAction().equals("org.metawatch.manager.UPDATE_CALENDAR")){
			
			if (MetaWatchService.watchType == MetaWatchService.WatchType.DIGITAL) {
				Idle.updateIdle(context, true);
			}
			
		}
		
		else if (intent.getAction().equals("com.android.music.metachanged")
				|| intent.getAction().equals(
						"mobi.beyondpod.action.PLAYBACK_STATUS")
				|| intent.getAction().equals("com.htc.music.metachanged")
				|| intent.getAction().equals("com.nullsoft.winamp.metachanged")
				|| intent.getAction().equals("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED")
				|| intent.getAction().equals("com.amazon.mp3.metachanged")
				|| intent.getAction().equals("com.adam.aslfms.notify.playstatechanged")) {

			/* If the intent specifies a "playing" extra, use it. */
			if (intent.hasExtra("playing")) {
				boolean playing = intent.getBooleanExtra("playing", false);
				if (playing == false) {
					/* Ignore stop events. */
					return;
				}
			}
			
			String artist = "";
			String track = "";
			String album = "";

			if (intent.hasExtra("artist"))
				artist = intent.getStringExtra("artist");
			else if (intent.hasExtra("ARTIST_NAME"))
				artist = intent.getStringExtra("ARTIST_NAME");
			else if (intent.hasExtra("com.amazon.mp3.artist"))
				artist = intent.getStringExtra("com.amazon.mp3.artist");
			
			if (intent.hasExtra("track"))
				track = intent.getStringExtra("track");
			else if (intent.hasExtra("TRACK_NAME"))
				track = intent.getStringExtra("TRACK_NAME");
			else if (intent.hasExtra("com.amazon.mp3.track"))
				track = intent.getStringExtra("com.amazon.mp3.track");
			
			if (intent.hasExtra("album"))
				album = intent.getStringExtra("album");
			else if (intent.hasExtra("ALBUM_NAME"))
				album = intent.getStringExtra("ALBUM_NAME");
			else if (intent.hasExtra("com.amazon.mp3.album"))
				album = intent.getStringExtra("com.amazon.mp3.album");
			
			if(artist==null)
				artist="";
			if(album==null)
				album="";
			if(track==null)
				track="";
			
			MediaControl.updateNowPlaying(context, artist, album, track, intent.getAction());
					
		}
		else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			
			if (noConnectivity) {
				if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): No data connectivity.");
			}
			else {
				if (Preferences.logging) Log.d(MetaWatch.TAG, "IntentReceiver.onReceive(): Data connectivity available.");
				
				long currentTime = System.currentTimeMillis();
				long diff = currentTime - WeatherData.timeStamp;
				
				if (diff < 30 * 60*1000) {
					if (Preferences.logging) Log.d(MetaWatch.TAG,
							"Skipping weather update - updated less than 30m ago");
				}
				else {			
					Monitors.updateWeatherData(context);
				}
			}

			
		}
		
	}

}

