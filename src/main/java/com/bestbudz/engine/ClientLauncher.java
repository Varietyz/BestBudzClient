package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.dock.definitions.ItemBonusManager;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.loading.LoadingVisual;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.GPUModelRenderer;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ClientLauncher {

	public static GPUContextManager gpuContextManager;
	public static boolean gpuInitialized = false;
	private static GameEngine gameEngine;
	private static Thread gameThread;
	private static LoadingVisual loader;

	private static final AtomicBoolean shutdownInProgress = new AtomicBoolean(false);
	private static final CountDownLatch initializationComplete = new CountDownLatch(1);
	private static JFrame mainFrame;
	private static UIDockFrame uiDock;

	private static final int SIGNLINK_STORE_ID = 32;
	private static final int CLIENT_NODE_ID = 10;
	private static final int CLIENT_PORT_OFFSET = 0;
	private static final int MIN_WINDOW_WIDTH = 1280;
	private static final int MIN_WINDOW_HEIGHT = 758;
	private static final int GPU_STABILITY_DELAY_MS = 500;
	private static final int SHUTDOWN_TIMEOUT_MS = 2000;
	private static final int LOADER_COMPLETION_DELAY_MS = 300;

	public static void main(String[] args) {
		try {
			System.out.println("[ClientLauncher] ===== Starting Best Budz Game Client =====");

			System.out.println("[ClientLauncher] Phase 1: Initializing loading interface...");
			showLoader();

			System.out.println("[ClientLauncher] Phase 2: Initializing core systems...");
			updateLoaderProgress(20);
			initializeCoreSystems();

			System.out.println("[ClientLauncher] Phase 3: Setting up user interface...");
			updateLoaderProgress(60);
			updateLoaderStatus("Starting game engine...");
			setupUserInterface();

			initializationComplete.countDown();
			System.out.println("[ClientLauncher] ===== Core initialization pipeline completed =====");

		} catch (Exception exception) {
			System.err.println("[ClientLauncher] ❌ Critical error during startup: " + exception.getMessage());
			exception.printStackTrace();

			if (loader != null) {
				try {
					loader.closeLoader();
				} catch (Exception e) {
					System.err.println("[ClientLauncher] Error closing loader: " + e.getMessage());
				}
			}

			performEmergencyShutdown();
			System.exit(1);
		}
	}

	private static void showLoader() {
		SwingUtilities.invokeLater(() -> {
			try {

				UIManager.setLookAndFeel(new FlatDarkLaf());
				System.out.println("[ClientLauncher] ✅ Dark theme applied successfully");

				loader = new LoadingVisual();
				loader.setVisible(true);
				System.out.println("[ClientLauncher] ✅ Loading visual displayed");

			} catch (Exception ex) {
				System.err.println("[ClientLauncher] ⚠️ Failed to initialize loading interface: " + ex.getMessage());
				ex.printStackTrace();
			}
		});

		try {
			Thread.sleep(100);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	private static void updateLoaderProgress(int progress) {
		if (loader != null) {
			SwingUtilities.invokeLater(() -> loader.updateProgress(progress));
		}
	}

	private static void updateLoaderStatus(String status) {
		if (loader != null) {
			SwingUtilities.invokeLater(() -> loader.updateStatus(status));
		}
	}

	private static void initializeCoreSystems() throws Exception {
		System.out.println("[ClientLauncher] Initializing settings handler...");
		SettingHandler.load();

		System.out.println("[ClientLauncher] Configuring client settings...");
		Client.nodeID = CLIENT_NODE_ID;
		Client.portOff = CLIENT_PORT_OFFSET;
		Client.setHighMem();
		Client.isMembers = true;

		System.out.println("[ClientLauncher] Initializing signlink system...");
		Signlink.storeid = SIGNLINK_STORE_ID;
		Signlink.startpriv(InetAddress.getLocalHost());

		System.out.println("[ClientLauncher] Initializing item bonus manager...");
		boolean itemBonusSuccess = ItemBonusManager.initialize();
		if (!itemBonusSuccess) {
			System.err.println("[ClientLauncher] ⚠️ Failed to load item bonuses, tooltips may be incomplete");
		} else {
			System.out.println("[ClientLauncher] ✅ Item bonus manager initialized successfully");
		}

		System.out.println("[ClientLauncher] Configuring engine parameters...");
		EngineConfig.MIN_WIDTH = Client.frameWidth;
		EngineConfig.MIN_HEIGHT = Client.frameHeight;

		System.out.println("[ClientLauncher] ✅ Core systems initialized successfully");
	}

	private static void setupUserInterface() throws Exception {

		mainFrame = createMainGameWindow();

		GameCanvas canvas = new GameCanvas();
		configureGameCanvas(mainFrame, canvas, false);

		updateLoaderProgress(80);
		updateLoaderStatus("Initializing game system...");
		initializeGameSystems(canvas);

		updateLoaderProgress(90);
		updateLoaderStatus("Starting game engine...");
		startGameEngine(canvas);

		setupWindowEventHandlers(mainFrame);

		System.out.println("[ClientLauncher] ✅ User interface setup complete");
	}

	private static JFrame createMainGameWindow() {
		final JFrame frame = new JFrame(EngineConfig.TITLE);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setResizable(true);
		frame.setMinimumSize(new Dimension(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT));

		frame.setPreferredSize(new Dimension(Client.frameWidth, Client.frameHeight));

		System.out.println("[ClientLauncher] ✅ Main game window created with title: " + EngineConfig.TITLE);
		return frame;
	}

	private static void configureGameCanvas(JFrame frame, GameCanvas canvas, boolean showImmediately) {
		canvas.setPreferredSize(new Dimension(Client.frameWidth, Client.frameHeight));
		frame.add(canvas);
		frame.pack();
		frame.setLocationRelativeTo(null);

		if (showImmediately) {
			frame.setVisible(true);
		}

		int attempts = 0;
		while (!canvas.isDisplayable() && attempts < 100) {
			try {
				Thread.sleep(10);
				attempts++;
			} catch (InterruptedException ignored) {
				Thread.currentThread().interrupt();
				break;
			}
		}

		if (!canvas.isDisplayable()) {
			System.err.println("[ClientLauncher] ⚠️ Canvas failed to become displayable after " + attempts + " attempts");
		} else {
			System.out.println("[ClientLauncher] ✅ Game canvas configured and displayable");
		}
	}

	public static void onGameLoadingComplete() {
		System.out.println("[ClientLauncher] ===== Game loading completed - transitioning to main interface =====");

		updateLoaderProgress(100);
		updateLoaderStatus("Ready!");

		try {
			Thread.sleep(LOADER_COMPLETION_DELAY_MS);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}

		SwingUtilities.invokeLater(() -> {
			if (loader != null) {
				loader.closeLoader();
				loader = null;
				System.out.println("[ClientLauncher] ✅ Loading visual closed");
			}
		});

		SwingUtilities.invokeLater(() -> {
			if (mainFrame != null) {
				mainFrame.setVisible(true);
				mainFrame.toFront();
				mainFrame.requestFocus();
				System.out.println("[ClientLauncher] ✅ Main game window displayed");

				setupUIDock(mainFrame);
			}
		});
	}

	private static void initializeGameSystems(GameCanvas canvas) throws InterruptedException {
		System.out.println("[ClientLauncher] Creating client instance...");
		Client client = new Client();
		Client.instance = client;

		System.out.println("[ClientLauncher] Configuring canvas rendering...");
		Client.instance.refreshFrameSize(canvas, canvas.getWidth(), canvas.getHeight());
		canvas.createBufferStrategy(EngineConfig.BUFFERS);
		GameLoader.setCanvas(canvas);

		System.out.println("[ClientLauncher] Initializing input handlers...");
		MouseManager mouseManager = new MouseManager();
		Keyboard keyboard = new Keyboard();

		Client.setCanvas(canvas);

		canvas.addKeyListener(keyboard);
		canvas.addMouseListener(mouseManager);
		canvas.addMouseMotionListener(mouseManager);
		canvas.addMouseWheelListener(mouseManager);
		canvas.addFocusListener(client);
		canvas.requestFocusInWindow();

		System.out.println("[ClientLauncher] ✅ Game systems initialized with input handling");
	}

	private static void startGameEngine(GameCanvas canvas) {
		System.out.println("[ClientLauncher] Creating game engine...");
		gameEngine = new GameEngine(canvas, Client.instance);

		gameThread = new Thread(() -> {
			try {
				System.out.println("[ClientLauncher] Game engine starting on thread: " + Thread.currentThread().getName());
				gameEngine.run();
			} catch (Exception e) {
				System.err.println("[ClientLauncher] ❌ Game engine error: " + e.getMessage());
				e.printStackTrace();
			}
		}, "GameLoop");

		gameThread.setPriority(Thread.NORM_PRIORITY + 1);
		gameThread.start();

		System.out.println("[ClientLauncher] ✅ Game engine started on thread: " + gameThread.getName());
	}

	public static void initializeGPUAfterGraphicsLoad() {
		System.out.println("[ClientLauncher] ===== Phase 5: Post-graphics GPU initialization =====");

		try {

			Thread.sleep(GPU_STABILITY_DELAY_MS);

			if (gpuContextManager == null) {
				System.out.println("[ClientLauncher] Creating GPU context manager...");
				gpuContextManager = GPUContextManager.getInstance();

				if (!gpuContextManager.initialize()) {
					System.err.println("[ClientLauncher] ❌ GPU context manager initialization failed");
					gpuInitialized = false;
					return;
				}
				System.out.println("[ClientLauncher] ✅ GPU context manager initialized");
			}

			boolean success = initializeGPURenderingWithContext();

			if (success) {
				gpuInitialized = GPUModelRenderer.isInitialized();

				if (gpuInitialized) {
					System.out.println("[ClientLauncher] ✅ GPU rendering initialized successfully");
				} else {
					System.out.println("[ClientLauncher] ⚠️ GPU model renderer not initialized");
				}
			} else {
				System.out.println("[ClientLauncher] ⚠️ GPU context initialization failed");
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

	private static boolean initializeGPURenderingWithContext() {
		final int MAX_RETRIES = 3;
		final int[] RETRY_DELAYS_MS = {200, 400, 600};
		final long[] TIMEOUT_PROGRESSION_MS = {2000, 3000, 4000};

		for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
			GPUContextManager.ContextToken contextToken = null;

			try {
				System.out.println("[ClientLauncher] GPU rendering initialization attempt " + attempt + "/" + MAX_RETRIES);

				if (gpuContextManager == null) {
					System.err.println("[ClientLauncher] GPU context manager is null on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAYS_MS[attempt - 1]);
						continue;
					}
					return false;
				}

				long timeoutMs = TIMEOUT_PROGRESSION_MS[attempt - 1];
				contextToken = gpuContextManager.acquireContext("Post-GameLoader GPU Init", timeoutMs);

				if (contextToken == null) {
					System.err.println("[ClientLauncher] Failed to acquire GPU context on attempt " + attempt +
						" (timeout: " + timeoutMs + "ms)");
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAYS_MS[attempt - 1]);
						continue;
					}
					return false;
				}

				if (!contextToken.isValid()) {
					System.err.println("[ClientLauncher] GPU context invalid on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAYS_MS[attempt - 1]);
						continue;
					}
					return false;
				}

				if (!gpuContextManager.isContextCurrent()) {
					System.err.println("[ClientLauncher] GPU context not current on attempt " + attempt);
					if (attempt < MAX_RETRIES) {
						Thread.sleep(RETRY_DELAYS_MS[attempt - 1]);
						continue;
					}
					return false;
				}

				System.out.println("[ClientLauncher] GPU context acquired successfully, initializing rendering engine...");

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

			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				System.err.println("[ClientLauncher] GPU initialization interrupted on attempt " + attempt);
				return false;
			} catch (Exception e) {
				System.err.println("[ClientLauncher] GPU rendering initialization attempt " + attempt +
					" failed: " + e.getMessage());
				if (attempt == MAX_RETRIES) {
					e.printStackTrace();
				}
			} finally {

				if (contextToken != null) {
					try {
						contextToken.close();
					} catch (Exception e) {
						System.err.println("[ClientLauncher] Error releasing context token: " + e.getMessage());
					}
				}
			}

			if (attempt < MAX_RETRIES) {
				try {
					Thread.sleep(RETRY_DELAYS_MS[attempt - 1]);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return false;
				}
			}
		}

		System.err.println("[ClientLauncher] ❌ All GPU initialization attempts failed");
		return false;
	}

	private static void setupUIDock(JFrame frame) {
		try {
			System.out.println("[ClientLauncher] Setting up UI dock...");
			uiDock = new UIDockFrame(frame);
			uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());

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

			frame.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(ComponentEvent e) {
					if (uiDock != null && frame.isVisible()) {
						Point location = frame.getLocationOnScreen();
						int x = location.x + frame.getWidth();
						int y = location.y;
						uiDock.setLocation(x, y);
					}
				}

				@Override
				public void componentResized(ComponentEvent e) {
					if (uiDock != null && frame.isVisible()) {
						uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());
						int height = frame.getHeight();
						uiDock.setSize(uiDock.getWidth(), height - 8);
						uiDock.revalidate();
						uiDock.repaint();
					}
				}
			});

			System.out.println("[ClientLauncher] ✅ UI dock configured and synchronized");

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ⚠️ Error setting up UI dock: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void setupWindowEventHandlers(JFrame frame) {
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("[ClientLauncher] Application shutdown requested by user...");
				performGracefulShutdown();
			}
		});
	}

	private static void performGracefulShutdown() {
		if (!shutdownInProgress.compareAndSet(false, true)) {
			System.out.println("[ClientLauncher] Shutdown already in progress, ignoring duplicate request");
			return;
		}

		try {
			System.out.println("[ClientLauncher] ===== Starting graceful shutdown sequence =====");

			if (gameEngine != null) {
				System.out.println("[ClientLauncher] Shutting down game engine...");
				gameEngine.shutdown();

				if (gameThread != null && gameThread.isAlive()) {
					try {
						gameThread.join(SHUTDOWN_TIMEOUT_MS);
						if (gameThread.isAlive()) {
							System.err.println("[ClientLauncher] ⚠️ Game thread did not shut down gracefully, interrupting");
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

			if (gpuInitialized) {
				System.out.println("[ClientLauncher] GPU resources cleaned up");
			}

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

			if (uiDock != null) {
				try {
					uiDock.dispose();
					System.out.println("[ClientLauncher] ✅ UI dock disposed");
				} catch (Exception e) {
					System.err.println("[ClientLauncher] ⚠️ Error disposing UI dock: " + e.getMessage());
				}
			}

			System.out.println("[ClientLauncher] ✅ Graceful shutdown completed successfully");

		} catch (Exception e) {
			System.err.println("[ClientLauncher] ❌ Error during graceful shutdown: " + e.getMessage());
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	private static void performEmergencyShutdown() {
		System.err.println("[ClientLauncher] ===== Performing emergency shutdown =====");
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

	public static String getGPUStatus() {
		if (!GPUModelRenderer.isInitialized()) {
			return "GPU: Disabled";
		}
		return "GPU: Active - Models: " + GPUModelRenderer.getModelsRendered() +
			", Tris: " + GPUModelRenderer.getTrianglesRendered();
	}

	public static boolean isGPUInitialized() {
		return gpuInitialized;
	}

	public static boolean waitForInitialization(long timeoutMs) {
		try {
			return initializationComplete.await(timeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	public static boolean isShutdownInProgress() {
		return shutdownInProgress.get();
	}
}
