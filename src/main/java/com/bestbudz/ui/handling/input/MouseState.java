package com.bestbudz.ui.handling.input;

public final class MouseState {
	public static volatile int x, y;
	public static boolean leftDown, rightDown;
	public static boolean leftClicked, rightClicked;
	public static volatile boolean pressed = false;
	public static volatile boolean released = false;
	public static volatile boolean clicked = false;
	public static volatile int button = 0;
	public static volatile boolean clickEvent = false;
	public static long lastMoveTime;
	public static int idleTime;
}
