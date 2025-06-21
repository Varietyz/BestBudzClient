package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.dock.definitions.ItemBonusManager;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.ui.manager.UIModalManager;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.GameCanvas;
import com.bestbudz.engine.core.GameEngine;
import com.bestbudz.engine.core.GameLoader;
import com.bestbudz.ui.handling.input.Keyboard;
import com.bestbudz.ui.handling.input.MouseManager;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

/**
 * Enhanced ClientLauncher with Post-GameLoader GPU Management
 *
 * GPU initialization now happens AFTER graphics are loaded in GameLoader
 * for proper OpenGL context and resource management.
 */
public final class ClientLauncher {
	private UIModalManager uiModalManager;
	public static GPUContextManager gpuContextManager;
	public static boolean gpuInitialized = false;
	private static GameEngine gameEngine;
	private static Thread gameThread;

	public static void main(String[] args) {
		try {
			// ===== PHASE 1: CORE SYSTEMS =====
			System.out.println("[ClientLauncher] Initializing core systems...");
			initializeCoreSystems();

			// ===== PHASE 2: UI SETUP =====
			System.out.println("[ClientLauncher] Setting up user interface...");
			setupUserInterface();

		} catch (Exception exception) {
			System.err.println("[ClientLauncher] Critical error during startup: " + exception.getMessage());
			exception.printStackTrace();

			// Attempt graceful shutdown
			performEmergencyShutdown();
			System.exit(1);
		}
	}

	/**
	 * Initialize core game systems
	 */
	private static void initializeCoreSystems() throws Exception {
		// Load settings
		SettingHandler.load();

		// Configure client settings
		Client.nodeID = 10;
		Client.portOff = 0;
		Client.setHighMem();
		Client.isMembers = true;

		// Initialize signlink
		Signlink.storeid = 32;
		Signlink.startpriv(InetAddress.getLocalHost());

		ItemBonusManager.initialize();

		if (!ItemBonusManager.initialize()) {
			System.err.println("[ClientLauncher] ⚠️ Failed to load item bonuses, tooltips may be incomplete");
		}

		// Set engine configuration
		EngineConfig.MIN_WIDTH = Client.frameWidth;
		EngineConfig.MIN_HEIGHT = Client.frameHeight;

		System.out.println("[ClientLauncher] Core systems initialized");
	}

	/**
	 * Setup the user interface and game window
	 */
	private static void setupUserInterface() throws Exception {
		// Set Look and Feel
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception ex) {
			System.err.println("[ClientLauncher] Failed to initialize FlatLaf: " + ex.getMessage());
		}

		// Create main game window
		final JFrame frame = createMainGameWindow();

		// Create game canvas
		GameCanvas canvas = new GameCanvas();
		configureGameCanvas(frame, canvas);

		// Initialize game systems
		initializeGameSystems(canvas);

		// Create and start game engine
		startGameEngine(canvas);

		// Setup UI dock
		setupUIDock(frame);

		// Setup window event handlers
		setupWindowEventHandlers(frame);

		System.out.println("[ClientLauncher] ✅ User interface setup complete");
	}

	/**
	 * Create the main game window
	 */
	private static JFrame createMainGameWindow() {
		final JFrame frame = new JFrame(EngineConfig.TITLE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(1280, 758));
		return frame;
	}

	/**
	 * Configure the game canvas
	 */
	private static void configureGameCanvas(JFrame frame, GameCanvas canvas) {
		canvas.setPreferredSize(new Dimension(Client.frameWidth, Client.frameHeight));
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// Wait for canvas to be displayable
		while (!canvas.isDisplayable()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignored) {}
		}
	}

	/**
	 * Initialize game systems (client, input, etc.)
	 */
	private static void initializeGameSystems(GameCanvas canvas) throws InterruptedException
	{
		// Create client instance
		Client client = new Client();
		Client.instance = client;

		// Setup canvas and buffer strategy
		Client.instance.refreshFrameSize(canvas, canvas.getWidth(), canvas.getHeight());
		canvas.createBufferStrategy(EngineConfig.BUFFERS);
		GameLoader.setCanvas(canvas);

		// Setup input handlers
		MouseManager mouseManager = new MouseManager();
		Keyboard keyboard = new Keyboard();

		Client.setCanvas(canvas);

		// Add input listeners
		canvas.addKeyListener(keyboard);
		canvas.addMouseListener(mouseManager);
		canvas.addMouseMotionListener(mouseManager);
		canvas.addMouseWheelListener(mouseManager);
		canvas.addFocusListener(client);
		canvas.requestFocusInWindow();

		System.out.println("[ClientLauncher] Game systems initialized");
	}

	/**
	 * Start game engine - GPU initialization will happen AFTER GameLoader completes
	 */
	private static void startGameEngine(GameCanvas canvas) {
		gameEngine = new GameEngine(canvas, Client.instance);
		gameThread = new Thread(() -> {
			// Start the game engine - this will call GameLoader
			gameEngine.run();
		}, "GameLoop");

		// Set thread priority for smooth gameplay
		gameThread.setPriority(Thread.NORM_PRIORITY + 1);
		gameThread.start();

		System.out.println("[ClientLauncher] Game engine started on thread: " + gameThread.getName());
	}

	/**
	 * This method should be called FROM GameLoader AFTER graphics are loaded
	 * Call this from GameLoader.loadComplete() or similar
	 */
	public static void initializeGPUAfterGraphicsLoad() {
		System.out.println("[ClientLauncher] Graphics loading complete, initializing GPU subsystem...");

		try {
			// Initialize the GPU context manager
			gpuContextManager = GPUContextManager.getInstance();
			if (!gpuContextManager.initialize()) {
				System.err.println("[ClientLauncher] ⚠️ GPU context manager failed to initialize");
				System.err.println("[ClientLauncher] Continuing with CPU-only rendering");
				gpuInitialized = false;
				return;
			}

			// Small delay to ensure context is ready
			Thread.sleep(50);

			// Initialize GPU rendering with proper context
			gpuInitialized = initializeGPURenderingWithContext();

			if (gpuInitialized) {
				System.out.println("[ClientLauncher] ✅ GPU subsystem initialized successfully after graphics load");
			} else {
				System.err.println("[ClientLauncher] ⚠️ GPU rendering failed to initialize, continuing with CPU rendering");
			}

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ❌ GPU initialization failed after graphics load: " + e.getMessage());
			e.printStackTrace();
			gpuInitialized = false;
		}
	}

	/**
	 * Initialize GPU rendering with proper context acquisition
	 * This runs on the game thread with graphics already loaded
	 */
	private static boolean initializeGPURenderingWithContext() {
		final int MAX_RETRIES = 3;
		final int RETRY_DELAY_MS = 200;

		for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
			GPUContextManager.ContextToken contextToken = null;

			try {
				System.out.println("[ClientLauncher] GPU rendering initialization attempt " + attempt + "/" + MAX_RETRIES);

				// Acquire GPU context for this thread
				long timeoutMs = 2000 + (attempt * 500);
				contextToken = gpuContextManager.acquireContext("Post-GameLoader GPU Init", timeoutMs);

				if (contextToken == null) {
					System.err.println("[ClientLauncher] Failed to acquire GPU context on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS);
						continue;
					}
					return false;
				}

				// Verify context validity
				if (!contextToken.isValid() || !gpuContextManager.isContextCurrent()) {
					System.err.println("[ClientLauncher] GPU context invalid on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS);
						continue;
					}
					return false;
				}

				System.out.println("[ClientLauncher] GPU context acquired successfully, initializing rendering engine...");

				// Initialize the GPU rendering engine
				if(EngineConfig.ENABLE_GPU) GPURenderingEngine.initialize();

				if (GPURenderingEngine.isEnabled()) {
					System.out.println("[ClientLauncher] ✅ GPU rendering engine initialized successfully");
					return true;
				} else {
					System.err.println("[ClientLauncher] GPU rendering engine not enabled after initialization");
				}

			} catch (Exception e) {
				System.err.println("[ClientLauncher] GPU rendering initialization attempt " + attempt + " failed: " + e.getMessage());
				if (attempt == MAX_RETRIES) {
					e.printStackTrace();
				}

			} finally {
				// Always release the context token
				if (contextToken != null) {
					try {
						contextToken.close();
					} catch (Exception e) {
						System.err.println("[ClientLauncher] Error releasing context token: " + e.getMessage());
					}
				}
			}

			// Wait before next attempt
			if (attempt < MAX_RETRIES) {
				try {
					Thread.sleep(RETRY_DELAY_MS);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * Setup UI dock frame
	 */
	private static void setupUIDock(JFrame frame) {
		UIDockFrame uiDock = new UIDockFrame(frame);
		uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());

		// Handle window state changes
		frame.addWindowStateListener(e -> {
			boolean maximized = (e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
			SwingUtilities.invokeLater(() -> {
				uiDock.setVisible(!maximized);
				if (!maximized) {
					int dockX = frame.getX() + frame.getWidth();
					uiDock.setLocation(dockX, frame.getY());
					uiDock.setSize(300, frame.getHeight());
					uiDock.revalidate();
					uiDock.repaint();
				}
			});
		});

		// Handle frame movement and resizing
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				Point location = frame.getLocationOnScreen();
				int x = location.x + frame.getWidth();
				int y = location.y;
				uiDock.setLocation(x, y);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());
				int height = frame.getHeight();
				uiDock.setSize(uiDock.getWidth(), height - 8);
				uiDock.revalidate();
				uiDock.repaint();
			}
		});

		System.out.println("[ClientLauncher] UI dock configured");
	}

	/**
	 * Setup window event handlers with proper GPU cleanup
	 */
	private static void setupWindowEventHandlers(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("[ClientLauncher] Application shutdown requested...");
				performGracefulShutdown();
			}
		});
	}

	/**
	 * Perform graceful shutdown with proper GPU cleanup
	 */
	private static void performGracefulShutdown() {
		try {
			System.out.println("[ClientLauncher] Starting graceful shutdown...");

			// ===== PHASE 1: STOP GAME ENGINE =====
			if (gameEngine != null) {
				System.out.println("[ClientLauncher] Shutting down game engine...");
				gameEngine.shutdown();

				// Wait for game thread to finish
				if (gameThread != null && gameThread.isAlive()) {
					try {
						gameThread.join(2000);
						if (gameThread.isAlive()) {
							System.err.println("[ClientLauncher] ⚠️ Game thread did not shut down gracefully");
							gameThread.interrupt();
						} else {
							System.out.println("[ClientLauncher] ✅ Game thread shut down successfully");
						}
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
						System.err.println("[ClientLauncher] Shutdown interrupted");
					}
				}
			}

			// ===== PHASE 2: GPU CLEANUP =====
			if (gpuInitialized) {
				System.out.println("[ClientLauncher] Cleaning up GPU resources...");
				try {
					GPURenderingEngine.cleanup();
					if (gpuContextManager != null) {
						gpuContextManager.shutdown();
					}
					System.out.println("[ClientLauncher] ✅ GPU resources cleaned up successfully");
				} catch (Exception e) {
					System.err.println("[ClientLauncher] ⚠️ Error during GPU cleanup: " + e.getMessage());
				}
			}

			// ===== PHASE 3: GAME DATA CLEANUP =====
			if (Client.instance != null) {
				System.out.println("[ClientLauncher] Cleaning up game data...");
				try {
					SettingHandler.save();
					Client.instance.cleanUpForQuit();
					System.out.println("[ClientLauncher] ✅ Game data cleaned up successfully");
				} catch (Exception e) {
					System.err.println("[ClientLauncher] ⚠️ Error during game data cleanup: " + e.getMessage());
				}
			}

			System.out.println("[ClientLauncher] ✅ Graceful shutdown completed");

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ❌ Error during graceful shutdown: " + e.getMessage());
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/**
	 * Emergency shutdown for critical errors
	 */
	private static void performEmergencyShutdown() {
		System.err.println("[ClientLauncher] Performing emergency shutdown...");
		try {
			if (gpuInitialized && gpuContextManager != null) {
				gpuContextManager.shutdown();
			}
			if (Client.instance != null) {
				SettingHandler.save();
			}
		} catch (Exception e) {
			System.err.println("[ClientLauncher] Error during emergency shutdown: " + e.getMessage());
		}
		System.err.println("[ClientLauncher] Emergency shutdown completed");
	}

	/**
	 * Handle game loads with GPU context persistence
	 */
	public static void handleGameLoad() {
		if (!gpuInitialized) {
			return;
		}

		try {
			System.out.println("[ClientLauncher] Handling game load with GPU persistence...");
			GPURenderingEngine.persistThroughGameLoad();
			System.out.println("[ClientLauncher] ✅ Game load handled with GPU persistence");

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ⚠️ Error handling game load: " + e.getMessage());

			try {
				System.out.println("[ClientLauncher] Attempting GPU recovery...");
				if (GPURenderingEngine.emergencyReset()) {
					System.out.println("[ClientLauncher] ✅ GPU recovery successful");
				} else {
					System.err.println("[ClientLauncher] ❌ GPU recovery failed, disabling GPU");
					gpuInitialized = false;
				}
			} catch (Exception recoveryError) {
				System.err.println("[ClientLauncher] ❌ GPU recovery error: " + recoveryError.getMessage());
				gpuInitialized = false;
			}
		}
	}

	/**
	 * Get GPU status for debugging
	 */
	public static String getGPUStatus() {
		if (!gpuInitialized) {
			return "GPU: Disabled/Failed";
		}
		try {
			return GPURenderingEngine.getGPUStatistics();
		} catch (Exception e) {
			return "GPU: Error getting statistics - " + e.getMessage();
		}
	}

	/**
	 * Check if GPU is initialized and working
	 */
	public static boolean isGPUInitialized() {
		return gpuInitialized;
	}

	public UIModalManager getUIModalManager() {
		return uiModalManager;
	}
}