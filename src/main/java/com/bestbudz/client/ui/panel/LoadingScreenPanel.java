package com.bestbudz.client.ui.panel;

import com.bestbudz.engine.Client;
import com.bestbudz.engine.GameCanvas;
import com.bestbudz.client.util.SpriteUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.Timer;

/**
 * Bubble Budz - Interactive Loading Panel Mini-Game
 */
public class LoadingScreenPanel extends JPanel {

	private final Client client;
	private Graphics2D activeGraphics;
	private GameCanvas activeCanvas;

	// Game State
	private boolean gameActive = true;
	private long roundStartTime = 0;
	private int currentScore = 0;
	private int bestScore = 0;
	private boolean wasVisible = false; // Track visibility state to detect activation
	private List<Bubble> bubbles = new CopyOnWriteArrayList<>();
	private List<PopEffect> popEffects = new CopyOnWriteArrayList<>();
	private Random random = new Random();
	private Timer gameTimer;
	private long lastBubbleSpawn = 0;
	private int nextSpawnDelay = 0;

	// Loading State
	private boolean isLoading = false;
	private String loadingMessage = "";
	private long loadingStartTime = 0;
	private BufferedImage logoImage;

	// Layout areas
	private Rectangle gameArea;

	// Constants
	private static final int PADDING = 0;
	private static final int LOGO_SIZE = 80;
	private static final int MIN_BUBBLES = 4;
	private static final int MAX_BUBBLES = 18;
	private static final int MIN_BUBBLE_RADIUS = 5;
	private static final int MAX_BUBBLE_RADIUS = 20;
	private static final int BUBBLE_LIFESPAN = 2800; // Back to fast: 2.8 seconds
	private static final int GROWTH_DURATION = 500; // Faster growth: 0.5 seconds
	private static final int SHRINK_DURATION = 500; // Faster shrink: 0.5 seconds
	private static final int BASE_VIEWPORT_HEIGHT = 600; // Reference height for scoring
	private static final int ROUND_DURATION = 30000; // 30 seconds per round

	// Enhanced Neon Colors for better readability
	private static final Color BG_OVERLAY = new Color(5, 5, 15, 220);
	private static final Color PANEL_BG = new Color(8, 8, 20, 200);
	private static final Color PANEL_BORDER = new Color(100, 200, 255, 80);
	private static final Color TEXT_NEON_CYAN = new Color(0, 255, 255);
	private static final Color TEXT_NEON_PINK = new Color(255, 100, 200);
	private static final Color TEXT_NEON_VIOLET = new Color(200, 100, 255);
	private static final Color TEXT_NEON_GREEN = new Color(100, 255, 100);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	private static final Color TEXT_SECONDARY = new Color(200, 200, 220);
	private static final Color SCORE_GLOW = new Color(255, 255, 0);

	// Enhanced Fonts
	private static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 14);
	private static final Font FONT_SCORE = new Font("SansSerif", Font.BOLD, 20);
	private static final Font FONT_TIMER = new Font("SansSerif", Font.BOLD, 16);
	private static final Font FONT_BEST = new Font("SansSerif", Font.BOLD, 12);
	private static final Font FONT_INSTRUCTION = new Font("SansSerif", Font.PLAIN, 11);
	private static final Font FONT_LOADING = new Font("SansSerif", Font.BOLD, 14);

	// Enhanced Bubble class with graceful timing and better clickboxes
	private static class Bubble {
		float x, y;
		float targetRadius;
		float currentRadius;
		long spawnTime;
		Color color;
		float alpha = 1.0f;
		boolean isClicked = false; // Track if bubble was clicked for graceful removal

		Bubble(float x, float y, float targetRadius, Color color) {
			this.x = x;
			this.y = y;
			this.targetRadius = targetRadius;
			this.currentRadius = 0;
			this.spawnTime = System.currentTimeMillis();
			this.color = color;
		}

		void update() {
			long elapsed = System.currentTimeMillis() - spawnTime;

			if (elapsed < GROWTH_DURATION) {
				// Growing phase - smooth easing
				float progress = (float) elapsed / GROWTH_DURATION;
				currentRadius = targetRadius * easeOut(progress);

			} else if (elapsed < GROWTH_DURATION + SHRINK_DURATION) {
				// Shrinking phase
				float shrinkElapsed = elapsed - GROWTH_DURATION;
				float progress = (float) shrinkElapsed / SHRINK_DURATION;
				currentRadius = targetRadius * (1.0f - easeIn(progress));
			} else {
				// Fading out
				currentRadius = 0;
			}

			// Gradual fade out near end of life
			if (elapsed > BUBBLE_LIFESPAN - 800) {
				float fadeProgress = (elapsed - (BUBBLE_LIFESPAN - 800)) / 800f;
				alpha = Math.max(0, 1.0f - fadeProgress);
			}
		}

		boolean isExpired() {
			return System.currentTimeMillis() - spawnTime > BUBBLE_LIFESPAN;
		}

		// Enhanced click detection with generous clickbox
		boolean contains(int px, int py) {
			if (currentRadius <= 1) return false; // Must be visible

			float dx = px - x;
			float dy = py - y;
			float distance = (float) Math.sqrt(dx * dx + dy * dy);

			// Generous clickbox - much larger than visual for better UX
			float clickRadius = currentRadius + 10; // +10px buffer (was +6)
			return distance <= clickRadius;
		}

		// Smooth easing functions
		private float easeOut(float t) {
			return 1 - (1 - t) * (1 - t);
		}

		private float easeIn(float t) {
			return t * t;
		}
	}

	// Enhanced Pop effect with more satisfying feedback
	private static class PopEffect {
		float x, y;
		float radius;
		float maxRadius;
		Color color;
		long startTime;
		float alpha;

		PopEffect(float x, float y, float radius, Color color) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.maxRadius = radius + 15; // Larger expansion
			this.color = color;
			this.startTime = System.currentTimeMillis();
			this.alpha = 1.0f;
		}

		void update() {
			long elapsed = System.currentTimeMillis() - startTime;
			float progress = elapsed / 600f; // Longer effect (0.6 seconds)

			if (progress <= 1.0f) {
				// Smooth expansion
				radius = radius + (maxRadius - radius) * easeOut(progress);
				alpha = 1.0f - (progress * progress); // Quadratic fade
			}
		}

		boolean isExpired() {
			return System.currentTimeMillis() - startTime > 600;
		}

		private float easeOut(float t) {
			return 1 - (1 - t) * (1 - t);
		}
	}

	public LoadingScreenPanel(Client client) {
		this.client = client;
		initialize();
	}

	private void initialize() {
		setOpaque(false);
		setLayout(null);
		loadLogo();
		setupMouse();
		setupGameTimer();

		boolean initialVisible = !Client.loggedIn;
		setVisible(initialVisible);
		wasVisible = initialVisible;

		// Load best score only once on startup
		loadBestScore();
		System.out.println("Bubble Budz initialized with best score: " + bestScore);

		startGame();
	}

	private void loadLogo() {
		try {
			ImageIcon icon = SpriteUtil.loadIconScaled("loading/bubblebudz.png", LOGO_SIZE);
			Image img = icon.getImage();
			logoImage = new BufferedImage(LOGO_SIZE, LOGO_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = logoImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
		} catch (Exception e) {
			logoImage = null;
		}
	}

	private void setupMouse() {
		MouseAdapter handler = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (!isLoading && gameActive) {
					handleBubbleClick(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Also handle on release for better responsiveness
				if (!isLoading && gameActive) {
					handleBubbleClick(e.getX(), e.getY());
				}
			}
		};
		addMouseListener(handler);
	}

	private void setupGameTimer() {
		gameTimer = new Timer(16, e -> {
			if (gameActive) {
				updateGame();
			}
			repaint();
		});
		gameTimer.start();
	}

	private void calculateLayout() {
		if (getWidth() <= 0 || getHeight() <= 0) return;

		// Simple full-screen game area with proper margins
		gameArea = new Rectangle(
			PADDING,
			80, // Leave space for header
			getWidth() - PADDING * 2,
			getHeight() - 160 // Leave space for header and footer
		);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (!Client.loggedIn && isVisible()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Background overlay
			g2d.setColor(BG_OVERLAY);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			calculateLayout();

			// Subtle panel background
			g2d.setColor(PANEL_BG);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			// Neon border
			//g2d.setStroke(new BasicStroke(3f));
			g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

			if (isLoading) {
				drawLoading(g2d);
			} else {
				drawContent(g2d);
			}

			g2d.dispose();
		}
	}

	private void drawContent(Graphics2D g2d) {
		drawHeader(g2d);
		drawGame(g2d);
		drawFooter(g2d);
	}

	private void drawHeader(Graphics2D g2d) {
		int centerX = getWidth() / 2;
		int y = 25;

		// Logo and title
		if (logoImage != null) {
			g2d.drawImage(logoImage, PADDING + 15, y + 5, null);
		}

		g2d.setFont(FONT_BRAND);
		g2d.setColor(TEXT_NEON_GREEN);
		g2d.drawString("BUBBLE BUDZ", PADDING+ 10, y);

		// Timer (top right)
		long roundElapsed = System.currentTimeMillis() - roundStartTime;
		int timeLeft = Math.max(0, (int)((ROUND_DURATION - roundElapsed) / 1000));

		g2d.setFont(FONT_TIMER);
		g2d.setColor(timeLeft > 5 ? TEXT_NEON_GREEN : TEXT_NEON_PINK);
		String timerText = "Time: " + timeLeft + "s";
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(timerText, getWidth() - PADDING - 20 - fm.stringWidth(timerText), y + 15);

		// Current Score (center top)
		g2d.setFont(FONT_SCORE);
		String scoreText = "Score: " + currentScore;
		fm = g2d.getFontMetrics();
		int scoreX = centerX - fm.stringWidth(scoreText) / 2;

		// Glow effect
		g2d.setColor(new Color(SCORE_GLOW.getRed(), SCORE_GLOW.getGreen(), SCORE_GLOW.getBlue(), 150));
		for (int i = 1; i <= 2; i++) {
			g2d.drawString(scoreText, scoreX + i, y + 45 + i);
		}
		g2d.setColor(SCORE_GLOW);
		g2d.drawString(scoreText, scoreX, y + 45);

		// Best Score (under current score)
		if (bestScore > 0) {
			g2d.setFont(FONT_BEST);
			g2d.setColor(TEXT_SECONDARY);
			String bestText = "Best: " + bestScore;
			fm = g2d.getFontMetrics();
			int bestX = centerX - fm.stringWidth(bestText) / 2;
			g2d.drawString(bestText, bestX, y + 65);
		}
	}

	private void drawGame(Graphics2D g2d) {
		if (gameArea == null) return;

		// Clip to game area
		Shape oldClip = g2d.getClip();
		g2d.clipRect(gameArea.x, gameArea.y, gameArea.width, gameArea.height);

		// Draw bubbles
		for (Bubble bubble : bubbles) {
			drawBubble(g2d, bubble);
		}

		// Draw pop effects
		for (PopEffect effect : popEffects) {
			drawPopEffect(g2d, effect);
		}

		g2d.setClip(oldClip);
	}

	private void drawBubble(Graphics2D g2d, Bubble bubble) {
		if (bubble.currentRadius <= 0) return;

		float x = bubble.x - bubble.currentRadius;
		float y = bubble.y - bubble.currentRadius;
		float diameter = bubble.currentRadius * 2;

		// Gradient fill
		RadialGradientPaint gradient = new RadialGradientPaint(
			bubble.x, bubble.y, bubble.currentRadius,
			new float[]{0f, 0.6f, 1f},
			new Color[]{
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(200 * bubble.alpha)),
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(140 * bubble.alpha)),
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(80 * bubble.alpha))
			}
		);

		g2d.setPaint(gradient);
		g2d.fill(new Ellipse2D.Float(x, y, diameter, diameter));

		// Neon border
		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(255 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(2.5f));
		g2d.draw(new Ellipse2D.Float(x, y, diameter, diameter));

		// Outer glow
		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(100 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(4f));
		g2d.draw(new Ellipse2D.Float(x - 3, y - 3, diameter + 6, diameter + 6));

		// Inner highlight
		float highlightRadius = bubble.currentRadius * 0.25f;
		g2d.setColor(new Color(255, 255, 255, (int)(120 * bubble.alpha)));
		g2d.fill(new Ellipse2D.Float(bubble.x - highlightRadius, bubble.y - highlightRadius,
			highlightRadius * 2, highlightRadius * 2));
	}

	// Enhanced pop effect rendering with multiple rings
	private void drawPopEffect(Graphics2D g2d, PopEffect effect) {
		if (effect.alpha <= 0) return;

		// Main ring
		g2d.setColor(new Color(effect.color.getRed(), effect.color.getGreen(), effect.color.getBlue(), (int)(255 * effect.alpha)));
		g2d.setStroke(new BasicStroke(3f));

		float x = effect.x - effect.radius;
		float y = effect.y - effect.radius;
		float diameter = effect.radius * 2;

		g2d.draw(new Ellipse2D.Float(x, y, diameter, diameter));

		// Secondary ring (slightly delayed)
		float alpha2 = Math.max(0, effect.alpha - 0.3f);
		if (alpha2 > 0) {
			g2d.setColor(new Color(effect.color.getRed(), effect.color.getGreen(), effect.color.getBlue(), (int)(200 * alpha2)));
			g2d.setStroke(new BasicStroke(2f));
			g2d.draw(new Ellipse2D.Float(x - 5, y - 5, diameter + 10, diameter + 10));
		}

		// Inner glow
		float alpha3 = Math.max(0, effect.alpha - 0.1f);
		if (alpha3 > 0) {
			g2d.setColor(new Color(255, 255, 255, (int)(150 * alpha3)));
			g2d.setStroke(new BasicStroke(1.5f));
			g2d.draw(new Ellipse2D.Float(x + 3, y + 3, diameter - 6, diameter - 6));
		}
	}

	private void drawFooter(Graphics2D g2d) {
		int centerX = getWidth() / 2;
		int footerStartY = getHeight() - 45; // Start higher to fit within bounds
		int y = footerStartY;

		// Calculate viewport bonus for instructions
		float heightMultiplier = Math.max(1.0f, (float)getHeight() / BASE_VIEWPORT_HEIGHT);

		// Instructions - keep it simple and short for narrow viewports
		g2d.setFont(FONT_INSTRUCTION);
		g2d.setColor(TEXT_PRIMARY);

		String instruction1 = "Click small bubbles for max points";
		String instruction2 = heightMultiplier > 1.1f ?
			String.format("Viewport bonus: +%.0f%% • 30s rounds", (heightMultiplier - 1.0f) * 50) :
			"30-second rounds • Persistent scores";

		FontMetrics fm = g2d.getFontMetrics();

		// First instruction line
		int x1 = centerX - fm.stringWidth(instruction1) / 2;
		x1 = Math.max(PADDING, Math.min(x1, getWidth() - PADDING - fm.stringWidth(instruction1)));
		g2d.drawString(instruction1, x1, y);
		y += fm.getHeight() + 2;

		// Second instruction line - check width and truncate if needed
		if (fm.stringWidth(instruction2) > getWidth() - PADDING * 2) {
			instruction2 = heightMultiplier > 1.1f ?
				String.format("+%.0f%% bonus • 30s rounds", (heightMultiplier - 1.0f) * 50) :
				"30s rounds";
		}

		int x2 = centerX - fm.stringWidth(instruction2) / 2;
		x2 = Math.max(PADDING, Math.min(x2, getWidth() - PADDING - fm.stringWidth(instruction2)));

		// Only draw if we have space
		if (y < getHeight() - 20) {
			g2d.drawString(instruction2, x2, y);
			y += fm.getHeight() + 3;
		}

		// Game info - only if we have space and use smaller font
		if (y < getHeight() - 12) {
			g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); // Smaller font
			g2d.setColor(TEXT_SECONDARY);
			String gameInfo = "Bubble Budz Mini-Game";
			fm = g2d.getFontMetrics();

			// Check if game info fits
			if (fm.stringWidth(gameInfo) <= getWidth() - PADDING * 2) {
				int infoX = centerX - fm.stringWidth(gameInfo) / 2;
				infoX = Math.max(PADDING, Math.min(infoX, getWidth() - PADDING - fm.stringWidth(gameInfo)));
				g2d.drawString(gameInfo, infoX, y);
			}
		}
	}

	private void drawLoading(Graphics2D g2d) {
		g2d.setColor(new Color(5, 5, 15, 250));
		g2d.fillRect(0, 0, getWidth(), getHeight());

		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;

		// Neon spinner
		long elapsed = System.currentTimeMillis() - loadingStartTime;
		double angle = Math.toRadians((elapsed / 8.0) % 360);

		g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(TEXT_NEON_CYAN);
		g2d.rotate(angle, centerX, centerY - 10);
		g2d.drawArc(centerX - 25, centerY - 35, 50, 50, 0, 270);
		g2d.rotate(-angle, centerX, centerY - 10);

		// Loading text
		g2d.setFont(FONT_LOADING);
		String text = loadingMessage.isEmpty() ? "Loading..." : loadingMessage;
		FontMetrics fm = g2d.getFontMetrics();
		int textX = centerX - fm.stringWidth(text) / 2;
		g2d.setColor(TEXT_PRIMARY);
		g2d.drawString(text, textX, centerY + 30);
	}

	// Simplified Game Logic
	private void startGame() {
		gameActive = true;
		roundStartTime = System.currentTimeMillis();
		currentScore = 0;
		bubbles.clear();
		popEffects.clear();
		lastBubbleSpawn = 0;
		nextSpawnDelay = 300 + random.nextInt(400);
	}

	private void updateGame() {
		// Check round timer - only affects scoring, not rendering
		long roundElapsed = System.currentTimeMillis() - roundStartTime;
		if (roundElapsed >= ROUND_DURATION) {
			// Always reload best score from file before comparison
			loadBestScore();

			// Save score only if current round score is higher than file best
			if (currentScore > bestScore) {
				bestScore = currentScore;
				saveBestScore();
				System.out.println("New high score achieved: " + bestScore);
			} else {
				System.out.println("Round score " + currentScore + " did not beat best score " + bestScore);
			}

			// Start new round - just reset score and timer
			roundStartTime = System.currentTimeMillis();
			currentScore = 0;
		}

		// Update bubbles
		bubbles.removeIf(bubble -> {
			bubble.update();
			return bubble.isExpired();
		});

		// Update pop effects
		popEffects.removeIf(effect -> {
			effect.update();
			return effect.isExpired();
		});

		// Dynamic spawning
		long currentTime = System.currentTimeMillis();
		if (bubbles.size() < MAX_BUBBLES && currentTime - lastBubbleSpawn > nextSpawnDelay) {
			boolean shouldSpawn = bubbles.size() < MIN_BUBBLES ||
				(bubbles.size() < MAX_BUBBLES * 0.8 && random.nextFloat() < 0.7f);

			if (shouldSpawn) {
				spawnBubble();
				lastBubbleSpawn = currentTime;
				nextSpawnDelay = 200 + random.nextInt(600);
				if (random.nextFloat() < 0.15f) {
					nextSpawnDelay = 50 + random.nextInt(150);
				}
			}
		}
	}

	private void spawnBubble() {
		if (gameArea == null) return;

		Color[] neonColors = {TEXT_NEON_CYAN, TEXT_NEON_PINK, TEXT_NEON_VIOLET, TEXT_NEON_GREEN};
		Color color = neonColors[random.nextInt(neonColors.length)];

		float radius = MIN_BUBBLE_RADIUS + random.nextFloat() * (MAX_BUBBLE_RADIUS - MIN_BUBBLE_RADIUS);

		// Find non-overlapping position
		float x, y;
		int attempts = 0;
		do {
			x = gameArea.x + radius + random.nextFloat() * (gameArea.width - radius * 2);
			y = gameArea.y + radius + random.nextFloat() * (gameArea.height - radius * 2);
			attempts++;
		} while (attempts < 50 && isPositionOverlapping(x, y, radius));

		if (attempts < 50) {
			bubbles.add(new Bubble(x, y, radius, color));
		}
	}

	private boolean isPositionOverlapping(float x, float y, float radius) {
		for (Bubble bubble : bubbles) {
			float dx = x - bubble.x;
			float dy = y - bubble.y;
			float distance = (float) Math.sqrt(dx * dx + dy * dy);
			if (distance < radius + bubble.targetRadius + 8) {
				return true;
			}
		}
		return false;
	}

	// Fast, skill-based click handling - immediate removal
	private void handleBubbleClick(int x, int y) {
		for (int i = bubbles.size() - 1; i >= 0; i--) {
			Bubble bubble = bubbles.get(i);
			if (!bubble.isClicked && bubble.contains(x, y)) {
				// Mark as clicked to prevent double-clicking
				bubble.isClicked = true;

				// Calculate score based on visual size for fairness
				int basePoints = Math.max(1, (int)(21 - bubble.currentRadius));
				float heightMultiplier = Math.max(1.0f, (float)getHeight() / BASE_VIEWPORT_HEIGHT);
				int heightBonus = (int)(basePoints * (heightMultiplier - 1.0f) * 0.5f);
				int totalPoints = basePoints + heightBonus;

				currentScore += totalPoints;

				// Create pop effect
				popEffects.add(new PopEffect(bubble.x, bubble.y, bubble.currentRadius, bubble.color));

				// IMMEDIATE removal - no delay for fast-paced gameplay
				bubbles.remove(i);

				return; // Only pop one bubble per click
			}
		}
	}

	// Persistent Storage
	public static String findCacheDir() {
		Path cacheDir = Paths.get(System.getProperty("user.home"), ".BestBudzCache");
		try {
			Files.createDirectories(cacheDir);
		} catch (IOException ex) {
			System.err.println("Failed to create cache directory: " + ex.getMessage());
		}
		return cacheDir.toAbsolutePath() + File.separator;
	}

	private void loadBestScore() {
		try {
			String cacheDir = findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.dat");

			if (Files.exists(scoreFile)) {
				String content = new String(Files.readAllBytes(scoreFile)).trim();
				if (!content.isEmpty()) {
					bestScore = Integer.parseInt(content);
					System.out.println("Loaded best score: " + bestScore);
				} else {
					bestScore = 0;
				}
			} else {
				bestScore = 0;
				System.out.println("No existing score file found, starting with best score: 0");
			}
		} catch (Exception e) {
			System.err.println("Failed to load best score: " + e.getMessage());
			bestScore = 0;
		}
	}

	private void saveBestScore() {
		try {
			// Re-read the file to get the current best score
			int filebestScore = 0;
			String cacheDir = findCacheDir();
			Path scoreFile = Paths.get(cacheDir, "bubblebudz.dat");

			if (Files.exists(scoreFile)) {
				try {
					String content = new String(Files.readAllBytes(scoreFile)).trim();
					if (!content.isEmpty()) {
						filebestScore = Integer.parseInt(content);
					}
				} catch (Exception e) {
					System.err.println("Error reading existing score: " + e.getMessage());
				}
			}

			// Only save if our best score is actually higher
			if (bestScore > filebestScore) {
				Files.write(scoreFile, String.valueOf(bestScore).getBytes());
				System.out.println("New best score saved: " + bestScore + " (previous: " + filebestScore + ")");
			} else {
				System.out.println("Score " + bestScore + " not higher than file best " + filebestScore + ", not saving");
			}
		} catch (Exception e) {
			System.err.println("Failed to save best score: " + e.getMessage());
		}
	}

	// Public API
	public void setGraphicsContext(Graphics2D graphics, GameCanvas canvas) {
		this.activeGraphics = graphics;
		this.activeCanvas = canvas;
	}

	public void refreshLoginState() {
		boolean nowVisible = !Client.loggedIn;
		setVisible(nowVisible);

		if (nowVisible && !wasVisible) {
			// Panel is becoming visible (activation) - load best score
			loadBestScore();
			System.out.println("Panel activated, best score loaded: " + bestScore);
			if (!gameActive) {
				startGame();
			}
		} else if (!nowVisible && wasVisible) {
			// Panel is becoming hidden - save current score if better
			if (currentScore > 0) {
				loadBestScore(); // Refresh from file first
				if (currentScore > bestScore) {
					bestScore = currentScore;
					saveBestScore();
				}
			}
		}

		wasVisible = nowVisible;
		repaint();
	}

	public void startLoading(String message) {
		isLoading = true;
		gameActive = false;
		loadingMessage = message;
		loadingStartTime = System.currentTimeMillis();
		repaint();
	}

	public void stopLoading() {
		isLoading = false;
		if (!isLoading && isVisible()) {
			gameActive = true;
			// Reload best score when transitioning from loading to game
			loadBestScore();
			System.out.println("Loading stopped, best score reloaded: " + bestScore);
			startGame();
		}
		repaint();
	}

	public void refreshLogo() {
		loadLogo();
		repaint();
	}

	public void dispose() {
		if (gameTimer != null) {
			gameTimer.stop();
		}
		saveBestScore();

		if (logoImage != null) {
			logoImage.flush();
			logoImage = null;
		}
	}
}