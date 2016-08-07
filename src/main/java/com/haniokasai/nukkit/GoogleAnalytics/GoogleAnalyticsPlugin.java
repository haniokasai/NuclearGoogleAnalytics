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
package com.haniokasai.nukkit.GoogleAnalytics;

import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginManager;


/**
 * The google analytics plugin main class
 *
 * @author Oliver
 *
 */
public class GoogleAnalyticsPlugin extends PluginBase implements Listener{

	private String analyticsServerDomain; // The domain of the google analytics account
	private String analyticsServerAccount; // The tracking account id

	private boolean enableDebug = false;

	private Tracker tracker = null;


	@Override
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();

		setDefaultConfiguration();

		// Config
		analyticsServerDomain = getConfig().getString("analytics.server_domain", "example.com");
		analyticsServerAccount = getConfig().getString("analytics.server_account", "MO-XXXXXXXX-X");
		enableDebug = getConfig().getBoolean("debug", false);

		// Warning message if not set up correctly!
		if(analyticsServerAccount.equals("UA-XXXXXXXX-X") || analyticsServerAccount.equals("MO-XXXXXXXX-X")) {
			getLogger().warning("The configuration for the Google Analytics Plugin is not correct!");
			getLogger().warning("Edit the plugins/GoogleAnalyticsPlugin/config.yml file and set the analytics.server_account settings.");

			pm.disablePlugin(this);

			return;
		}

		// Tracker
		tracker = new Tracker(this, analyticsServerDomain, analyticsServerAccount, enableDebug);

		// Events
		pm.registerEvents(new GoogleAnalyticsEventListener(this), this);

		getLogger().info("Enabled [" + analyticsServerAccount + "]");
	}

	@Override
	public void onDisable() {
	}


	/**
	 * Get the tracker object for this server.
	 * @return A tracker object.
	 */
	public Tracker getTracker() {
		return tracker;
	}

	private void setDefaultConfiguration() {
		this.getConfig().set("track_events.login", true);
		this.getConfig().set("track_events.trylogin", true);
		this.getConfig().set("track_events.quit", true);
		this.getConfig().set("track_events.enter", true);
		this.getConfig().set("track_events.respawn", true);
		this.getConfig().set("track_events.kicked", true);
		this.getConfig().set("track_events.death", true);
		this.getConfig().set("track_events.kill", true);
		this.getConfig().set("track_events.enchantitem", true);
		this.getConfig().set("track_events.tame", true);
		this.getConfig().set("track_events.gamemodechange", true);
		this.getConfig().set("track_events.levelup", true);

		this.getConfig().set("analytics.server_domain", "");
		this.getConfig().set("analytics.server_account", "MO-XXXXXXXX-X");

		this.getConfig().set("debug", false);
		this.saveConfig();
	}
}
