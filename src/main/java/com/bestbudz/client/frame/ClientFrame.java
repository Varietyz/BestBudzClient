package com.bestbudz.client.frame;

import com.bestbudz.client.ClientEngine;
import com.bestbudz.config.FrameConfig;

import com.bestbudz.util.ResourceLoader;
import javax.swing.*;
import java.awt.*;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * Modular client frame supporting dynamic plugin-like UI features.
 */
public final class ClientFrame extends JFrame {

	private final ClientEngine applet;
	private static final long serialVersionUID = 1L;
	private final Insets insets;
	private final Toolkit toolkit = Toolkit.getDefaultToolkit();
	private final Dimension screenSize = toolkit.getScreenSize();

	public ClientFrame(ClientEngine applet, int width, int height, boolean resizable, boolean fullscreen) {
		System.out.println("[LOG] ClientFrame.<init> called: "
			+ "width=" + width
			+ ", height=" + height
			+ ", resizable=" + resizable
			+ ", fullscreen=" + fullscreen
		);
		this.applet = applet;
		setWindowIcon();

		setTitle(FrameConfig.TITLE);
		setResizable(resizable);
		setUndecorated(fullscreen);
		setBackground(FrameConfig.BACKGROUND_COLOR);
		setFocusTraversalKeysEnabled(false);
		setLayout(null); // Optional layout override
		setVisible(true);

		Insets tempInsets = getInsets();
		this.insets = (tempInsets != null) ? tempInsets : new Insets(0, 0, 0, 0); // ✅ Assign before use

		configureFrame(width, height, resizable, fullscreen);
		loadPlugins();
	}


	private void configureFrame(int width, int height, boolean resizable, boolean fullscreen) {
		if (resizable) {
			setMinimumSize(new Dimension(
				FrameConfig.MIN_WIDTH + insets.left + insets.right,
				FrameConfig.MIN_HEIGHT + insets.top + insets.bottom
			));
		}
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
		setLocationRelativeTo(null);
		requestFocus();
		toFront();
	}


	private void setWindowIcon() {
		String fullPath = FrameConfig.RESOURCE_ROOT + FrameConfig.ICON_PATH;
		Image icon = ResourceLoader.loadIcon(fullPath);
		if (icon != null) {
			setIconImage(icon);
		}
	}

	private void loadPlugins() {

	}

	public Graphics getGraphics() {
		final Graphics graphics = super.getGraphics();
		Insets insets = this.getInsets();
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(insets != null ? insets.left : 0, insets != null ? insets.top : 0);
		return graphics;
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}

	public void update(Graphics graphics) {
		applet.update(graphics);
	}

	public void paint(Graphics graphics) {
		applet.paint(graphics);
	}
}
