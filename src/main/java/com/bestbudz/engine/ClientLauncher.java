package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.dock.definitions.ItemBonusManager;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.ui.manager.UIModalManager;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.loading.LoadingVisual;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.RS317GPUInterface;
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
	private static LoadingVisual loader;

	public static void main(String[] args) {
		try {
			// ===== SHOW SIMPLE LOADER FIRST =====
			System.out.println("[ClientLauncher] Starting Best Budz...");
			showLoader();

			// ===== INITIALIZE CORE SYSTEMS (same as before) =====
			System.out.println("[ClientLauncher] Initializing core system...");
			updateLoaderProgress(20);
			initializeCoreSystems();

			updateLoaderProgress(60);
			updateLoaderStatus("Starting game engine...");

			// ===== SETUP UI (canvas will be created but hidden until loading done) =====
			System.out.println("[ClientLauncher] Setting up user interface...");
			setupUserInterface();

		} catch (Exception exception) {
			System.err.println("[ClientLauncher] Critical error during startup: " + exception.getMessage());
			exception.printStackTrace();

			// Close loader on error
			if (loader != null) {
				loader.closeLoader();
			}

			performEmergencyShutdown();
			System.exit(1);
		}
	}

	// ADD THIS METHOD
	private static void showLoader() {
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(new FlatDarkLaf());
			} catch (Exception ex) {
				System.err.println("[ClientLauncher] Failed to set look and feel: " + ex.getMessage());
			}

		});

		// Wait for loader to appear
		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {}
	}

	// ADD THIS METHOD
	private static void updateLoaderProgress(int progress) {
		if (loader != null) {
			loader.updateProgress(progress);
		}
	}

	// ADD THIS METHOD
	private static void updateLoaderStatus(String status) {
		if (loader != null) {
			loader.updateStatus(status);
		}
	}

	/**
	 * Initialize core game system
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

		System.out.println("[ClientLauncher] Core system initialized");
	}

	// MODIFY setupUserInterface() method - add this at the BEGINNING
	private static void setupUserInterface() throws Exception {
		// Create main game window but DON'T show it yet
		final JFrame frame = createMainGameWindow();

		// Create game canvas
		GameCanvas canvas = new GameCanvas();
		configureGameCanvas(frame, canvas, false); // Pass false to not show yet

		// Initialize game system
		updateLoaderProgress(80);
		updateLoaderStatus("Initializing game system...");
		initializeGameSystems(canvas);

		// Create and start game engine
		updateLoaderProgress(90);
		updateLoaderStatus("Starting game engine...");
		startGameEngine(canvas);

		// Setup window event handlers
		setupWindowEventHandlers(frame);

		// DON'T setup dock here - wait until main window is shown!

		System.out.println("[ClientLauncher] ✅ User interface setup complete (hidden)");
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
	private static void configureGameCanvas(JFrame frame, GameCanvas canvas, boolean showImmediately) {
		canvas.setPreferredSize(new Dimension(Client.frameWidth, Client.frameHeight));
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);

		if (showImmediately) {
			frame.setVisible(true); // Only show if requested
		}

		// Wait for canvas to be displayable (even if not visible)
		while (!canvas.isDisplayable()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignored) {}
		}
	}

	public static void onGameLoadingComplete() {
		System.out.println("[ClientLauncher] Game loading complete - showing main window");

		updateLoaderProgress(100);
		updateLoaderStatus("Ready!");

		// Small delay for visual feedback
		try {
			Thread.sleep(300);
		} catch (InterruptedException ignored) {}

		// Close loader
		if (loader != null) {
			loader.closeLoader();
			loader = null;
		}

		// Show main window FIRST
		SwingUtilities.invokeLater(() -> {
			JFrame mainFrame = null;

			// Find and show the main frame
			for (Frame frame : Frame.getFrames()) {
				if (frame instanceof JFrame && frame.getTitle().equals(EngineConfig.TITLE)) {
					mainFrame = (JFrame) frame;
					frame.setVisible(true);
					frame.toFront();
					break;
				}
			}

			// THEN setup dock after main window is visible and settled
			if (mainFrame != null) {
				final JFrame finalMainFrame = mainFrame;
				setupUIDock(finalMainFrame);
			}
		});
	}

	/**
	 * Initialize game system (client, input, etc.)
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

		System.out.println("[ClientLauncher] Game system initialized");
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

// Fixed initializeGPUAfterGraphicsLoad method in ClientLauncher.java

	public static void initializeGPUAfterGraphicsLoad() {
		System.out.println("[ClientLauncher] Graphics loaded, initializing GPU rendering (PROPER)...");

		try {
			// Wait for graphics to stabilize
			Thread.sleep(500);

			// CRITICAL FIX: Initialize context manager FIRST if not already done
			if (gpuContextManager == null) {
				System.out.println("[ClientLauncher] Creating GPU context manager...");
				gpuContextManager = GPUContextManager.getInstance();

				// Initialize the context manager - this was missing!
				if (!gpuContextManager.initialize()) {
					System.err.println("[ClientLauncher] ❌ GPU context manager initialization failed");
					gpuInitialized = false;
					return;
				}
				System.out.println("[ClientLauncher] ✅ GPU context manager initialized");
			}

			// Use the improved context-based GPU initialization
			boolean success = initializeGPURenderingWithContext();

			if (success) {
				// Now initialize the RS317 GPU interface
				gpuInitialized = RS317GPUInterface.initialize();

				if (gpuInitialized) {
					System.out.println("[ClientLauncher] ✅ GPU rendering initialized successfully");

					if (Client.frameWidth > 0 && Client.frameHeight > 0) {
						RS317GPUInterface.setScreenSize(Client.frameWidth, Client.frameHeight);
					}
				} else {
					System.out.println("[ClientLauncher] ⚠️ RS317 GPU interface failed, using CPU fallback");
				}
			} else {
				System.out.println("[ClientLauncher] ⚠️ GPU context initialization failed, using CPU fallback");
				gpuInitialized = false;
			}

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ❌ GPU initialization error: " + e.getMessage());
			e.printStackTrace();
			gpuInitialized = false;
		}

		System.out.println("[ClientLauncher] Graphics initialization complete (GPU: " +
			(gpuInitialized ? "ENABLED" : "DISABLED") + ")");
	}

	/**
	 * FIXED: Initialize GPU rendering with proper context acquisition and error handling
	 */
	private static boolean initializeGPURenderingWithContext() {
		final int MAX_RETRIES = 3;
		final int RETRY_DELAY_MS = 200;

		for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
			GPUContextManager.ContextToken contextToken = null;

			try {
				System.out.println("[ClientLauncher] GPU rendering initialization attempt " + attempt + "/" + MAX_RETRIES);

				// CRITICAL FIX: Check context manager validity first
				if (gpuContextManager == null) {
					System.err.println("[ClientLauncher] GPU context manager is null on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS);
						continue;
					}
					return false;
				}

				// Acquire GPU context for this thread with longer timeout
				long timeoutMs = 2000 + (attempt * 1000); // Increase timeout per attempt
				contextToken = gpuContextManager.acquireContext("Post-GameLoader GPU Init", timeoutMs);

				// CRITICAL FIX: Better null checking
				if (contextToken == null) {
					System.err.println("[ClientLauncher] Failed to acquire GPU context on attempt " + attempt +
						" (timeout: " + timeoutMs + "ms)");
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS * attempt); // Progressive backoff
						continue;
					}
					return false;
				}

				// Verify context validity with additional checks
				if (!contextToken.isValid()) {
					System.err.println("[ClientLauncher] GPU context invalid on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS);
						continue;
					}
					return false;
				}

				// CRITICAL FIX: Check if context is actually current
				if (!gpuContextManager.isContextCurrent()) {
					System.err.println("[ClientLauncher] GPU context not current on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAY_MS);
						continue;
					}
					return false;
				}

				System.out.println("[ClientLauncher] GPU context acquired successfully, initializing rendering engine...");

				// Initialize the GPU rendering engine ONLY if config allows it
				if (EngineConfig.ENABLE_GPU) {
					GPURenderingEngine.initialize();

					if (GPURenderingEngine.isEnabled()) {
						System.out.println("[ClientLauncher] ✅ GPU rendering engine initialized successfully");
						return true;
					} else {
						System.err.println("[ClientLauncher] GPU rendering engine not enabled after initialization");
						String error = GPURenderingEngine.getLastError();
						if (error != null && !error.isEmpty()) {
							System.err.println("[ClientLauncher] GPU Error: " + error);
						}
					}
				} else {
					System.out.println("[ClientLauncher] GPU disabled in config, skipping engine initialization");
					return false;
				}

			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				System.err.println("[ClientLauncher] GPU initialization interrupted on attempt " + attempt);
				return false;
			} catch (Exception e) {
				System.err.println("[ClientLauncher] GPU rendering initialization attempt " + attempt + " failed: " + e.getMessage());
				if (attempt == MAX_RETRIES) {
					e.printStackTrace();
				}

			} finally {
				// CRITICAL FIX: Always release the context token safely
				if (contextToken != null) {
					try {
						contextToken.close();
					} catch (Exception e) {
						System.err.println("[ClientLauncher] Error releasing context token: " + e.getMessage());
					}
				}
			}

			// Wait before next attempt (progressive backoff)
			if (attempt < MAX_RETRIES) {
				try {
					Thread.sleep(RETRY_DELAY_MS * attempt);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
		}

		System.err.println("[ClientLauncher] ❌ All GPU initialization attempts failed");
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

			// ===== GPU CLEANUP =====
			if (gpuInitialized) {
				System.out.println("[ClientLauncher] Cleaning up GPU resources...");
				try {
					RS317GPUInterface.cleanup();
					System.out.println("[ClientLauncher] ✅ GPU resources cleaned up successfully");
				} catch (Exception e) {
					System.err.println("[ClientLauncher] ⚠️ Error during GPU cleanup: " + e.getMessage());
					// Don't crash on cleanup errors
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

	/*
	 * Update getGPUStatus to use the interface:
	 */
	public static String getGPUStatus() {
		return RS317GPUInterface.getStatus();
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