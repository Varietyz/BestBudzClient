package com.bestbudz.dock.ui.panel.example;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.dock.util.InteractiveButtonUtil;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 *
 * FOLDER STRUCTURE EXAMPLE:
 * For a panel called "Inventory", create:
 *    com/bestbudz/dock/ui/panel/inventory/
 *    ├── InventoryPanel.java        (extends this template)
 *    ├── InventoryUtils.java        (item management, sorting, filtering)
 *    ├── InventoryConfig.java       (slot counts, item IDs, interface IDs)
 *    └── InventoryStyle.java        (item slot styling, grid layouts)
 *
 * For a panel called "Skills", create:
 *    com/bestbudz/dock/ui/panel/skills/
 *    ├── SkillsPanel.java           (extends this template)
 *    ├── SkillsUtils.java           (XP calculations, level formulas)
 *    ├── SkillsConfig.java          (skill IDs, XP tables, level caps)
 *    └── SkillsStyle.java           (progress bars, skill icons)
 *
 * ORGANIZATION BENEFITS:
 * - Easy to find panel-specific code
 * - Clean separation of concerns
 * - Reusable utility methods
 * - Centralized configuration
 * - Consistent styling patterns
 * - Simple to extend or modify individual panels
 * Universal Template Panel for creating new consistent panel layouts.
 *
 * PANEL CREATION WORKFLOW:
 * 1. Copy this template and rename the class (e.g., ExamplePanel -> SomeNamedPanel)
 * 2. Create organized folder structure for your panel:
 *
 *    com/bestbudz/dock/ui/panel/example/
 *    ├── ExamplePanel.java          (Main panel class - copy from this template)
 *    ├── ExampleUtils.java          (Panel-specific utility methods)
 *    ├── ExampleConfig.java         (Panel configuration and constants)
 *    └── ExampleStyle.java          (Panel-specific styling and themes)
 *
 * 3. Update getPanelID() to return your panel's unique name (e.g., "Example")
 * 4. Configure ExampleConfig.java with your panel's constants and settings
 * 5. Implement ExampleStyle.java for any custom styling beyond the base styles
 * 6. Add your business logic and utility methods in ExampleUtils.java
 * 7. Add your content in initializeComponents() and setupLayout()
 * 8. Use InteractiveButtonUtil methods for consistent button styling with rainbow hover
 * 9. Register in UIDockFrame: registerPanel(new ExamplePanel());
 * 10. If using RSInterfaces, add to DockPanelMapping:
 *     EXAMPLE_PANEL(interfaceId1, interfaceId2, "Example Panel", ExamplePanel.class);
 *
 * BUTTON CREATION (NEW IMPROVED METHODS):
 * - InteractiveButtonUtil.addInteractiveButton("Text", buttonId) - Text button with rainbow hover
 * - InteractiveButtonUtil.addSpriteButton("sprites/icon.png", buttonId) - Icon button with rainbow hover
 * - InteractiveButtonUtil.addInteractiveButton("Text", "sprites/icon.png", buttonId) - Text + Icon button
 * - makeClickable(component, interfaceId) - Add click functionality to any component
 * - addRainbowHover(component) - Add rainbow hover to any component
 * - makeClickableWithRainbow(component, interfaceId) - Both click and rainbow hover
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
 * - Integrated InteractiveButtonUtil for consistent button styling and rainbow effects
 */
public class ExamplePanel extends JPanel implements UIPanel, DockTextUpdatable {

	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel titleLabel;

	public ExamplePanel() {
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
	 * Shows how to use InteractiveButtonUtil and rainbow effects
	 */
	private void addSampleContent() {
		// Regular labels
		for (int i = 1; i <= 3; i++) {
			JLabel sampleLabel = new JLabel("Sample Item " + i);
			sampleLabel.setForeground(Color.WHITE);
			sampleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			sampleLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
			sampleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(sampleLabel);
		}

		// Add spacing
		contentPanel.add(Box.createVerticalStrut(10));

		// Demonstrate InteractiveButtonUtil methods
		JLabel demoLabel = new JLabel("InteractiveButtonUtil Examples:");
		demoLabel.setForeground(new Color(0xFF9933));
		demoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		addContentItem(demoLabel);

		// Text-only button with automatic rainbow hover
		JButton textButton = InteractiveButtonUtil.addInteractiveButton("Text Button", 29001);
		addContentItem(textButton);

		// Sprite-only button (uncomment when you have sprites)
		// JButton spriteButton = InteractiveButtonUtil.addSpriteButton("sprites/skills/assault.png", 29002);
		// addContentItem(spriteButton);

		// Text + Sprite button (uncomment when you have sprites)
		// JButton comboButton = InteractiveButtonUtil.addInteractiveButton("Combo", "sprites/skills/combo.png", 29003);
		// addContentItem(comboButton);

		// Custom styled button
		JButton customButton = InteractiveButtonUtil.addCustomButton("Custom Button", 29004, button -> {
			button.setFont(new Font("SansSerif", Font.BOLD, 12));
			button.setForeground(Color.YELLOW);
			button.setBackground(Color.DARK_GRAY);
			button.setBorder(BorderFactory.createRaisedBevelBorder());
		}, true); // true enables rainbow hover
		addContentItem(customButton);

		// Add spacing
		contentPanel.add(Box.createVerticalStrut(10));

		// Legacy methods still work for custom components
		JLabel legacyLabel = new JLabel("Legacy Methods Still Available:");
		legacyLabel.setForeground(new Color(0xFF9933));
		legacyLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
		addContentItem(legacyLabel);

		// Clickable label with rainbow hover (using legacy method)
		JLabel clickableLabel = new JLabel("Click me! (Legacy method)");
		clickableLabel.setForeground(Color.CYAN);
		clickableLabel.setBorder(new EmptyBorder(4, 4, 4, 4));
		makeClickableWithRainbow(clickableLabel, 29005);
		addContentItem(clickableLabel);

		// Just rainbow hover (no click action) using legacy method
		JLabel rainbowLabel = new JLabel("Hover for rainbow effect (Legacy)");
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
	 * Example button panel using InteractiveButtonUtil for consistent styling
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setOpaque(false);

		// Use InteractiveButtonUtil for consistent styling and automatic rainbow hover
		JButton refreshButton = InteractiveButtonUtil.addInteractiveButton("Refresh", 29010);
		JButton actionButton = InteractiveButtonUtil.addInteractiveButton("Action", 29011);
		JButton settingsButton = InteractiveButtonUtil.addInteractiveButton("Settings", 29012);

		buttonPanel.add(refreshButton);
		buttonPanel.add(actionButton);
		buttonPanel.add(settingsButton);

		return buttonPanel;
	}

	/**
	 * Create a row of action buttons using InteractiveButtonUtil
	 * @param buttonData Array of [text, interfaceId] pairs
	 * @param useSprites Whether to include sprite paths (buttonData should have 3 elements: [text, spritePath, interfaceId])
	 */
	public JPanel createInteractiveButtonRow(Object[][] buttonData, boolean useSprites) {
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttonRow.setOpaque(false);

		for (Object[] data : buttonData) {
			JButton button;

			if (useSprites && data.length >= 3) {
				String text = (String) data[0];
				String spritePath = (String) data[1];
				int interfaceId = (Integer) data[2];
				button = InteractiveButtonUtil.addInteractiveButton(text, spritePath, interfaceId);
			} else {
				String text = (String) data[0];
				int interfaceId = (Integer) data[1];
				button = InteractiveButtonUtil.addInteractiveButton(text, interfaceId);
			}

			buttonRow.add(button);
		}

		return buttonRow;
	}

	/**
	 * Create a row of sprite-only buttons using InteractiveButtonUtil
	 * @param spriteData Array of [spritePath, interfaceId] pairs
	 */
	public JPanel createSpriteButtonRow(Object[][] spriteData) {
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttonRow.setOpaque(false);

		for (Object[] data : spriteData) {
			String spritePath = (String) data[0];
			int interfaceId = (Integer) data[1];

			JButton button = InteractiveButtonUtil.addSpriteButton(spritePath, interfaceId);
			buttonRow.add(button);
		}

		return buttonRow;
	}

	/**
	 * Create a row of action buttons with legacy styling (for backwards compatibility)
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
	 * Create a grid of buttons using InteractiveButtonUtil
	 * @param buttonData 2D array of button data
	 * @param cols Number of columns in the grid
	 */
	public JPanel createInteractiveButtonGrid(Object[][] buttonData, int cols) {
		JPanel gridPanel = new JPanel(new GridLayout(0, cols, 5, 5));
		gridPanel.setOpaque(false);

		for (Object[] data : buttonData) {
			String text = (String) data[0];
			int interfaceId = (Integer) data[1];

			JButton button = InteractiveButtonUtil.addInteractiveButton(text, interfaceId);
			gridPanel.add(button);
		}

		return gridPanel;
	}

	/**
	 * Apply rainbow hover to existing button (utility method)
	 * @param button The button to enhance
	 */
	public void applyRainbowToButton(JButton button) {
		InteractiveButtonUtil.applyRainbowHoverToButton(button);
	}

	/**
	 * Remove rainbow hover from existing button (utility method)
	 * @param button The button to modify
	 */
	public void removeRainbowFromButton(JButton button) {
		InteractiveButtonUtil.removeRainbowHoverFromButton(button);
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