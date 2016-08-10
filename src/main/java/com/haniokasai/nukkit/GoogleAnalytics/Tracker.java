/*
 *
 *  Copyright 2012-2014 Oliver Sand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

/*
 *
 * Some parts are based on the Google Analystics Android SDK. See https://developers.google.com/analytics/devguides/collection/android/).
 *
 *  Copyright 2011-2014 Google Analytics Plugin Authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.haniokasai.nukkit.GoogleAnalytics;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import cn.nukkit.Server;

public class Tracker {


	public static void Track(String visitorId, String visitorIp,String category, String action, String label){
		Tracker.trackPageAction(visitorId,visitorIp,category, action, label);
		Tracker.trackPageView(visitorId,visitorIp);
	}



	private static void trackPageAction(String visitorId,String visitorIp,String category, String action, String label){

		// Construct the gif hit url.
		String utmUrl;
		try {
			utmUrl = "https://www.google-analytics.com/collect?v="+1+
					"&tid="+GoogleAnalyticsPlugin.analyticsServerAccount+
					"&cid="+visitorId+
					"&uip="+visitorIp+
			        "&t="+"event"+
			        "&ec="+category+
			        "&ea="+action+
			        "&el="+URLEncoder.encode(label, "UTF-8")+
			        "&ev="+0;
			if(GoogleAnalyticsPlugin.enableDebug) {
				Server.getInstance().getLogger().info("Tracker Url: " + utmUrl);
			}

			sendRequestToGoogleAnalytics(utmUrl);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}


	}

	private static void trackPageView(String visitorId,String visitorIp){
		String page ="/";
		String title ="MCPE";
		// Construct the gif hit url.
				String utmUrl =  "https://www.google-analytics.com/collect?v="+1+
						"&tid="+GoogleAnalyticsPlugin.analyticsServerAccount+
						"&cid="+visitorId+
						"&uip="+visitorIp+
				        "&t="+"pageview"+
				        "&dh="+GoogleAnalyticsPlugin.analyticsServerDomain+
				        "&dp="+page+
				        "&dt="+title;

				if(GoogleAnalyticsPlugin.enableDebug) {
					Server.getInstance().getLogger().info("Tracker Url: " + utmUrl);
				}

				sendRequestToGoogleAnalytics(utmUrl);
	}

	private static void sendRequestToGoogleAnalytics(String utmUrl){
		try {
			URL url = new URL(utmUrl);
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			connection.connect();

			InputStream in = connection.getInputStream();
			int available;

			while((available = in.available()) != 0)
			{
				in.skip(available);
			}

			in.close();
	  	} catch (Exception e) {
	  		Server.getInstance().getLogger().warning("Tracker Connection Error: " + e.getMessage());
	  	}
	}



}