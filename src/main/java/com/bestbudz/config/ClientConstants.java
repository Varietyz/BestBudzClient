package com.bestbudz.config;

import com.bestbudz.cache.Signlink;

/*
 * 
 * Handles all the client constants
 * @author Daniel
 * @author Zion
 *
 */
public class ClientConstants {

	/**
	 * The name of the client
	 */
	public final static String CLIENT_NAME = "Best Budz";

	/**
	 * The client version
	 */
	public final static String CLIENT_VERSION = "1.2";

	/**
	 * The client version
	 */
	public final static int CLIENT_VERSION_INT = 10;

	/**
	 * The world selected
	 */
	public static int worldSelected;

	/**
	 * The IP address which the client will be connecting to
	 */
	public final static String[] SERVER_IPS = { "161.35.87.63", "161.35.87.63", "127.0.0.1",
			"161.35.87.63" };

	/**
	 * The client port
	 */
	public final static int SERVER_PORT = 42000;

	/**
	 * Check if client will be connecting to local host
	 */
	public final static boolean LOCALHOST = false;

	/**
	 * The location of the cache
	 */
	public final static String CACHE_LOCATION = Signlink.findCacheDir();

	/**
	 * The text which will be shown on announcement bar
	 */
	public final static String ANNOUNCEMENT = "Welcome to Best Budz, where being high is a requirement!";

	/**
	 * Amount of icons to display
	 */
	public static final int ICON_AMOUNT = 13;

	/**
	 * Checks if the client will be run in debug mode
	 */
	public static boolean DEBUG_MODE = false;

}
