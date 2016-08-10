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
import java.net.URL;
import java.net.URLConnection;

import cn.nukkit.plugin.Plugin;

/**
 * Defines a Google Analytics tracker.
 *
 * @author Oliver
 *
 */
public class Tracker {

	private static Plugin plugin;


	/**
	 * Create a new instance of a tracker.
	 * @param analyticsServerDomain The domain name of the google analytics account.
	 * @param analyticsServerAccount Th account id of the google analytics account.
	 * @param enableDebug Enable outputting tracking urls.
	 */
	public Tracker(Plugin plugin) {
		Tracker.plugin = plugin;
	}


	/**
	 * Track something as action an pageview.
	 * @param clientName
	 * @param visitorId
	 * @param visitorIp
	 * @param category
	 * @param action
	 * @param label
	 * @throws Exception
	 */
	public void Track(String visitorId, String visitorIp,String category, String action, String label) throws Exception {
		Tracker.trackPageAction(visitorId,visitorIp,category, action, label);
		Tracker.trackPageView(visitorId,visitorIp);
	}



	private static void trackPageAction(String visitorId,String visitorIp,String category, String action, String label) throws Exception {

		// Construct the gif hit url.
		String utmUrl =  "https://www.google-analytics.com/collect?v="+1+
				"&tid="+GoogleAnalyticsPlugin.analyticsServerAccount+
				"&cid="+visitorId+
				"&uip="+visitorIp+
		        "&t="+"event"+
		        "&ec="+category+
		        "&ea="+action+
		        "&el="+label+
		        "&ev="+0;

		if(GoogleAnalyticsPlugin.enableDebug) {
			plugin.getLogger().info("Tracker Url: " + utmUrl);
		}

		sendRequestToGoogleAnalytics(utmUrl);
	}

	private static void trackPageView(String visitorId,String visitorIp) throws Exception {
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
					plugin.getLogger().info("Tracker Url: " + utmUrl);
				}

				sendRequestToGoogleAnalytics(utmUrl);
	}

	private static void sendRequestToGoogleAnalytics(String utmUrl) throws Exception {
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
	  		plugin.getLogger().warning("Tracker Connection Error: " + e.getMessage());
	  	}
	}



}