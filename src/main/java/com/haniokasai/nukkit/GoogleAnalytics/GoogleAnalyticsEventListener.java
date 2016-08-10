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

import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.PlayerGameModeChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;

/**
 * The entity listener for tracking events
 *
 * @author Oliver
 *
 */
public class GoogleAnalyticsEventListener implements Listener {
	private final GoogleAnalyticsPlugin plugin;

	private final boolean enableEventDeath;
	private final boolean enableEventKill;
	private final boolean enableEventTryLogin;
	private final boolean enableEventLogin;
	private final boolean enableEventQuit;
	private final boolean enableEventRespawn;
	private final boolean enableEventKicked;
	private final boolean enableEventGameModeChange;

	private final HashMap<String, Long> playerJoinedTime = new HashMap<String, Long>();


	public GoogleAnalyticsEventListener(GoogleAnalyticsPlugin instance) {
	    this.plugin = instance;

	    // Load config
	    enableEventDeath = plugin.getConfig().getBoolean("track_events.death", true);
	    enableEventKill = plugin.getConfig().getBoolean("track_events.kill", true);
	    enableEventTryLogin = plugin.getConfig().getBoolean("track_events.trylogin", true);
	    enableEventLogin = plugin.getConfig().getBoolean("track_events.login", true);
	    enableEventQuit = plugin.getConfig().getBoolean("track_events.quit", true);
	    enableEventRespawn = plugin.getConfig().getBoolean("track_events.respawn", true);
	    enableEventKicked = plugin.getConfig().getBoolean("track_events.kicked", true);
	    enableEventGameModeChange = plugin.getConfig().getBoolean("track_events.gamemodechange", true);
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLogin(PlayerLoginEvent event){
		if(enableEventTryLogin) {
			try {
				Player player = event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					plugin.getTracker().Track(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "TryLogin", player.isWhitelisted() ? "Whitelisted" : "Not whitelisted");
				}
			}
			catch(Exception e) {
		  		plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event){
		if(enableEventLogin) {
			try {
				Player player = event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					playerJoinedTime.put(player.getName(), System.currentTimeMillis());

					plugin.getTracker().Track(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Login", player.isOp() ? "Operator" : "Player");
				}
		}catch(Exception e) {
				plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event){
		if(enableEventQuit) {
			try {
				Player player = event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					Long joinTime = playerJoinedTime.get(player.getName());
					String playTime = "Playtime Unkown";

					if(joinTime != null) {
						long time = (System.currentTimeMillis() - joinTime) / 1000;

						if(time / 60 / 60 >= 5) {
							playTime = "Played more than 5 hours";
						}
						else if(time / 60 / 60 >= 4) {
							playTime = "Played 4 hours";
						}
						else if(time / 60 / 60 >= 3) {
							playTime = "Played 3 hours";
						}
						else if(time / 60 / 60 >= 2) {
							playTime = "Played 2 hours";
						}
						else if(time / 60 / 60 >= 1) {
							playTime = "Played 1 hour";
						}
						else if(time / 60 <= 30 && time / 60 > 5) {
							playTime = "Played less than 30 minutes";
						}
						else {
							playTime = "Played less than 5 minutes";
						}
					}

					plugin.getTracker().Track(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Quit", playTime);
				}
			}
			catch(Exception e) {
				plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event){
		if(enableEventRespawn) {
			try {
				Player player = event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					plugin.getTracker().TrackAction(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Respawn", "At spawn");
				}
			}
			catch(Exception e) {
				plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent event){
		if(enableEventKicked) {
			try {
				Player player = event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					plugin.getTracker().TrackAction(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Kicked", event.getReason());
				}
			}
			catch(Exception e) {
				plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event){
		Entity entity = event.getEntity();
		Player player = null;
		// Died Event
		if(entity instanceof Player && enableEventDeath) {
			try {
				player = (Player)entity;

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					plugin.getTracker().TrackAction(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Died", entity.getLastDamageCause() != null ? getDamageName(entity.getLastDamageCause().getCause()) : "Unkown");
				}
			}
			catch(Exception e) {
		  		plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}

		// Kill Event
		try{
			Player killer= (Player)(((EntityDamageByEntityEvent) player.getLastDamageCause())).getDamager();
			if(killer instanceof Player){

				if(player != null && !player.hasPermission("googleanalyticsplugin.ignore") && enableEventKill) {
					plugin.getTracker().TrackAction(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Kill", getEntityName(entity));
				}
			}

		}catch(Exception e) {
		  	plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event){
		if(enableEventGameModeChange) {
			try {
				Player player = (Player) event.getPlayer();

				if(!player.hasPermission("googleanalyticsplugin.ignore")) {
					plugin.getTracker().TrackAction(getClientName(plugin, player), getClientId(player), getClientIP(player.getAddress()), player.getName(), "Game Mode Change", "Game Mode " + getGamemode(event.getNewGamemode()));
				}
			}
			catch(Exception e) {
				plugin.getLogger().warning("Event Listener Error: " + e.getMessage());
			}
		}
	}


	private static String getEntityName(Entity entity) {
		String victimName = "";

		if(entity instanceof Player) {
			Player victim = (Player)entity;

			victimName = victim.getName();
		}
		else {
			String className = entity.getClass().getName();

			victimName = className.substring(className.indexOf(".Craft") + ".Craft".length());
		}

		return victimName;
	}

	private static String getClientName(GoogleAnalyticsPlugin plugin, Player player) {

	    String serverVersion = plugin.getServer().getVersion();
	    String clientVersion = serverVersion;
	    String clientName = "Minecraft";

	    // Check for other clients here...

		return clientName + " " + clientVersion;
	}

	private static String getDamageName(int cause) {
		switch (cause) {
		case EntityDamageEvent.CAUSE_BLOCK_EXPLOSION:
			return "Died from an explosion";
		case EntityDamageEvent.CAUSE_CUSTOM:
            return "Strangely died";
		case EntityDamageEvent.CAUSE_DROWNING:
            return "Drowned to death";
		case EntityDamageEvent.CAUSE_ENTITY_ATTACK:
            return "Was brutally attacked";
		case EntityDamageEvent.CAUSE_ENTITY_EXPLOSION :
            return "Got nerfed by a creeper";
		case EntityDamageEvent.CAUSE_FALL:
            return "Fell to there death";
		case EntityDamageEvent.CAUSE_FIRE:
            return "Was burnt to death";
		case EntityDamageEvent.CAUSE_FIRE_TICK:
			return "Got smoked";
		case EntityDamageEvent.CAUSE_LAVA:
			return "Went swimming in magma";
		case EntityDamageEvent.CAUSE_LIGHTNING:
			return "Was hit by a lightning";
		case EntityDamageEvent.CAUSE_MAGIC:
			return "Was killed by magic";
		case EntityDamageEvent.CAUSE_CONTACT:
			return "Melted away";
		case EntityDamageEvent.CAUSE_PROJECTILE:
			return "Was shoot";
		case EntityDamageEvent.CAUSE_SUFFOCATION:
			return "Suffocated";
		case EntityDamageEvent.CAUSE_SUICIDE:
			return "Committed suicide";
		case EntityDamageEvent.CAUSE_VOID:
			return "Fell into the void";
		default:
			return "Strangely died";
		}
	}


	private static String getGamemode(int newGameMode) {
		switch (newGameMode) {
		case 1:
			return "Creative";
		case 0:
			return "Survival";
		case 2:
			return "Adventure";
		case 3:
			return "Spectator";
		}
		return "Survival";
	}

	public static String getClientIP(String string) {
		return string != null ? string.toString() : "0.0.0.0";
	}

	public static String getClientId(Player player) {
		return player.getName();
	}
}
