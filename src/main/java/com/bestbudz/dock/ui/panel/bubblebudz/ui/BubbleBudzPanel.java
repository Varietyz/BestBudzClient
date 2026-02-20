package com.bestbudz.dock.ui.panel.bubblebudz.ui;

import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectEventListener;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectManager;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectLoader;
import com.bestbudz.dock.ui.panel.bubblebudz.core.GameStateManager;
import com.bestbudz.dock.ui.panel.bubblebudz.core.GameEventManager;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.ClassicGameMode;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.GameMode;
import com.bestbudz.dock.ui.panel.bubblebudz.system.input.BubbleInputHandler;
import com.bestbudz.dock.ui.panel.bubblebudz.system.assets.AssetManager;
import com.bestbudz.dock.ui.panel.bubblebudz.game.scoring.BubbleBudzScoreManager;
import com.bestbudz.dock.ui.panel.bubblebudz.core.BubbleBudzGame;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderContext;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderManager;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.BackgroundRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.BubbleRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.GameModeUIRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.GeometricBubbleRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.EffectRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.FooterRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.HeaderRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.components.LoadingRenderer;
import com.bestbudz.dock.ui.panel.bubblebudz.ui.managers.GameModeManager;
import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Bubble Budz - Interactive Loading Panel Mini-Game
 * Now supporting both classic bubbles and geometric shapes!
 * Press 'G' to toggle between modes
 */
public class BubbleBudzPanel extends JPanel implements GameEventManager.GameEventListener {
	private final Client client;

	// Core Systems
	private final GameStateManager stateManager = new GameStateManager();
	private final GameEventManager eventManager = new GameEventManager();
	private GameMode currentGameMode = new ClassicGameMode(); // Start with classic
	private final RenderManager renderManager = new RenderManager();
	private final AssetManager assetManager = new AssetManager();
	private final GameModeManager gameModeManager = new GameModeManager(this::onGameModeChanged);

	// Game Components
	private final BubbleBudzScoreManager highscoreManager = new BubbleBudzScoreManager();
	private final EffectManager effectsManager = new EffectManager();
	private final BubbleBudzGame gameLogic = new BubbleBudzGame();
	private BubbleInputHandler inputHandler;

	// Mode tracking
	private boolean useGeometricMode = false;

	// State
	private int currentScore = 0;
	private long roundStartTime = 0;
	private boolean wasVisible = false;
	private Timer gameTimer;

	// Loading state (maintained for compatibility)
	private boolean isLoading = false;
	private String loadingMessage = "";
	private long loadingStartTime = 0;

	public BubbleBudzPanel(Client client) {
		this.client = client;
		initialize();
	}

	private void initialize() {
		setOpaque(false);
		setLayout(null);
		setFocusable(true); // Allow keyboard input

		// Setup initial input handler
		this.inputHandler = new BubbleInputHandler(gameLogic, eventManager, currentGameMode);

		// Setup systems
		EffectLoader.loadAllEffects(effectsManager);
		setupEventHandlers();
		setupRenderComponents();
		setupEffectEventListener();
		setupMouse();

		// Connect game logic to game mode
		gameLogic.setGameMode(currentGameMode);

		// Setup timer
		gameTimer = new Timer(16, e -> {
			updateGame();
			repaint();
		});
		gameTimer.start();

		// Initial state
		boolean initialVisible = !Client.loggedIn;
		setVisible(initialVisible);
		wasVisible = initialVisible;

		highscoreManager.loadBestScore();
		startGame();
	}

	private void setupEventHandlers() {
		eventManager.subscribe(GameEventManager.ScoreChangedEvent.class, this);
		eventManager.subscribe(GameEventManager.RoundCompleteEvent.class, this);
	}

	private void setupRenderComponents() {
		updateRenderComponents();
	}

	private void updateRenderComponents() {
		// Clear existing components
		renderManager.clearComponents();

		// Add base components
		renderManager.addComponent(new BackgroundRenderer());
		renderManager.addComponent(new HeaderRenderer());

		// Add appropriate bubble renderer based on mode
		if (useGeometricMode) {
			renderManager.addComponent(new GeometricBubbleRenderer(gameLogic));
		} else {
			renderManager.addComponent(new BubbleRenderer(gameLogic));
		}

		renderManager.addComponent(new EffectRenderer(effectsManager));

		// Add game mode UI renderer
		renderManager.addComponent(new GameModeUIRenderer(gameModeManager));

		renderManager.addComponent(new FooterRenderer());
		renderManager.addComponent(new LoadingRenderer());
	}

	private void setupMouse() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// Handle game mode UI first
				gameModeManager.handleMousePressed(e);

				// Only handle game clicks if overlay is not visible and not loading
				if (!gameModeManager.isOverlayVisible() && !isLoading) {
					inputHandler.setPanelHeight(getHeight());
					inputHandler.handleClick(e.getX(), e.getY(), e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				gameModeManager.handleMouseReleased(e);
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				gameModeManager.handleMouseMoved(e);
			}
		});
	}

	private void onGameModeChanged(GameMode newGameMode, String modeName) {
		GameMode oldGameMode = currentGameMode;
		currentGameMode = newGameMode;
		useGeometricMode = modeName.equals("Geometric");

		System.out.println("Switched to " + modeName + " Mode!");

		// Update game components
		gameLogic.setGameMode(currentGameMode);
		inputHandler = new BubbleInputHandler(gameLogic, eventManager, currentGameMode);

		// Update renderer
		updateRenderComponents();

		// Update game mode manager's current mode
		gameModeManager.setCurrentMode(modeName);

		// Restart game with new mode (preserves current score)
		long currentTime = System.currentTimeMillis();
		roundStartTime = currentTime;
		currentGameMode.setRoundStartTime(roundStartTime);
		currentGameMode.setCurrentScore(currentScore);
		gameLogic.clearBubbles();
		effectsManager.clearEffects();

		// Focus back to panel for continued input
		requestFocusInWindow();
	}

	/**
	 * Get current game mode name for display
	 */
	public String getCurrentModeName() {
		return useGeometricMode ? "Geometric" : "Classic";
	}

	private void startGame() {
		roundStartTime = System.currentTimeMillis();
		currentScore = 0;
		currentGameMode.setRoundStartTime(roundStartTime);
		currentGameMode.setCurrentScore(currentScore);
		gameLogic.clearBubbles();
		effectsManager.clearEffects();
	}

	private void updateGame() {
		if (Client.loggedIn) return;
		long currentTime = System.currentTimeMillis();

		// Check for round completion
		if (currentGameMode.isRoundComplete(currentTime)) {
			highscoreManager.updateIfBetter(currentScore);
			eventManager.publish(new GameEventManager.RoundCompleteEvent(
				currentScore, highscoreManager.isNewHighScore(currentScore)));

			// Reset for new round
			roundStartTime = currentTime;
			currentScore = 0;
			currentGameMode.setRoundStartTime(roundStartTime);
			currentGameMode.setCurrentScore(currentScore);
		}

		// Update game elements
		gameLogic.updateBubbles();
		effectsManager.updateEffects();
		gameLogic.spawnBubbleIfNeeded(BubbleBudzRenderer.calculateGameArea(getWidth(), getHeight()));
	}

	private void setupEffectEventListener() {
		// Create effect event listener
		EffectEventListener effectEventListener = new EffectEventListener(effectsManager);

		// Subscribe to game events
		effectEventListener.subscribeToEvents(eventManager);

		System.out.println("Effect event listener registered for automatic effect creation");
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (Client.loggedIn) return;
		super.paintComponent(g);

		if (!Client.loggedIn && isVisible()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			Rectangle gameArea = BubbleBudzRenderer.calculateGameArea(getWidth(), getHeight());
			RenderContext context = new RenderContext(
				getWidth(), getHeight(), gameArea,
				assetManager.getImage("loading/bubblebudz.png", 80),
				roundStartTime, currentScore, highscoreManager.getBestScore(),
				isLoading, loadingMessage, loadingStartTime
			);

			renderManager.render(g2d, context);

			g2d.dispose();
		}
	}

	@Override
	public void onEvent(GameEventManager.GameEvent event) {
		if (event instanceof GameEventManager.ScoreChangedEvent) {
			currentScore = ((GameEventManager.ScoreChangedEvent) event).newScore;
		}
	}

	public void refreshLoginState() {
		boolean nowVisible = !Client.loggedIn;
		setVisible(nowVisible);

		if (nowVisible && !wasVisible) {
			highscoreManager.loadBestScore();
			startGame();
			requestFocusInWindow(); // Ensure keyboard input works
		} else if (!nowVisible && wasVisible && currentScore > 0) {
			highscoreManager.updateIfBetter(currentScore);
		}

		wasVisible = nowVisible;
		repaint();
	}

	public void dispose() {
		if (gameTimer != null) gameTimer.stop();
		highscoreManager.saveBestScore();
		assetManager.dispose();
	}
}