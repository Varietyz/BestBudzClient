// Updated AppearancePanel.java with persistence support

package com.bestbudz.dock.ui.panel.character;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Appearance Panel with Persistence Support
 *
 * Now saves and loads appearance data from .bestbudz/appearance.dock file
 * to maintain character appearance across client restarts.
 */
public class AppearancePanel extends JPanel implements UIPanel, DockTextUpdatable {

	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel statusLabel;
	private AppearanceStorage storage;

	// Current appearance state (loaded from storage or defaults)
	private byte currentGender = AppearanceConfig.Defaults.GENDER;

	// Identity kit values (loaded from storage or defaults)
	private byte currentHead = AppearanceConfig.Defaults.MALE_HEAD;
	private byte currentJaw = AppearanceConfig.Defaults.MALE_JAW;
	private byte currentTorso = AppearanceConfig.Defaults.MALE_TORSO;
	private byte currentArms = AppearanceConfig.Defaults.MALE_ARMS;
	private byte currentHands = AppearanceConfig.Defaults.MALE_HANDS;
	private byte currentLegs = AppearanceConfig.Defaults.MALE_LEGS;
	private byte currentFeet = AppearanceConfig.Defaults.MALE_FEET;

	// Color indices (loaded from storage or defaults)
	private byte currentHairColor = AppearanceConfig.Defaults.HAIR_COLOR;
	private byte currentTorsoColor = AppearanceConfig.Defaults.TORSO_COLOR;
	private byte currentLegsColor = AppearanceConfig.Defaults.LEGS_COLOR;
	private byte currentFeetColor = AppearanceConfig.Defaults.FEET_COLOR;
	private byte currentSkinColor = AppearanceConfig.Defaults.SKIN_COLOR;

	// Track if we've loaded data for current session
	private boolean appearanceLoaded = false;
	private String lastLoadedUser = "";

	public AppearancePanel() {
		setLayout(new BorderLayout());
		setBackground(MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		// Initialize storage system
		storage = new AppearanceStorage();

		initializeComponents();
	}

	private void initializeComponents() {
		// Create status label first (needed for header)
		statusLabel = new JLabel("Changes applied immediately");

		// Create main content panel
		contentPanel = AppearanceStyle.createContentPanel();
		buildContent();

		// Create scroll pane
		scrollPane = new JScrollPane(contentPanel);
		AppearanceStyle.styleScrollPane(scrollPane);

		// Setup main layout
		add(scrollPane, BorderLayout.CENTER);

		ensureAppearanceLoaded();
	}

	private void buildContent() {
		// Gender Section
		contentPanel.add(AppearanceStyle.createGenderSection(
			() -> changeGender((byte) 0),
			() -> changeGender((byte) 1)
		));
		contentPanel.add(AppearanceStyle.createVerticalSpacing(10));

		// Design Section
		contentPanel.add(AppearanceStyle.createSectionHeader(AppearanceConfig.UI.DESIGN_SECTION));
		contentPanel.add(AppearanceStyle.createVerticalSpacing(5));

		// Add part controls using config for proper indexing
		for (int i = 0; i < AppearanceConfig.UI.PART_NAMES.length; i++) {
			final int partIndex = i;
			contentPanel.add(AppearanceStyle.createPartRow(
				AppearanceConfig.UI.PART_NAMES[i],
				() -> changeAppearancePart(partIndex, -1),
				() -> changeAppearancePart(partIndex, 1)
			));
		}

		contentPanel.add(AppearanceStyle.createVerticalSpacing(10));

		// Color Section
		contentPanel.add(AppearanceStyle.createSectionHeader(AppearanceConfig.UI.COLOR_SECTION));
		contentPanel.add(AppearanceStyle.createVerticalSpacing(5));

		// Add color controls using config for proper indexing
		for (int i = 0; i < AppearanceConfig.UI.COLOR_NAMES.length; i++) {
			final int colorIndex = i;
			contentPanel.add(AppearanceStyle.createColorRow(
				AppearanceConfig.UI.COLOR_NAMES[i],
				() -> changeColor(colorIndex, -1),
				() -> changeColor(colorIndex, 1)
			));
		}

		// Add flexible space at bottom
		contentPanel.add(AppearanceStyle.createVerticalGlue());
	}

	/**
	 * Loads appearance data from storage if not already loaded for this user
	 */
	private void ensureAppearanceLoaded() {
		if (!Client.loggedIn) {
			return;
		}

		String currentUser = Client.myUsername != null ? Client.myUsername : "";
		if (currentUser.isEmpty()) {
			return;
		}

		// Check if we need to load data (new user or first load)
		if (!appearanceLoaded || !currentUser.equals(lastLoadedUser)) {
			System.out.println("=== LOADING APPEARANCE DATA ===");
			System.out.println("Current user: " + currentUser);
			System.out.println("Last loaded user: " + lastLoadedUser);
			System.out.println("Appearance loaded: " + appearanceLoaded);

			loadAppearanceFromStorage(currentUser);
			lastLoadedUser = currentUser;
			appearanceLoaded = true;

			System.out.println("=== APPEARANCE DATA LOADED ===");
			printCurrentAppearance();
		}
	}

	/**
	 * Loads appearance data from storage for the specified user
	 */
	private void loadAppearanceFromStorage(String username) {
		try {
			storage.setCurrentUser(username);
			AppearanceStorage.AppearanceData data = storage.loadAppearance();

			// Store the loaded values
			byte oldGender = currentGender;

			// Update current values from loaded data
			currentGender = data.gender;
			currentHead = data.head;
			currentJaw = data.jaw;
			currentTorso = data.torso;
			currentArms = data.arms;
			currentHands = data.hands;
			currentLegs = data.legs;
			currentFeet = data.feet;
			currentHairColor = data.hairColor;
			currentTorsoColor = data.torsoColor;
			currentLegsColor = data.legsColor;
			currentFeetColor = data.feetColor;
			currentSkinColor = data.skinColor;

			boolean hasExistingData = storage.hasAppearanceData(username);
			if (hasExistingData) {
				updateStatus("Loaded saved appearance for " + username, false);
				System.out.println("Successfully loaded saved appearance data");
			} else {
				updateStatus("Using default appearance for new character", false);
				System.out.println("No saved data found, using defaults");
			}

			// Log the change
			if (oldGender != currentGender) {
				System.out.println("Gender changed from " + oldGender + " to " + currentGender + " during load");
			}

		} catch (Exception e) {
			System.err.println("Failed to load appearance data: " + e.getMessage());
			e.printStackTrace();
			updateStatus("Failed to load appearance data, using defaults", true);

			// Fall back to defaults on error
			resetToDefaults();
		}
	}

	/**
	 * Resets all appearance values to safe defaults
	 */
	private void resetToDefaults() {
		System.out.println("Resetting to default appearance values");
		currentGender = AppearanceConfig.Defaults.GENDER;
		currentHead = AppearanceConfig.Defaults.MALE_HEAD;
		currentJaw = AppearanceConfig.Defaults.MALE_JAW;
		currentTorso = AppearanceConfig.Defaults.MALE_TORSO;
		currentArms = AppearanceConfig.Defaults.MALE_ARMS;
		currentHands = AppearanceConfig.Defaults.MALE_HANDS;
		currentLegs = AppearanceConfig.Defaults.MALE_LEGS;
		currentFeet = AppearanceConfig.Defaults.MALE_FEET;
		currentHairColor = AppearanceConfig.Defaults.HAIR_COLOR;
		currentTorsoColor = AppearanceConfig.Defaults.TORSO_COLOR;
		currentLegsColor = AppearanceConfig.Defaults.LEGS_COLOR;
		currentFeetColor = AppearanceConfig.Defaults.FEET_COLOR;
		currentSkinColor = AppearanceConfig.Defaults.SKIN_COLOR;
	}

	/**
	 * Saves current appearance data to storage
	 */
	private void saveAppearanceToStorage() {
		if (!Client.loggedIn || Client.myUsername == null) {
			System.out.println("Cannot save appearance: not logged in or no username");
			return;
		}

		try {
			AppearanceStorage.AppearanceData data = new AppearanceStorage.AppearanceData();
			data.gender = currentGender;
			data.head = currentHead;
			data.jaw = currentJaw;
			data.torso = currentTorso;
			data.arms = currentArms;
			data.hands = currentHands;
			data.legs = currentLegs;
			data.feet = currentFeet;
			data.hairColor = currentHairColor;
			data.torsoColor = currentTorsoColor;
			data.legsColor = currentLegsColor;
			data.feetColor = currentFeetColor;
			data.skinColor = currentSkinColor;

			storage.setCurrentUser(Client.myUsername);
			storage.saveAppearance(data);

			System.out.println("Successfully saved appearance data for " + Client.myUsername);

		} catch (Exception e) {
			System.err.println("Failed to save appearance data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Click throttling to prevent rapid-fire clicks
	private long lastClickTime = 0;
	private static final long CLICK_DELAY = 300;

	// Appearance change methods
	private void changeGender(byte newGender) {
		if (!canClick()) return;
		ensureAppearanceLoaded(); // Make sure we have current data

		if (currentGender == newGender) {
			updateStatus("Already " + (newGender == 0 ? "male" : "female"), false);
			return;
		}

		System.out.println("=== GENDER CHANGE DEBUG ===");
		System.out.println("Changing from gender " + currentGender + " to " + newGender);

		currentGender = newGender;

		// Reset to safe defaults for new gender (but keep colors)
		if (newGender == 0) { // Male
			currentHead = AppearanceConfig.Defaults.MALE_HEAD;
			currentJaw = AppearanceConfig.Defaults.MALE_JAW;
			currentTorso = AppearanceConfig.Defaults.MALE_TORSO;
			currentArms = AppearanceConfig.Defaults.MALE_ARMS;
			currentHands = AppearanceConfig.Defaults.MALE_HANDS;
			currentLegs = AppearanceConfig.Defaults.MALE_LEGS;
			currentFeet = AppearanceConfig.Defaults.MALE_FEET;
		} else { // Female
			currentHead = AppearanceConfig.Defaults.FEMALE_HEAD;
			currentJaw = AppearanceConfig.Defaults.FEMALE_JAW;
			currentTorso = AppearanceConfig.Defaults.FEMALE_TORSO;
			currentArms = AppearanceConfig.Defaults.FEMALE_ARMS;
			currentHands = AppearanceConfig.Defaults.FEMALE_HANDS;
			currentLegs = AppearanceConfig.Defaults.FEMALE_LEGS;
			currentFeet = AppearanceConfig.Defaults.FEMALE_FEET;
		}

		System.out.println("New appearance values set:");
		System.out.println("  Head: " + currentHead + ", Jaw: " + currentJaw + ", Torso: " + currentTorso);
		System.out.println("  Arms: " + currentArms + ", Hands: " + currentHands + ", Legs: " + currentLegs + ", Feet: " + currentFeet);

		// Save immediately after gender change
		saveAppearanceToStorage();

		// Send gender change with special handling
		sendGenderChangeUpdate("Changed to " + (newGender == 0 ? "Stoner" : "Stonerette"));
	}

	private void changeAppearancePart(int partIndex, int direction) {
		if (!canClick()) return;
		ensureAppearanceLoaded(); // Make sure we have current data

		int[] range = AppearanceConfig.getPartRange(partIndex, currentGender);

		// Skip if no options available (like female jaw)
		if (range[0] == -1 && range[1] == -1) {
			updateStatus("No " + AppearanceConfig.UI.PART_NAMES[partIndex].toLowerCase() + " options available", false);
			return;
		}

		byte oldValue = getCurrentPartValue(partIndex);
		byte newValue = (byte) AppearanceConfig.cycleValue(oldValue, range[0], range[1], direction);

		// Only proceed if value actually changed
		if (newValue != oldValue) {
			setCurrentPartValue(partIndex, newValue);
			saveAppearanceToStorage(); // Save after each change
			sendAppearanceUpdate(AppearanceConfig.UI.PART_NAMES[partIndex] + " style changed");
		}
	}

	private void changeColor(int colorIndex, int direction) {
		if (!canClick()) return;
		ensureAppearanceLoaded(); // Make sure we have current data

		int[] range = AppearanceConfig.getColorRange(colorIndex);
		byte oldValue = getCurrentColorValue(colorIndex);
		byte newValue = (byte) AppearanceConfig.cycleValue(oldValue, range[0], range[1], direction);

		// Only proceed if value actually changed
		if (newValue != oldValue) {
			setCurrentColorValue(colorIndex, newValue);
			saveAppearanceToStorage(); // Save after each change
			sendAppearanceUpdate(AppearanceConfig.UI.COLOR_NAMES[colorIndex] + " color changed");
		}
	}

	// Helper methods for cleaner code
	private boolean canClick() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClickTime < CLICK_DELAY) {
			return false; // Too soon, ignore click
		}
		lastClickTime = currentTime;
		return true;
	}

	private byte getCurrentPartValue(int partIndex) {
		switch (partIndex) {
			case 0: return currentHead;
			case 1: return currentJaw;
			case 2: return currentTorso;
			case 3: return currentArms;
			case 4: return currentHands;
			case 5: return currentLegs;
			case 6: return currentFeet;
			default: return 0;
		}
	}

	private void setCurrentPartValue(int partIndex, byte value) {
		switch (partIndex) {
			case 0: currentHead = value; break;
			case 1: currentJaw = value; break;
			case 2: currentTorso = value; break;
			case 3: currentArms = value; break;
			case 4: currentHands = value; break;
			case 5: currentLegs = value; break;
			case 6: currentFeet = value; break;
		}
	}

	private byte getCurrentColorValue(int colorIndex) {
		switch (colorIndex) {
			case AppearanceConfig.Colors.HAIR: return currentHairColor;
			case AppearanceConfig.Colors.TORSO: return currentTorsoColor;
			case AppearanceConfig.Colors.LEGS: return currentLegsColor;
			case AppearanceConfig.Colors.FEET: return currentFeetColor;
			case AppearanceConfig.Colors.SKIN: return currentSkinColor;
			default: return 0;
		}
	}

	private void setCurrentColorValue(int colorIndex, byte value) {
		switch (colorIndex) {
			case AppearanceConfig.Colors.HAIR: currentHairColor = value; break;
			case AppearanceConfig.Colors.TORSO: currentTorsoColor = value; break;
			case AppearanceConfig.Colors.LEGS: currentLegsColor = value; break;
			case AppearanceConfig.Colors.FEET: currentFeetColor = value; break;
			case AppearanceConfig.Colors.SKIN: currentSkinColor = value; break;
		}
	}

	/**
	 * Special handling for gender changes - sends multiple packets to ensure proper refresh
	 */
	private void sendGenderChangeUpdate(String statusMessage) {
		SwingUtilities.invokeLater(() -> {
			try {
				if (Client.stream == null) {
					updateStatus("Error: Not connected to server", true);
					return;
				}

				System.out.println("=== SENDING GENDER CHANGE PACKET ===");
				printCurrentAppearance();

				// Send initial gender change packet
				sendAppearancePacket();
				updateStatus(statusMessage, false);

				// Send a follow-up packet after short delay to ensure refresh
				Timer refreshTimer = new Timer(150, e -> {
					try {
						System.out.println("=== SENDING GENDER REFRESH PACKET ===");
						sendAppearancePacket();
						System.out.println("Gender change refresh packet sent");
					} catch (Exception ex) {
						System.err.println("Failed to send gender refresh: " + ex.getMessage());
					}
				});
				refreshTimer.setRepeats(false);
				refreshTimer.start();
				saveAppearanceToStorage();

			} catch (Exception ex) {
				updateStatus("Error: " + ex.getMessage(), true);
				System.err.println("Failed to send gender change: " + ex.getMessage());
				ex.printStackTrace();
			}
		});
	}

	/**
	 * Regular appearance update for non-gender changes
	 */
	private void sendAppearanceUpdate(String statusMessage) {
		SwingUtilities.invokeLater(() -> {
			try {
				if (Client.stream == null) {
					updateStatus("Error: Not connected to server", true);
					return;
				}

				sendAppearancePacket();
				updateStatus(statusMessage, false);

			} catch (Exception ex) {
				updateStatus("Error: " + ex.getMessage(), true);
				System.err.println("Failed to send appearance update: " + ex.getMessage());
				ex.printStackTrace();
			}
		});
	}

	/**
	 * Core packet sending method - handles the actual protocol communication
	 */
	private void sendAppearancePacket() throws Exception {
		// CRITICAL FIX: Handle female jaw validation properly
		int jawToSend;
		if (currentGender == 1) { // Female
			jawToSend = -1; // Send -1 as that's what the validation expects
		} else {
			jawToSend = currentJaw;
		}

		System.out.println("Sending packet with jaw value: " + jawToSend + " (original: " + currentJaw + ")");

		// Send packet 101 with complete appearance data
		Client.stream.createFrame(101);
		Client.stream.writeWordBigEndian(currentGender);
		Client.stream.writeWordBigEndian(currentHead);
		Client.stream.writeWordBigEndian(jawToSend);
		Client.stream.writeWordBigEndian(currentTorso);
		Client.stream.writeWordBigEndian(currentArms);
		Client.stream.writeWordBigEndian(currentHands);
		Client.stream.writeWordBigEndian(currentLegs);
		Client.stream.writeWordBigEndian(currentFeet);

		// Write colors in server-expected order
		Client.stream.writeWordBigEndian(currentHairColor);
		Client.stream.writeWordBigEndian(currentTorsoColor);
		Client.stream.writeWordBigEndian(currentLegsColor);
		Client.stream.writeWordBigEndian(currentFeetColor);
		Client.stream.writeWordBigEndian(currentSkinColor);

		printCurrentAppearance();
	}

	/**
	 * Debug helper to print current appearance state
	 */
	private void printCurrentAppearance() {
		System.out.println("Current appearance state:");
		System.out.println("  Gender: " + currentGender + " (" + (currentGender == 0 ? "Male" : "Female") + ")");
		System.out.println("  Head: " + currentHead);
		System.out.println("  Jaw: " + currentJaw);
		System.out.println("  Torso: " + currentTorso);
		System.out.println("  Arms: " + currentArms);
		System.out.println("  Hands: " + currentHands);
		System.out.println("  Legs: " + currentLegs);
		System.out.println("  Feet: " + currentFeet);
		System.out.println("  Hair Color: " + currentHairColor);
		System.out.println("  Torso Color: " + currentTorsoColor);
		System.out.println("  Legs Color: " + currentLegsColor);
		System.out.println("  Feet Color: " + currentFeetColor);
		System.out.println("  Skin Color: " + currentSkinColor);
	}

	private void updateStatus(String message, boolean isError) {
		AppearanceStyle.updateStatusLabel(statusLabel, message, isError);
	}

	// UIPanel interface methods
	@Override
	public String getPanelID() {
		return "Appearance";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn) {
			updateStatus("Please log in to customize appearance", true);
			return;
		}

		// Force reload of appearance data when panel is activated
		System.out.println("=== PANEL ACTIVATED ===");
		appearanceLoaded = false; // Force reload
		ensureAppearanceLoaded();

		SwingUtilities.invokeLater(() -> {
			setVisible(true);
			updateStatus("Changes applied immediately", false);
			if (scrollPane != null) {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
			revalidate();
			repaint();
		});
	}

	@Override
	public void onDeactivate() {
		// Save appearance data when panel is deactivated
		if (Client.loggedIn) {
			saveAppearanceToStorage();
		}
	}

	@Override
	public void updateText() {
		repaint();
	}

	@Override
	public void updateDockText(int index, String text) {
		if (!Client.loggedIn) return;

		if (text.contains("appearance") || text.contains("style")) {
			updateStatus(text, false);
		}
	}

	@Override
	public int[] getBlockedInterfaces() {
		return new int[] { 3559 };
	}

	/**
	 * Get the panel icon path for the dock tab
	 */
	public String getPanelIconPath() {
		return "sprites/appearance-tab.png";
	}

	/**
	 * Public method to manually refresh appearance data (useful for external calls)
	 */
	public void refreshAppearanceData() {
		appearanceLoaded = false;
		lastLoadedUser = "";
		ensureAppearanceLoaded();
	}

	/**
	 * Public method to get current appearance summary for debugging
	 */
	public String getAppearanceSummary() {
		return String.format("User: %s, Gender: %s, Head: %d, Colors: H%d/T%d/L%d/F%d/S%d",
			Client.myUsername != null ? Client.myUsername : "Unknown",
			currentGender == 0 ? "Male" : "Female",
			currentHead,
			currentHairColor, currentTorsoColor, currentLegsColor, currentFeetColor, currentSkinColor);
	}
}