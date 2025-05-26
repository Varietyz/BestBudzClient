package com.bestbudz.client.frame;

import com.bestbudz.client.ClientEngine;
import com.bestbudz.config.FrameConfig;
import com.bestbudz.util.ResourceLoader;
import java.awt.*;
import javax.swing.*;

public final class ClientFrame extends JFrame
{

	private static final long serialVersionUID = 1L;
	private final ClientEngine applet;
	private final Insets insets;

	public ClientFrame(ClientEngine applet, int width, int height, boolean resizable) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		super(FrameConfig.TITLE);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		System.out.println("[LOG] ClientFrame.<init> called: "
			+ "width=" + width
			+ ", height=" + height
			+ ", resizable=" + resizable
		);
		this.applet = applet;
		setWindowIcon();

		setResizable(resizable);
		setBackground(FrameConfig.BACKGROUND_COLOR);
		setFocusTraversalKeysEnabled(false);
		setLayout(null);
		SwingUtilities.updateComponentTreeUI(this);
		setVisible(true);

		Insets tempInsets = getInsets();
		this.insets = (tempInsets != null) ? tempInsets : new Insets(0, 0, 0, 0);

		configureFrame(width, height, resizable);
		loadPlugins();
	}


	private void configureFrame(int width, int height, boolean resizable)
	{
		if (resizable)
		{
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


	private void setWindowIcon()
	{
		String fullPath = FrameConfig.RESOURCE_ROOT + FrameConfig.ICON_PATH;
		Image icon = ResourceLoader.loadIcon(fullPath);
		if (icon != null)
		{
			setIconImage(icon);
		}
	}

	private void loadPlugins()
	{

	}

	public int getFrameWidth()
	{
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}

	public int getFrameHeight()
	{
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}

	public void update(Graphics graphics)
	{
		applet.update(graphics);
	}

	public Graphics getGraphics()
	{
		final Graphics graphics = super.getGraphics();
		Insets insets = this.getInsets();
		graphics.fillRect(0, 0, getWidth(), getHeight());
		graphics.translate(insets != null ? insets.left : 0, insets != null ? insets.top : 0);
		return graphics;
	}

	public void paint(Graphics graphics)
	{
		applet.paint(graphics);
	}
}

