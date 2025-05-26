package com.bestbudz.client.frame;

import com.bestbudz.client.ui.manager.UIPanelManager;
import com.bestbudz.client.ui.panel.*;
import com.bestbudz.client.ui.panel.UIPanel;

import com.bestbudz.engine.Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
	private LoginOverlayPanel loginOverlay;
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

		// Initialize panelManager first
		panelManager = new UIPanelManager();

		setupMainLayeredPane();
		setupPanels();

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
		loginOverlay = new LoginOverlayPanel(client);
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

	// DELEGATE EVERYTHING ELSE
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
	public LoginOverlayPanel getLoginOverlay() {
		return loginOverlay;
	}

	/**
	 * Clean up resources when closing
	 */
	@Override
	public void dispose() {
		if (loginOverlay != null) {
			loginOverlay.dispose();
		}
		super.dispose();
	}
}