package com.bestbudz.dock.frame;

import com.bestbudz.dock.ui.manager.UIPanelManager;
import com.bestbudz.dock.ui.panel.bubblebudz.ui.BubbleBudzPanel;
import com.bestbudz.dock.config.RegisteredPanels;
import com.bestbudz.dock.util.UIPanel;

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
	private String topVisiblePanelID;
	private String bottomVisiblePanelID;

	public static final Map<String, String> panelPositions = new LinkedHashMap<>();
	private JSplitPane splitPane;

	private final UIPanelManager panelManager;

	public static TogglePreview.ScrollPaneWithTabs scrollTopWithTabs;
	public static TogglePreview.ScrollPaneWithTabs scrollBottomWithTabs;

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
	private JLayeredPane mainLayeredPane;
	private JPanel contentPanel;

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
	}

	/**
	 * Setup panels using centralized configuration
	 */
	private void setupPanels() {
		// Register all panels from the centralized configuration
		UIPanel[] panels = RegisteredPanels.createAllPanels();

		System.out.println("Registering " + panels.length + " panels from configuration...");

		for (UIPanel panel : panels) {
			registerPanel(panel);
			System.out.println("Registered panel: " + panel.getPanelID());
		}

		// Load default layout using the centralized configuration
		loadDockPanelLayout();

		System.out.println("Panel registration and layout setup complete.");
	}

	/**
	 * Setup keyboard shortcuts for the dock frame including modal shortcuts
	 */
	private void setupKeyboardShortcuts() {
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getRootPane().getActionMap();

	}


	/**
	 * Sets up the main layered pane that contains everything including toggle bars and login overlay
	 */
	private void setupMainLayeredPane() {
		mainLayeredPane = new JLayeredPane();
		mainLayeredPane.setPreferredSize(new Dimension(300, 750));

		contentPanel = new JPanel(new BorderLayout());
		contentPanel.setOpaque(true);

		setupToggleBars();
		setupMainContent();

		mainLayeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);

		loginOverlay = new BubbleBudzPanel(client);
		mainLayeredPane.add(loginOverlay, JLayeredPane.PALETTE_LAYER);

		mainLayeredPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateMainLayeredPaneBounds();
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				SwingUtilities.invokeLater(() -> updateMainLayeredPaneBounds());
			}
		});

		add(mainLayeredPane, BorderLayout.CENTER);
		SwingUtilities.invokeLater(() -> updateMainLayeredPaneBounds());
	}

	private void setupToggleBars() {
		toggleBarTop.setBorder(BorderFactory.createEmptyBorder());
		toggleBarBottom.setBorder(BorderFactory.createEmptyBorder());

		scrollTop.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollTop.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollTop.setBorder(BorderFactory.createEmptyBorder());
		scrollTop.setViewportBorder(null);

		scrollBottom.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollBottom.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollBottom.setBorder(BorderFactory.createEmptyBorder());
		scrollBottom.setViewportBorder(null);

		// Create enhanced scroll panes with side tabs and store references
		scrollTopWithTabs = new TogglePreview.ScrollPaneWithTabs(scrollTop, toggleBarTop);
		scrollBottomWithTabs = new TogglePreview.ScrollPaneWithTabs(scrollBottom, toggleBarBottom);

		// Create the basic wrapper first
		JPanel basicToggleWrapper = new JPanel(new GridLayout(2, 1, 0, 2));
		basicToggleWrapper.setBorder(BorderFactory.createEmptyBorder());
		basicToggleWrapper.add(scrollTopWithTabs);
		basicToggleWrapper.add(scrollBottomWithTabs);

		// NOW add the single hover tab wrapper
		java.util.List<JScrollPane> scrollPanes = java.util.Arrays.asList(scrollTop, scrollBottom);
		java.util.List<JPanel> toggleBars = java.util.Arrays.asList(toggleBarTop, toggleBarBottom);
		toggleWrapper = new TogglePreview.ToggleBarWithSinglePreview(basicToggleWrapper, scrollPanes, toggleBars);

		contentPanel.add(toggleWrapper, BorderLayout.NORTH);

		// DON'T ADD ANY SCROLL LISTENERS HERE - LET UIDockHelper.setupLoopingScrollBars() HANDLE IT!
	}

	private void setupMainContent() {
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topStack, bottomStack);
		splitPane.setResizeWeight(0.4);
		contentPanel.add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * Updates the bounds of all components in the main layered pane
	 */
	private void updateMainLayeredPaneBounds() {
		if (mainLayeredPane != null && contentPanel != null && loginOverlay != null) {
			Dimension size = mainLayeredPane.getSize();
			if (size.width > 0 && size.height > 0) {
				contentPanel.setBounds(0, 0, size.width, size.height);
				loginOverlay.setBounds(0, 0, size.width, size.height);
				contentPanel.revalidate();
				splitPane.revalidate();
			}
		}
	}

	// DELEGATE PANEL METHODS (no more automatic saving)

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

	// ACCESSORS

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

	public void setTopVisiblePanelID(String id) { topVisiblePanelID = id; }
	public void setBottomVisiblePanelID(String id) { bottomVisiblePanelID = id; }
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

		if (loginOverlay != null) {
			loginOverlay.dispose();
		}

		super.dispose();
	}
}