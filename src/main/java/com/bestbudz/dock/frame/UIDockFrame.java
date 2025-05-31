package com.bestbudz.dock.frame;

import com.bestbudz.dock.ui.manager.UIPanelManager;
import com.bestbudz.dock.ui.manager.UIModalManager;
import com.bestbudz.dock.ui.panel.client.BubbleBudzPanel;
import com.bestbudz.dock.ui.panel.client.SettingsPanel;
import com.bestbudz.dock.ui.panel.game.AchievementsPanel;
import com.bestbudz.dock.ui.panel.game.QuestTabPanel;
import com.bestbudz.dock.ui.panel.game.SkillsPanel;
import com.bestbudz.dock.ui.panel.social.StonersPanel;
import com.bestbudz.dock.util.UIPanel;

import com.bestbudz.dock.ui.panel.social.ChatPanel;
import com.bestbudz.engine.core.Client;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class UIDockFrame extends JDialog {

	private Client client;
	private String lastActivePanelID;
	private String topVisiblePanelID;
	private String bottomVisiblePanelID;

	public static final Map<String, String> panelPositions = new LinkedHashMap<>();
	private JSplitPane splitPane;

	private final UIPanelManager panelManager;
	private final UIModalManager modalManager; // New modal manager

	public static final JPanel toggleBarTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
	public static final JPanel toggleBarBottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
	public static final JScrollPane scrollTop = new JScrollPane(toggleBarTop);
	public static final JScrollPane scrollBottom = new JScrollPane(toggleBarBottom);
	private JPanel toggleWrapper;

	private final CardLayout topLayout = new CardLayout();
	private final CardLayout bottomLayout = new CardLayout();

	private final JPanel topStack = new JPanel(topLayout);
	private final JPanel bottomStack = new JPanel(bottomLayout);

	private final Map<String, Component> panelMap = new LinkedHashMap<>();
	public static final Map<String, JToggleButton> toggleButtons = new LinkedHashMap<>();

	// Login overlay components
	private BubbleBudzPanel loginOverlay;
	private JLayeredPane mainLayeredPane; // This will contain everything including toggle bars
	private JPanel contentPanel; // Container for toggle bars and split pane

	private static UIDockFrame instance;

	public static UIDockFrame getInstance() {
		return instance;
	}

	public UIDockFrame(JFrame owner) {
		super(owner, false);
		instance = this;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(300, 750);
		setLayout(new BorderLayout());
		setResizable(true);
		setUndecorated(true);

		// Initialize managers first
		panelManager = new UIPanelManager();

		// Initialize modal manager with this frame as parent and the client
		modalManager = new UIModalManager(this, Client.instance);

		setupMainLayeredPane();
		setupPanels();
		setupKeyboardShortcuts();

		setVisible(true);
		UIDockHelper.updateToggleInteractivity();

		// Start login state monitoring
		if (!Client.loggedIn) {
			new Timer(2000, e -> UIDockHelper.updateToggleInteractivity()).start();
		}

		// Initialize login overlay state
		if (loginOverlay != null) {
			loginOverlay.refreshLoginState();
		}

		// Configure modal manager based on user preferences
		configureModalManager();
	}

	/**
	 * Configure the modal manager with default settings
	 */
	private void configureModalManager() {
		// Enable debug mode if in development
		boolean debugMode = Boolean.getBoolean("dock.debug") || System.getProperty("env", "prod").equals("dev");
		modalManager.setDebugMode(debugMode);

		// Modal dialogues enabled by default, but can be configured
		boolean useModals = !Boolean.getBoolean("dock.disableModals");
		modalManager.setUseModalDialogues(useModals);

		if (debugMode) {
			System.out.println("Modal manager configured - Debug: " + debugMode + ", Modals: " + useModals);
		}
	}

	/**
	 * Setup keyboard shortcuts for the dock frame including modal shortcuts
	 */
	private void setupKeyboardShortcuts() {
		// Get the root pane's input map for global shortcuts
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

		// ESC key - Close all modals or minimize dock
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeModalsOrMinimize");
		actionMap.put("closeModalsOrMinimize", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (modalManager.hasVisibleModals()) {
					modalManager.closeAllModals();
				} else {
					setVisible(false);
				}
			}
		});

		// F12 - Toggle modal debug mode
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "toggleModalDebug");
		actionMap.put("toggleModalDebug", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean currentDebug = modalManager.isDebugMode();
				modalManager.setDebugMode(!currentDebug);
				showTemporaryMessage("Modal Debug: " + (!currentDebug ? "ON" : "OFF"));
			}
		});

		// Ctrl+M - Toggle modal dialogues on/off
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK), "toggleModalDialogues");
		actionMap.put("toggleModalDialogues", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean currentUse = modalManager.isUsingModalDialogues();
				modalManager.setUseModalDialogues(!currentUse);
				showTemporaryMessage("Modal Dialogues: " + (!currentUse ? "ON" : "OFF"));
			}
		});

		// Ctrl+Shift+T - Show test dialogue (development only)
		if (modalManager.isDebugMode()) {
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK | KeyEvent.SHIFT_DOWN_MASK), "testDialogue");
			actionMap.put("testDialogue", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showTestDialogue();
				}
			});
		}
	}

	/**
	 * Show a temporary message in the UI (could be implemented as a toast or status update)
	 */
	private void showTemporaryMessage(String message) {
		// For now, just print to console, but this could be a toast notification
		System.out.println("UI Message: " + message);

		// Optionally show a temporary modal
		modalManager.showConfirmation("Information", message, null, null);
	}

	/**
	 * Show test dialogue for development purposes
	 */
	private void showTestDialogue() {
		if (!modalManager.isDebugMode()) return;

		// Show a test confirmation
		modalManager.showConfirmation(
			"Test Dialogue",
			"This is a test dialogue. Modal system is working correctly!",
			() -> System.out.println("Test dialogue confirmed"),
			() -> System.out.println("Test dialogue cancelled")
		);
	}

	/**
	 * Sets up the main layered pane that contains everything including toggle bars and login overlay
	 */
	private void setupMainLayeredPane() {
		// Create the main layered pane that will hold everything
		mainLayeredPane = new JLayeredPane();
		mainLayeredPane.setPreferredSize(new Dimension(300, 750));

		// Create content panel that holds toggle bars and split pane
		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(true);

		// Setup toggle bars in the content panel
		setupToggleBars();

		// Setup main content (split pane) in the content panel
		setupMainContent();

		// Add content panel to the default layer
		mainLayeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

		// Create and add login overlay to top layer (spans entire frame)
		loginOverlay = new BubbleBudzPanel(client);
		mainLayeredPane.add(loginOverlay, JLayeredPane.PALETTE_LAYER);

		// Handle resizing for all components
		mainLayeredPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateMainLayeredPaneBounds();
			}
		});

		// Add frame resize listener
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(() -> updateMainLayeredPaneBounds());
			}
		});

		// Add the main layered pane to the frame
		add(mainLayeredPane, BorderLayout.CENTER);

		// Initial bounds setup
		SwingUtilities.invokeLater(() -> updateMainLayeredPaneBounds());
	}

	private void setupToggleBars() {
		toggleBarTop.setBorder(BorderFactory.createEmptyBorder());
		toggleBarBottom.setBorder(BorderFactory.createEmptyBorder());

		scrollTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollTop.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollTop.setBorder(BorderFactory.createEmptyBorder());
		scrollTop.setViewportBorder(null);

		scrollBottom.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollBottom.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollBottom.setBorder(BorderFactory.createEmptyBorder());
		scrollBottom.setViewportBorder(null);

		toggleWrapper = new JPanel(new GridLayout(2, 1, 0, 0));
		toggleWrapper.setBorder(BorderFactory.createEmptyBorder());
		toggleWrapper.add(scrollTop);
		toggleWrapper.add(scrollBottom);

		// Add toggle wrapper to content panel (not directly to frame)
		contentPanel.add(toggleWrapper, BorderLayout.NORTH);

		// Sync scroll bars
		scrollTop.getHorizontalScrollBar().addAdjustmentListener(e -> {
			JScrollBar bottomScroll = scrollBottom.getHorizontalScrollBar();
			int max = bottomScroll.getMaximum() - bottomScroll.getVisibleAmount();
			int mirrored = max - e.getValue();
			bottomScroll.setValue(Math.max(0, mirrored));
		});

		scrollTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBottom.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}

	private void setupMainContent() {
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topStack, bottomStack);
		splitPane.setResizeWeight(0.4);

		// Add split pane to content panel (not directly to frame)
		contentPanel.add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * Updates the bounds of all components in the main layered pane
	 */
	private void updateMainLayeredPaneBounds() {
		if (mainLayeredPane != null && contentPanel != null && loginOverlay != null) {
			Dimension size = mainLayeredPane.getSize();
			if (size.width > 0 && size.height > 0) {
				// Content panel fills the entire layered pane
				contentPanel.setBounds(0, 0, size.width, size.height);

				// Login overlay spans the entire layered pane (including toggle bars)
				loginOverlay.setBounds(0, 0, size.width, size.height);

				// Ensure proper validation
				contentPanel.revalidate();
				splitPane.revalidate();
			}
		}
	}

	private void setupPanels() {
		registerPanel(new SettingsPanel());
		registerPanel(new QuestTabPanel());
		registerPanel(new AchievementsPanel());
		registerPanel(new ChatPanel());
		registerPanel(new StonersPanel());
		registerPanel(new SkillsPanel());

		loadDockPanelLayout();
	}

	// MODAL INTEGRATION METHODS

	/**
	 * Show dialogue modal for game interface - Main integration point
	 */
	public void showDialogue(int interfaceId, Object rsInterface) {
		if (modalManager != null) {
			modalManager.showDialogue(interfaceId, rsInterface);
		}
	}

	/**
	 * Show input prompt modal
	 */
	public void showInputPrompt(String inputType, String prompt) {
		if (modalManager != null) {
			modalManager.showInputPrompt(inputType, prompt);
		}
	}

	/**
	 * Handle server closing dialogue (called when opcode 219 is received)
	 */
	public void onServerCloseDialogue() {
		if (modalManager != null) {
			modalManager.onServerCloseDialogue();
		}
	}

	/**
	 * Show confirmation dialog
	 */
	public void showConfirmation(String title, String message, Runnable onConfirm, Runnable onCancel) {
		if (modalManager != null) {
			modalManager.showConfirmation(title, message, onConfirm, onCancel);
		}
	}

	/**
	 * Show progress dialog
	 */
	public void showProgress(String title, String message, boolean indeterminate) {
		if (modalManager != null) {
			modalManager.showProgress(title, message, indeterminate);
		}
	}

	/**
	 * Check if dialogue modal is currently active
	 */
	public boolean isDialogueActive() {
		return modalManager != null && modalManager.isDialogueActive();
	}

	/**
	 * Get current dialogue interface ID
	 */
	public int getCurrentDialogueInterfaceId() {
		return modalManager != null ? modalManager.getCurrentDialogueInterfaceId() : -1;
	}

	/**
	 * Check if any modals are visible
	 */
	public boolean hasVisibleModals() {
		return modalManager != null && modalManager.hasVisibleModals();
	}

	/**
	 * Close all modals
	 */
	public void closeAllModals() {
		if (modalManager != null) {
			modalManager.closeAllModals();
		}
	}

	/**
	 * Get the modal manager instance for advanced usage
	 */
	public UIModalManager getModalManager() {
		return modalManager;
	}

	/**
	 * Configure modal settings
	 */
	public void configureModals(boolean useModalDialogues, boolean debugMode) {
		if (modalManager != null) {
			modalManager.setUseModalDialogues(useModalDialogues);
			modalManager.setDebugMode(debugMode);
		}
	}

	// INTEGRATION WITH CLIENT PACKET HANDLING

	/**
	 * This method should be called from your client's packet handler when a dialogue interface is opened (opcode 164)
	 */
	public static void handleDialogueInterface(int interfaceId, Object rsInterface) {
		UIDockFrame instance = getInstance();
		if (instance != null) {
			instance.showDialogue(interfaceId, rsInterface);
		}
	}

	/**
	 * This method should be called from your client's packet handler when interfaces are closed (opcode 219)
	 */
	public static void handleCloseInterface() {
		UIDockFrame instance = getInstance();
		if (instance != null) {
			instance.onServerCloseDialogue();
		}
	}

	/**
	 * This method should be called when an input prompt is needed (e.g., "Enter amount")
	 */
	public static void handleInputPrompt(String inputType, String prompt) {
		UIDockFrame instance = getInstance();
		if (instance != null) {
			instance.showInputPrompt(inputType, prompt);
		}
	}

	// DELEGATE EVERYTHING ELSE (Original methods preserved)
	public void registerPanel(UIPanel uiPanel) {
		UIDockHelper.registerPanel(this, uiPanel);
	}

	public void loadDockPanelLayout() {
		UIDockHelper.loadDockPanelLayout(this);
	}

	public void showPanelTop(String id) {
		UIDockHelper.showPanelTop(this, id);
	}

	public void showPanelBottom(String id) {
		UIDockHelper.showPanelBottom(this, id);
	}

	public void saveLayoutState() {
		UIDockHelper.saveLayoutState(this);
	}

	public void updateDockPanelConfig() {
		UIDockHelper.updateDockPanelConfig(this);
	}

	public void switchPanel(JPanel stack, CardLayout layout, String id) {
		UIDockHelper.switchPanel(this, stack, layout, id);
	}

	public void updateToggles() {
		UIDockHelper.updateToggles(this);
	}

	public void panelSwitcher(String id) {
		UIDockHelper.panelSwitcher(this, id);
	}

	public UIPanel getPanel(String id) {
		return panelManager.getPanel(id);
	}

	public static String getPanelPosition(String id) {
		return panelPositions.getOrDefault(id, "top");
	}

	public void dockPanelToMatch(String sourcePanelID, String targetPanelID) {
		UIDockHelper.dockPanelToMatch(this, sourcePanelID, targetPanelID);
	}

	// ACCESSORS (internal use only)
	public JPanel getToggleBar() { return toggleBarTop; }
	public JPanel getToggleBarBottom() { return toggleBarBottom; }
	public JSplitPane getSplitPane() { return splitPane; }
	public CardLayout getTopLayout() { return topLayout; }
	public CardLayout getBottomLayout() { return bottomLayout; }
	public JPanel getTopStack() { return topStack; }
	public JPanel getBottomStack() { return bottomStack; }
	public Map<String, String> getPanelPositions() { return panelPositions; }
	public Map<String, Component> getPanelMap() { return panelMap; }
	public Map<String, JToggleButton> getToggleButtons() { return toggleButtons; }
	public UIPanelManager getPanelManager() { return panelManager; }

	public void setLastActivePanelID(String id) { lastActivePanelID = id; }
	public void setTopVisiblePanelID(String id) { topVisiblePanelID = id; }
	public void setBottomVisiblePanelID(String id) { bottomVisiblePanelID = id; }
	public String getLastActivePanelID() { return lastActivePanelID; }
	public String getTopVisiblePanelID() { return topVisiblePanelID; }
	public String getBottomVisiblePanelID() { return bottomVisiblePanelID; }

	/**
	 * Get access to the login overlay for manual refresh if needed
	 */
	public BubbleBudzPanel getLoginOverlay() {
		return loginOverlay;
	}

	/**
	 * Clean up resources when closing
	 */
	@Override
	public void dispose() {
		// Dispose modal manager first
		if (modalManager != null) {
			modalManager.dispose();
		}

		if (loginOverlay != null) {
			loginOverlay.dispose();
		}

		super.dispose();
	}
}