package com.haniokasai.nukkit.GoogleAnalytics;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;

import javax.xml.bind.DatatypeConverter;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class Main  extends PluginBase implements Listener{

	public Config config;
	private static String AnalyticsServerDomain;
	public static String AnalyticsServerAccount;

	public void onEnable(){
		 this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getLogger().info("GoogleAnalytics Starterd");
		getDataFolder().mkdir();
		@SuppressWarnings("deprecation")
		Config config = new Config(
                new File(this.getDataFolder(), "config.yml"),Config.YAML,
                new LinkedHashMap<String, Object>() {
                    {
                    	put("analyticsServerAccount", "MO-XXXXXXXX-X");
                    	put("analyticsServerDomain", "website.com");
                    }
                });
        config.save();
        AnalyticsServerAccount = config.getString("analyticsServerAccount");
        AnalyticsServerDomain = config.getString("analyticsServerDomain");
	}

	public void onDisable(){
		this.getServer().getLogger().info("GoogleAnalytics Stopped");
	}


	@EventHandler
	   public void onPlayerJoin(PlayerJoinEvent event) throws NoSuchAlgorithmException{
	        track(event.getPlayer().getDisplayName(), event.getPlayer().getAddress(),event.getPlayer().getDisplayName(),"Spawn",event.getPlayer().getDisplayName());
	    }

	@EventHandler
	    public void onPlayerRespawn(PlayerRespawnEvent event) throws NoSuchAlgorithmException{
	        track(event.getPlayer().getDisplayName(), event.getPlayer().getAddress(),event.getPlayer().getDisplayName(),"Respawn", "At Spawn");
	    }

	    /*public void onPlayerPreLogin(PlayerPreLoginEvent event){
            this.pos[event.getPlayer().getDisplayName()][0] = round(microtime(true) * 1000);
            track(event.getPlayer().getDisplayName(), event.getPlayer().getAddress(), event.getPlayer().getDisplayName(),"Connecting",event.getPlayer().getAddress()); //visitorId, visitorIp, category, action, label
    }
	    public function onPlayerQuit(PlayerQuitEvent event){
	        this.pos[event.getPlayer().getDisplayName()][1] = "Playtime Unkown";
	        if (this.pos[event.getPlayer().getDisplayName()][1] != null){
	            this.pos[event.getPlayer().getDisplayName()][2] = (round(microtime(true) * 1000) - this.pos[event.getPlayer().getDisplayName()][0]) / 1000;
	            if (this.pos[event.getPlayer().getDisplayName()][2] / 60 / 60 >= 5) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played more than 5 hours";
	            } else if (this.pos[event.getPlayer().getDisplayName()][2] / 60 / 60 >= 4) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played 4 hours";
	            } else if (this.pos[event.getPlayer().getDisplayName()][2] / 60 / 60 >= 3) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played 3 hours";
	            } else if (this.pos[event.getPlayer().getDisplayName()][2] / 60 / 60 >= 2) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played 2 hours";
	            } else if (this.pos[event.getPlayer().getDisplayName()][2] / 60 / 60 >= 1) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played 1 hour";
	            } else if ((this.pos[event.getPlayer().getDisplayName()][2] / 60 <= 30) && (this.pos[event.getPlayer().getDisplayName()][2] / 60 > 5)) {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played less than 30 minutes";
	            } else {
	                this.pos[event.getPlayer().getDisplayName()][1] = "Played less than 5 minutes";
	            }
	        }
	      this.track(event.getPlayer().getDisplayName(), event.getPlayer().getAddress(),event.getPlayer().getDisplayName(),"Quit", this.pos[event.getPlayer().getDisplayName()][1]);
	   *}*/

	    public void track(String username, String visitorIp, String category, String action, String label) throws NoSuchAlgorithmException{
	        Main.trackPageAction(username, visitorIp, category, action, label);
	        Main.trackPageView(username, visitorIp);
	    }

	    public static void trackPageAction(String visitorId, String visitorIp, String category, String action, String label) throws NoSuchAlgorithmException{
	       String message = AnalyticsServerAccount + " " + visitorId;
	        byte[] bytes = MessageDigest.getInstance("MD5").digest(ByteBuffer.allocate(4).putInt(message.length()).array());
	        String  messageAsNumber = DatatypeConverter.printHexBinary(bytes);
	        String md5String = messageAsNumber;
	                while(md5String.length() < 32)
	                {
	                        md5String = "0" + md5String;
	                }
	        String getVisitorId =  ("0x" + md5String.substring(0, 16));
	        String event = '(' + category + '*' + action + '*' + label + ')';

	        String utmUrl = "http://www.google-analytics.com/__utm.gif?utmwv=4.4sj&utmn=" +
	          Math.random() +
	          "&utmhn=" + AnalyticsServerDomain +
	          "&utmr=" + "-" +
	          "&utmp=" +
	          "&utmt=" + "event" +
	          "&utme=" + "5" + event.replace(" ", "%20") +
	          "&utmac=" + AnalyticsServerAccount +
	          "&utmcc=__utma%3D999.999.999.999.999.1%3B" +
	          "&utmvid=" + getVisitorId +
	          "&utmdt=" + ("MCPE") +
	          "&utmip=" + visitorIp;


	        Main.asyncOperation(utmUrl);
	    }

	    public static void trackPageView(String visitorId, String visitorIp) throws NoSuchAlgorithmException{
	    	String message = AnalyticsServerAccount + " " + visitorId;
	        byte[] bytes = MessageDigest.getInstance("MD5").digest(ByteBuffer.allocate(4).putInt(message.length()).array());
	        String  messageAsNumber = DatatypeConverter.printHexBinary(bytes);
	        String md5String = messageAsNumber;
	                while(md5String.length() < 32)
	                {
	                        md5String = "0" + md5String;
	                }
	        String getVisitorId =  ("0x" + md5String.substring(0, 16));

	        String utmUrl = "http://www.google-analytics.com/__utm.gif?utmwv=4.4sj&utmn=" +
	  	          Math.random() +
	  	          "&utmhn=" + AnalyticsServerDomain +
	  	          "&utmr=" + "-" +
		          "&utmp="  +AnalyticsServerDomain +
		          "&utmac=" + AnalyticsServerAccount +
		          "&utmcc=__utma%3D999.999.999.999.999.1%3B" +
		          "&utmvid=" + getVisitorId  +
		          "&utmdt=" + ("MCPE") +
		          "&utmip=" + visitorIp;


	        Main.asyncOperation(utmUrl);
		    }

	    public static void asyncOperation(String url){
			/*$ch = curl_init();

			curl_setopt($ch, CURLOPT_URL, $url);

			curl_setopt($ch, CURLOPT_FRESH_CONNECT, true);/nocache
			curl_setopt($ch, CURLOPT_HEADER, false);
			curl_setopt($ch, CURLOPT_TIMEOUT, 1);

			curl_exec($ch);
			curl_close($ch);*/
	        org.toilelibre.libe.curl.Curl.curl(url); //Returns responseBody
	    }
}

