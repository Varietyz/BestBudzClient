package com.bestbudz.engine.core;

import static com.bestbudz.engine.config.ColorConfig.*;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.Client.LOGIN_BACKGROUND;
import com.bestbudz.graphics.buffer.ImageProducer;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.Iterator;

/**
 * Minimal modern boot screen with animated loader
 * Clean, simple design focused on smooth startup experience
 */
public class LoadingScreen extends Client {

	// Animation state
	private static long bootStartTime = System.currentTimeMillis();
	private static float rotation = 0f;
	private static BufferedImage bootLogo = null;
	private static boolean isGifAnimation = false;
	private static ImageReader gifReader = null;
	private static int currentGifFrame = 0;
	private static int totalGifFrames = 0;
	private static long lastFrameTime = 0;
	private static int frameDelay = 100; // Default 100ms between frames

	// Animation constants
	private static final float ROTATION_SPEED = 2.0f; // degrees per frame
	private static final int LOADER_SIZE = 60;
	private static final int LOGO_MAX_SIZE = 200;

	/**
	 * Display the boot screen with animated loader
	 */
	public static void showBootScreen(Graphics2D g) {
		showBootScreen(g, null);
	}

	/**
	 * Display the boot screen with canvas context
	 */
	public static void showBootScreen(Graphics2D g, GameCanvas canvas) {
		// Get screen dimensions
		int screenW = getScreenWidth(g, canvas);
		int screenH = getScreenHeight(g, canvas);

		// Initialize buffer if available
		initializeBootBuffer(g, screenW, screenH);

		if (Client.aRSImageProducer_1109 == null) {
			// Fallback: render directly
			renderBootScreenDirect(g, screenW, screenH);
			return;
		}

		Graphics2D bufferGraphics = null;
		try {
			bufferGraphics = Client.aRSImageProducer_1109.getImageGraphics();
			if (bufferGraphics != null) {
				setupRendering(bufferGraphics);
				renderBootScreen(bufferGraphics, screenW, screenH);
				Client.aRSImageProducer_1109.drawGraphics(0, g, 0);
			} else {
				renderBootScreenDirect(g, screenW, screenH);
			}
		} catch (Exception e) {
			renderFallbackScreen(g, screenW, screenH);
		} finally {
			if (bufferGraphics != null) {
				bufferGraphics.dispose();
			}
		}

		// Update animation
		updateAnimation();
	}

	/**
	 * Initialize boot screen buffer
	 */
	private static void initializeBootBuffer(Graphics2D g, int screenW, int screenH) {
		try {
			if (Client.aRSImageProducer_1109 == null ||
				Client.aRSImageProducer_1109.canvasWidth != screenW ||
				Client.aRSImageProducer_1109.canvasHeight != screenH) {
				Client.aRSImageProducer_1109 = new ImageProducer(screenW, screenH);
				Client.aRSImageProducer_1109.initDrawingArea();
			}
		} catch (Exception e) {
			// Will use direct rendering fallback
		}
	}

	/**
	 * Render boot screen directly to graphics
	 */
	private static void renderBootScreenDirect(Graphics2D g, int screenW, int screenH) {
		try {
			setupRendering(g);
			renderBootScreen(g, screenW, screenH);
		} catch (Exception e) {
			renderFallbackScreen(g, screenW, screenH);
		}
	}

	/**
	 * Main boot screen rendering
	 */
	private static void renderBootScreen(Graphics2D g, int screenW, int screenH) {
		// Draw background
		drawBackground(g, screenW, screenH);

		// Draw boot logo or loader
		drawBootContent(g, screenW, screenH);
	}

	/**
	 * Draw background with subtle styling
	 */
	private static void drawBackground(Graphics2D g, int screenW, int screenH) {
		// Fill with base color
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, screenW, screenH);

		// Add background image if available (dimmed)
		if (LOGIN_BACKGROUND != null) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
			int bgX = (screenW - LOGIN_BACKGROUND.getWidth()) / 2;
			int bgY = (screenH - LOGIN_BACKGROUND.getHeight()) / 2;
			g.drawImage(LOGIN_BACKGROUND, bgX, bgY, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}

		// Subtle gradient overlay
		GradientPaint gradient = new GradientPaint(
			0, 0, new Color(0, 0, 0, 0),
			0, screenH, new Color(0, 0, 0, 50)
		);
		g.setPaint(gradient);
		g.fillRect(0, 0, screenW, screenH);
	}

	/**
	 * Draw boot logo or animated loader
	 */
	private static void drawBootContent(Graphics2D g, int screenW, int screenH) {
		int centerX = screenW / 2;
		int centerY = screenH / 2;

		// Try to load and display custom boot logo/gif
		if (bootLogo == null) {
			loadBootLogo();
		}

		if (bootLogo != null) {
			drawBootLogo(g, centerX, centerY);
		} else {
			// Fallback to animated spinner
			drawSpinnerLoader(g, centerX, centerY);
		}
	}

	/**
	 * Load boot logo from resources (supports static images and GIFs)
	 */
	private static void loadBootLogo() {
		try {
			// Try to load custom boot logo/animation
			InputStream logoStream = LoadingScreen.class.getResourceAsStream("/boot_logo.gif");
			if (logoStream == null) {
				logoStream = LoadingScreen.class.getResourceAsStream("/boot_logo.png");
			}
			if (logoStream == null) {
				logoStream = LoadingScreen.class.getResourceAsStream("/logo.gif");
			}
			if (logoStream == null) {
				logoStream = LoadingScreen.class.getResourceAsStream("/logo.png");
			}

			if (logoStream != null) {
				// Check if it's a GIF
				ImageInputStream imageStream = ImageIO.createImageInputStream(logoStream);
				Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

				if (readers.hasNext()) {
					ImageReader reader = readers.next();
					String format = reader.getFormatName().toLowerCase();

					if (format.equals("gif")) {
						// Setup GIF animation
						isGifAnimation = true;
						gifReader = reader;
						gifReader.setInput(imageStream);
						totalGifFrames = gifReader.getNumImages(true);
						bootLogo = gifReader.read(0); // Load first frame

						// Try to get frame delay from metadata
						try {
							frameDelay = getGifFrameDelay(0);
						} catch (Exception e) {
							frameDelay = 100; // Default fallback
						}
					} else {
						// Static image
						reader.setInput(imageStream);
						bootLogo = reader.read(0);
						isGifAnimation = false;
					}
				}
				logoStream.close();
			}
		} catch (Exception e) {
			// Logo loading failed, will use fallback spinner
			bootLogo = null;
			isGifAnimation = false;
		}
	}

	/**
	 * Get GIF frame delay in milliseconds
	 */
	private static int getGifFrameDelay(int frameIndex) {
		try {
			javax.imageio.metadata.IIOMetadata metadata = gifReader.getImageMetadata(frameIndex);
			String metaFormatName = metadata.getNativeMetadataFormatName();
			org.w3c.dom.Node root = metadata.getAsTree(metaFormatName);
			org.w3c.dom.Node graphicsControlExtensionNode = findNode(root, "GraphicControlExtension");

			if (graphicsControlExtensionNode != null) {
				String delayTime = ((org.w3c.dom.Element) graphicsControlExtensionNode).getAttribute("delayTime");
				return Integer.parseInt(delayTime) * 10; // Convert to milliseconds
			}
		} catch (Exception e) {
			// Ignore and use default
		}
		return 100; // Default 100ms
	}

	/**
	 * Find specific node in DOM tree
	 */
	private static org.w3c.dom.Node findNode(org.w3c.dom.Node node, String nodeName) {
		if (node.getNodeName().equals(nodeName)) {
			return node;
		}
		org.w3c.dom.NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			org.w3c.dom.Node found = findNode(children.item(i), nodeName);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * Draw custom boot logo (static or animated)
	 */
	private static void drawBootLogo(Graphics2D g, int centerX, int centerY) {
		if (bootLogo == null) return;

		// Update GIF animation if needed
		if (isGifAnimation && gifReader != null) {
			updateGifFrame();
		}

		// Calculate scaled size
		int logoW = bootLogo.getWidth();
		int logoH = bootLogo.getHeight();

		float scale = Math.min((float)LOGO_MAX_SIZE / logoW, (float)LOGO_MAX_SIZE / logoH);
		int scaledW = (int)(logoW * scale);
		int scaledH = (int)(logoH * scale);

		// Draw logo centered
		int logoX = centerX - scaledW / 2;
		int logoY = centerY - scaledH / 2;

		g.drawImage(bootLogo, logoX, logoY, scaledW, scaledH, null);
	}

	/**
	 * Update GIF animation frame
	 */
	private static void updateGifFrame() {
		if (!isGifAnimation || gifReader == null || totalGifFrames <= 1) return;

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastFrameTime >= frameDelay) {
			try {
				currentGifFrame = (currentGifFrame + 1) % totalGifFrames;
				bootLogo = gifReader.read(currentGifFrame);
				lastFrameTime = currentTime;

				// Update frame delay for this frame
				frameDelay = getGifFrameDelay(currentGifFrame);
			} catch (Exception e) {
				// If frame reading fails, disable animation
				isGifAnimation = false;
			}
		}
	}

	/**
	 * Draw fallback animated spinner
	 */
	private static void drawSpinnerLoader(Graphics2D g, int centerX, int centerY) {
		// Outer ring
		g.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 80));
		g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawOval(centerX - LOADER_SIZE/2, centerY - LOADER_SIZE/2, LOADER_SIZE, LOADER_SIZE);

		// Animated arc
		g.setColor(ACCENT_COLOR);
		g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		Arc2D.Float arc = new Arc2D.Float(
			centerX - LOADER_SIZE/2, centerY - LOADER_SIZE/2,
			LOADER_SIZE, LOADER_SIZE,
			rotation, 90, Arc2D.OPEN
		);
		g.draw(arc);

		// Center dot
		g.setColor(ACCENT_COLOR);
		g.fillOval(centerX - 3, centerY - 3, 6, 6);
	}

	/**
	 * Update animation state
	 */
	private static void updateAnimation() {
		rotation += ROTATION_SPEED;
		if (rotation >= 360f) {
			rotation -= 360f;
		}
	}

	/**
	 * Ultimate fallback screen
	 */
	private static void renderFallbackScreen(Graphics2D g, int screenW, int screenH) {
		// Simple solid background
		g.setColor(new Color(25, 25, 35));
		g.fillRect(0, 0, screenW, screenH);

		// Simple loading text
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 18));
		String text = "Loading...";
		FontMetrics fm = g.getFontMetrics();
		g.drawString(text, (screenW - fm.stringWidth(text))/2, screenH/2);
	}

	/**
	 * Setup rendering quality
	 */
	private static void setupRendering(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	/**
	 * Get screen width safely
	 */
	private static int getScreenWidth(Graphics2D g, GameCanvas canvas) {
		try {
			if (canvas != null && canvas.getWidth() > 0) {
				return canvas.getWidth();
			}
			if (g != null && g.getClipBounds() != null) {
				return g.getClipBounds().width;
			}
			if (Client.frameWidth > 0) {
				return Client.frameWidth;
			}
		} catch (Exception e) {
			// Use fallback
		}
		return 765; // Default fallback
	}

	/**
	 * Get screen height safely
	 */
	private static int getScreenHeight(Graphics2D g, GameCanvas canvas) {
		try {
			if (canvas != null && canvas.getHeight() > 0) {
				return canvas.getHeight();
			}
			if (g != null && g.getClipBounds() != null) {
				return g.getClipBounds().height;
			}
			if (Client.frameHeight > 0) {
				return Client.frameHeight;
			}
		} catch (Exception e) {
			// Use fallback
		}
		return 503; // Default fallback
	}

	/**
	 * Cleanup resources when boot screen is no longer needed
	 */
	public static void cleanup() {
		try {
			if (gifReader != null) {
				gifReader.dispose();
				gifReader = null;
			}
			bootLogo = null;
			isGifAnimation = false;
		} catch (Exception e) {
			// Ignore cleanup errors
		}
	}
}