package com.bestbudz.dock.ui.panel;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Universal Template Panel for creating new consistent panel layouts.
 *
 * USAGE:
 * 1. Copy this template and rename the class
 * 2. Update getPanelID() to return your panel's unique name
 * 3. Add your content in initializeComponents() and setupLayout()
 * 4. Use ButtonHandler.createButton() or addClickableButton() for interactions
 * 5. Register in UIDockFrame: registerPanel(new YourPanel());
 * 6. If using RSInterfaces, add to DockPanelMapping:
 *    YOUR_PANEL(interfaceId1, interfaceId2, "Your Panel Name", YourPanel.class);
 *
 * BUTTON CREATION:
 * - Create any component (JButton, JLabel, JPanel, etc.)
 * - Call makeClickable(component, interfaceId) to add interface functionality
 * - Call addRainbowHover(component) to add animated hover effects
 *
 * STYLING: (Below colors are defined in com.bestbudz.engine.config.ColorConfig.java)
 *  CONFIG_NAME = COLOR
 * -----------------------------------------
 * 	BG_COLOR = new Color(15, 25, 20);
 * 	PANEL_COLOR = new Color(25, 40, 35, 220);
 * 	BORDER_COLOR2 = new Color(120, 140, 100);
 * 	ACCENT_COLOR = new Color(80, 180, 120);
 * 	GOLD_ACCENT_COLOR = new Color(220, 180, 80);
 * 	BLUE_ACCENT_COLOR = new Color(80, 140, 200); (Notification texts)
 * 	TEXT = new Color(240, 250, 245);
 * 	TEXT_DIM = new Color(180, 200, 185);
 * 	TEXT_BLACK = new Color(0, 0, 0);
 * 	INPUT_BG_COLOR = new Color(35, 50, 40);
 * 	INPUT_HOVER_COLOR = new Color(45, 65, 50);
 * 	HOVER_COLOR = new Color(45, 70, 55);
 * 	SELECTED_COLOR = new Color(60, 120, 80, 220);
 *
 * FEATURES:
 * - Automatic scrolling when content exceeds height
 * - Consistent styling with other panels
 * - Standard lifecycle methods (onActivate/onDeactivate)
 * - DockTextUpdatable support for server data updates
 * - Proper layout management and sizing
 * - Integrated ButtonHandler for interface interactions
 */
public class TemplatePanel extends JPanel implements UIPanel, DockTextUpdatable {

	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel titleLabel;

	public TemplatePanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		initializeComponents();
		setupLayout();
	}

	private void initializeComponents() {
		// Create title (optional - remove if not needed)
		titleLabel = new JLabel("Template Panel");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
		titleLabel.setForeground(new Color(0xFF9933)); // Standard title color
		titleLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

		// Create main content panel
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);

		// Create scroll pane with vertical scrolling
		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);

		// Smooth scrolling
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setBlockIncrement(64);

		// Add sample content (replace with your actual content)
		addSampleContent();
	}

	private void setupLayout() {
		// Add title to top (remove if not needed)
		add(titleLabel, BorderLayout.NORTH);

		// Add scrollable content to center
		add(scrollPane, BorderLayout.CENTER);

		// Optional: Add buttons or controls to bottom
		add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Sample content - replace with your actual panel content
	 * Shows how to make components clickable and add rainbow effects
	 */
	private void addSampleContent() {
		// Regular labels
		for (int i = 1; i <= 5; i++) {
			JLabel sampleLabel = new JLabel("Sample Item " + i);
			sampleLabel.setForeground(Color.WHITE);
			sampleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			sampleLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
			sampleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(sampleLabel);
		}

		// Clickable button with rainbow hover
		JButton button = new JButton("Action Button");
		makeClickableWithRainbow(button, 29001);
		addContentItem(button);

		// Clickable label with rainbow hover
		JLabel clickableLabel = new JLabel("Click me!");
		clickableLabel.setForeground(Color.CYAN);
		clickableLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		makeClickableWithRainbow(clickableLabel, 29002);
		addContentItem(clickableLabel);

		// Just rainbow hover (no click action)
		JLabel rainbowLabel = new JLabel("Hover for rainbow effect");
		rainbowLabel.setForeground(Color.WHITE);
		rainbowLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		addRainbowHover(rainbowLabel);
		addContentItem(rainbowLabel);
	}

	/**
	 * Make any component clickable to send interface packets
	 * Works with JButton, JLabel, JPanel, or any JComponent
	 * @param component The component to make clickable
	 * @param interfaceId Interface ID to send when clicked
	 */
	public void makeClickable(JComponent component, int interfaceId) {
		if (component instanceof JButton) {
			// For buttons, use ActionListener
			((JButton) component).addActionListener(ButtonHandler.createButtonListener(interfaceId));
		} else {
			// For other components, use MouseListener and add hand cursor
			component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			component.addMouseListener(ButtonHandler.createClickListener(interfaceId));
		}
	}

	/**
	 * Add rainbow hover effect to any component
	 * @param component The component to add rainbow hover to
	 */
	public void addRainbowHover(JComponent component) {
		if (component instanceof JButton) {
			RainbowHoverUtil.applyRainbowHover((JButton) component);
		} else if (component instanceof JPanel) {
			Color originalBg = component.getBackground();
			Color hoverBg = originalBg != null ? originalBg.brighter() : Color.DARK_GRAY;
			RainbowHoverUtil.applyRainbowHoverToPanel((JPanel) component, originalBg, hoverBg);
		} else {
			Color originalBg = component.getBackground();
			if (originalBg == null) originalBg = Color.DARK_GRAY;
			RainbowHoverUtil.applyRainbowHover(component, originalBg);
		}
	}

	/**
	 * Make component clickable AND add rainbow hover in one call
	 * @param component The component to enhance
	 * @param interfaceId Interface ID to send when clicked
	 */
	public void makeClickableWithRainbow(JComponent component, int interfaceId) {
		makeClickable(component, interfaceId);
		addRainbowHover(component);
	}

	/**
	 * Example button panel with interface actions and rainbow effects
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setOpaque(false);

		JButton refreshButton = new JButton("Refresh");
		makeClickableWithRainbow(refreshButton, 29010);

		JButton actionButton = new JButton("Action");
		makeClickableWithRainbow(actionButton, 29011);

		JButton settingsButton = new JButton("Settings");
		makeClickableWithRainbow(settingsButton, 29012);

		buttonPanel.add(refreshButton);
		buttonPanel.add(actionButton);
		buttonPanel.add(settingsButton);

		return buttonPanel;
	}

	/**
	 * Create a row of action buttons with optional rainbow effects
	 * @param buttonData Array of [text, interfaceId] pairs
	 * @param useRainbow Whether to add rainbow hover effects
	 */
	public JPanel createActionButtonRow(Object[][] buttonData, boolean useRainbow) {
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttonRow.setOpaque(false);

		for (Object[] data : buttonData) {
			String text = (String) data[0];
			int interfaceId = (Integer) data[1];

			JButton button = new JButton(text);
			if (useRainbow) {
				makeClickableWithRainbow(button, interfaceId);
			} else {
				makeClickable(button, interfaceId);
			}
			buttonRow.add(button);
		}

		return buttonRow;
	}

	/**
	 * Get the content panel where new elements should be added
	 */
	public JPanel getContentPanel() {
		return contentPanel;
	}

	/**
	 * Scroll to the bottom (useful for new content)
	 */
	public void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
			verticalBar.setValue(verticalBar.getMaximum());
		});
	}

	/**
	 * Scroll to the top
	 */
	public void scrollToTop() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
			verticalBar.setValue(0);
		});
	}

	/**
	 * Add a new item to the content panel
	 */
	public void addContentItem(Component component) {
		contentPanel.add(component);
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Clear all content from the panel
	 */
	public void clearContent() {
		contentPanel.removeAll();
		contentPanel.revalidate();
		contentPanel.repaint();
	}

	/**
	 * Update the title text
	 */
	public void setTitle(String title) {
		if (titleLabel != null) {
			titleLabel.setText(title);
		}
	}

	@Override
	public void updateText() {
		// Refresh the panel when text updates are received
		repaint();
	}

	@Override
	public String getPanelID() {
		// CHANGE THIS: Return your panel's unique identifier
		return "Template";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn) return;

		// Panel activation logic
		// Example: Send activation packet
		ButtonHandler.sendClick(29000); // Example activation interface ID
		System.out.println(getPanelID() + " panel activated");
	}

	@Override
	public void onDeactivate() {
		// Panel deactivation logic
		// Example: Stop timers, cleanup resources
		System.out.println(getPanelID() + " panel deactivated");
	}

	@Override
	public void updateDockText(int index, String text) {
		if (!Client.loggedIn) return;

		// Handle server text updates
		// Example: Update specific labels or content based on index
		System.out.println("Received dock text update [" + index + "]: " + text);

		// You could update specific UI elements based on the index
		// For example, if this is a dynamic list that gets updated from server
	}

	/**
	 * Optional: Return icon path for the panel tab
	 * Uncomment and implement if your panel needs a custom icon
	 */
	// @Override
	// public String getPanelIconPath() {
	//     return "sprites/your-panel-icon.png";
	// }

	/**
	 * Optional: Return blocked interface IDs
	 * Uncomment and implement if your panel blocks certain RSInterfaces
	 */
	// @Override
	// public int[] getBlockedInterfaces() {
	//     return new int[] { 29000, 29001, 29002 }; // Example blocked interface IDs
	// }
}