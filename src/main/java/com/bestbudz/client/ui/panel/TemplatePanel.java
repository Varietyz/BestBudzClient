package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.engine.Client;

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
 * 4. Register in UIDockFrame: registerPanel(new YourPanel());
 * 5. If using RSInterfaces, add to DockPanelMapping:
 *    YOUR_PANEL(interfaceId1, interfaceId2, "Your Panel Name", YourPanel.class);
 *
 * FEATURES:
 * - Automatic scrolling when content exceeds height
 * - Consistent styling with other panels
 * - Standard lifecycle methods (onActivate/onDeactivate)
 * - DockTextUpdatable support for server data updates
 * - Proper layout management and sizing
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
		// add(createButtonPanel(), BorderLayout.SOUTH);
	}

	/**
	 * Sample content - replace with your actual panel content
	 */
	private void addSampleContent() {
		// Example: Add some labels
		for (int i = 1; i <= 10; i++) {
			JLabel sampleLabel = new JLabel("Sample Item " + i);
			sampleLabel.setForeground(Color.WHITE);
			sampleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			sampleLabel.setBorder(new EmptyBorder(2, 4, 2, 4));
			sampleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(sampleLabel);
		}
	}

	/**
	 * Example button panel creation (uncomment in setupLayout() if needed)
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setOpaque(false);

		JButton exampleButton = new JButton("Example Action");
		exampleButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
		exampleButton.addActionListener(e -> handleButtonClick());

		buttonPanel.add(exampleButton);
		return buttonPanel;
	}

	/**
	 * Example button click handler
	 */
	private void handleButtonClick() {
		if (!Client.loggedIn) return;

		// Example: Send packet to server
		try {
			// Client.stream.createFrame(185);
			// Client.stream.writeWord(buttonId);
			System.out.println("Button clicked in " + getPanelID());
		} catch (Exception ex) {
			System.err.println("Error in button click: " + ex.getMessage());
		}
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
		// Example: Refresh data, send packets, start timers
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
	//     return new int[] { interfaceId1, interfaceId2 };
	// }
}