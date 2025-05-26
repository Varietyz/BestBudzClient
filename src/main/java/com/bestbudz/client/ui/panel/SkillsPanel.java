package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.client.util.SpriteUtil;
import com.bestbudz.data.Skills;
import com.bestbudz.engine.Client;

import java.awt.image.BufferedImage;
import javax.swing.*;
	import javax.swing.border.EmptyBorder;
import java.awt.*;
	import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class SkillsPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final SkillComponent[] skills = new SkillComponent[Skills.SKILLS_COUNT];
	private static final NumberFormat formatter = NumberFormat.getInstance();
	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel totalGradesLabel; // Reference to total grades label
	private JLabel totalXpLabel;     // Reference to total XP label
	private JButton totalOverlayButton; // Reference to total overlay button

	// Modern color scheme
	private static final Color BACKGROUND_COLOR = new Color(45, 45, 45);
	private static final Color TILE_BACKGROUND = new Color(60, 60, 60);
	private static final Color TILE_HOVER = new Color(75, 75, 75);
	private static final Color TILE_PRESSED = new Color(50, 50, 50);
	private static final Color BORDER_COLOR = new Color(100, 100, 100);
	private static final Color TEXT_PRIMARY = Color.WHITE;
	private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
	private static Color XP_TEXT = new Color(150, 150, 255); // Made non-final for color customization
	private static final Color GOLD_COLOR = new Color(255, 215, 0);

	// Advancement level colors (1-5+ grading system)
	private static final Color[] ADVANCE_COLORS = {
		new Color(100, 100, 100),    // Level 0 (shouldn't show, but just in case)
		new Color(46, 125, 50),      // Level 1 - Green (Bronze equivalent)
		new Color(183, 28, 28),      // Level 2 - Red (Iron equivalent)
		new Color(245, 127, 23),     // Level 3 - Orange (Gold equivalent)
		new Color(123, 31, 162),     // Level 4 - Purple (Platinum equivalent)
		new Color(13, 71, 161),      // Level 5+ - Blue (Diamond equivalent)
	};

	private boolean truncateXP = true;
	private boolean autoSort = true;
	private long[] lastUpdateTimes = new long[Skills.SKILLS_COUNT];
	private long[] lastXpValues = new long[Skills.SKILLS_COUNT];
	private int[] lastAdvanceValues = new int[Skills.SKILLS_COUNT]; // Track advancement changes
	private JSlider colorSlider;
	private static long lastClickTime = 0;

	// Mapping of skill names to server button IDs for profession chat
	private static final Map<String, Integer> SKILL_BUTTON_MAP = new HashMap<>();
	static {
		SKILL_BUTTON_MAP.put("assault", 94147);
		SKILL_BUTTON_MAP.put("aegis", 94153);
		SKILL_BUTTON_MAP.put("vigour", 94150);
		SKILL_BUTTON_MAP.put("life", 94148);
		SKILL_BUTTON_MAP.put("sagittarius", 94156);
		SKILL_BUTTON_MAP.put("necromance", 94159);
		SKILL_BUTTON_MAP.put("mage", 94162);
		SKILL_BUTTON_MAP.put("foodie", 94158);
		SKILL_BUTTON_MAP.put("lumbering", 94164);
		SKILL_BUTTON_MAP.put("woodcarving", 94163);
		SKILL_BUTTON_MAP.put("fisher", 94155);
		SKILL_BUTTON_MAP.put("pyromaniac", 94161);
		SKILL_BUTTON_MAP.put("handiness", 94160);
		SKILL_BUTTON_MAP.put("forging", 94152);
		SKILL_BUTTON_MAP.put("quarrying", 94149);
		SKILL_BUTTON_MAP.put("thc-hempistry", 94154);
		SKILL_BUTTON_MAP.put("weedsmoking", 94151);
		SKILL_BUTTON_MAP.put("accomplisher", 94157);
		SKILL_BUTTON_MAP.put("mercenary", 94166);
		SKILL_BUTTON_MAP.put("cultivation", 94167);
		SKILL_BUTTON_MAP.put("consumer", 94165);
	}

	// Experience table for calculating XP to next level
	private static final long[] XP_TABLE = {
		0, 15053, 30322, 45812, 61525, 77465, 93635, 110038, 126678, 143558,
		160681, 178051, 195671, 213546, 231678, 250072, 268732, 287660, 306862, 326340,
		346099, 366144, 386477, 407104, 428028, 449254, 470786, 492629, 514786, 537264,
		560065, 583195, 606659, 630461, 654607, 679101, 703948, 729154, 754723, 780660,
		806972, 833664, 860740, 888207, 916070, 944335, 973007, 1002093, 1031599, 1061530,
		1091893, 1122694, 1153939, 1185634, 1217787, 1250404, 1283491, 1317055, 1351103, 1385642,
		1420680, 1456223, 1492278, 1528853, 1565956, 1603594, 1641775, 1680507, 1719797, 1759654,
		1800086, 1841101, 1882707, 1924914, 1967729, 2011162, 2055221, 2099916, 2145255, 2191248,
		2237905, 2285234, 2333246, 2381951, 2431358, 2481478, 2532320, 2583896, 2636216, 2689290,
		2743130, 2797746, 2853150, 2909353, 2966367, 3024203, 3082873, 3142389, 3202764, 3264009,
		3326138, 3389163, 3453097, 3517953, 3583744, 3650484, 3718187, 3786867, 3856537, 3927211,
		3998905, 4071634, 4145411, 4220252, 4296172, 4373188, 4451314, 4530567, 4610963, 4692519,
		4775251, 4859176, 4944312, 5030675, 5118285, 5207157, 5297312, 5388767, 5481540, 5575652,
		5671121, 5767968, 5866211, 5965871, 6066968, 6169523, 6273558, 6379093, 6486150, 6594751,
		6704919, 6816675, 6930044, 7045047, 7161709, 7280054, 7400105, 7521888, 7645428, 7770749,
		7897878, 8026840, 8157662, 8290371, 8424995, 8561559, 8700094, 8840626, 8983186, 9127801,
		9274503, 9423320, 9574283, 9727424, 9882774, 10040364, 10200227, 10362396, 10526903, 10693783,
		10863071, 11034799, 11209005, 11385723, 11564990, 11746842, 11931318, 12118454, 12308289,
		12500862, 12696212, 12894380, 13095406, 13299331, 13506198, 13716048, 13928925, 14144872,
		14363933, 14586154, 14811581, 15040258, 15272234, 15507555, 15746270, 15988429, 16234080,
		16483274, 16736062, 16992496, 17252628, 17516512, 17784202, 18055754, 18331221, 18610662,
		18894133, 19181692, 19473399, 19769313, 20069495, 20374007, 20682910, 20996269, 21314147,
		21636610, 21963723, 22295555, 22632172, 22973645, 23320042, 23671436, 24027898, 24389500,
		24756319, 25128427, 25505903, 25888823, 26277265, 26671310, 27071039, 27476532, 27887874,
		28305149, 28728442, 29157840, 29593431, 30035304, 30483551, 30938262, 31399532, 31867455,
		32342126, 32823644, 33312106, 33807614, 34310268, 34820171, 35337429, 35862147, 36394434,
		36934397, 37482148, 38037799, 38601464, 39173259, 39753301, 40341709, 40938603, 41544106,
		42158342, 42781437, 43413519, 44054717, 44705163, 45364991, 46034335, 46713333, 47402124,
		48100849, 48809652, 49528678, 50258074, 50997990, 51748578, 52509991, 53282387, 54065922,
		54860758, 55667058, 56484987, 57314713, 58156406, 59010238, 59876385, 60755025, 61646336,
		62550504, 63467711, 64398148, 65342004, 66299473, 67270751, 68256039, 69255536, 70269450,
		71297986, 72341358, 73399777, 74473462, 75562633, 76667512, 77788327, 78925307, 80078686,
		81248700, 82435589, 83639596, 84860968, 86099955, 87356813, 88631798, 89925172, 91237200,
		92568151, 93918298, 95287918, 96677292, 98086704, 99516445, 100966806, 102438085, 103930585,
		105444610, 106980473, 108538486, 110118971, 111722250, 113348654, 114998515, 116672171,
		118369966, 120092249, 121839371, 123611692, 125409575, 127233388, 129083506, 130960308,
		132864179, 134795509, 136754694, 138742136, 140758243, 142803427, 144878109, 146982714,
		149117673, 151283424, 153480412, 155709086, 157969904, 160263329, 162589832, 164949890,
		167343987, 169772613, 172236267, 174735454, 177270686, 179842483, 182451373, 185097890,
		187782578, 190505987, 193268674, 196071208, 198914162, 201798120, 204723672, 207691419,
		210701969, 213755940, 216853958, 219996658, 223184685, 226418692, 229699342, 233027309,
		236403275, 239827931, 243301981, 246826136, 250401120, 254027665, 257706515, 261438425,
		265224159, 269064494, 272960218, 276912129, 280921037, 284987766, 289113148, 293298030,
		297543270, 301849738, 306218318, 310649905, 315145408, 319705748, 324331862, 329024698,
		333785217, 338614397, 343513227, 348482712, 353523871, 358637738, 363825361, 369087804,
		374426147, 379841484, 385334925, 390907597, 396560643, 402295222, 408112509, 414013699,
		420000000
	};

	public SkillsPanel() {
		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(BACKGROUND_COLOR);
		setBorder(new EmptyBorder(5, 5, 5, 5));

		// Create header with controls
		JPanel headerPanel = createHeaderPanel();
		add(headerPanel, BorderLayout.NORTH);

		// Create content panel with grid layout
		contentPanel = new JPanel();
		contentPanel.setLayout(new GridLayout(0, 2, 6, 6));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(new EmptyBorder(3, 8, 3, 8));

		// Create scroll pane
		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Style the scrollbar
		styleScrollBar(scrollPane);

		add(scrollPane, BorderLayout.CENTER);

		// Set tooltip delay to 1 second (1000ms)
		ToolTipManager.sharedInstance().setInitialDelay(1000);
		ToolTipManager.sharedInstance().setDismissDelay(5000); // Show for 5 seconds
		ToolTipManager.sharedInstance().setReshowDelay(500);   // Quick reshow delay

		// Populate skills
		populateSkills();
	}

	private JPanel createHeaderPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new EmptyBorder(0, 5, 5, 5));

		JLabel titleLabel = new JLabel("Skills");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);

		// Create controls panel
		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		controlsPanel.setOpaque(false);

		// Color slider
		JLabel colorLabel = new JLabel("XP:");
		colorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		colorLabel.setForeground(TEXT_SECONDARY);

		colorSlider = new JSlider(0, 360, 240);
		colorSlider.setOpaque(false);
		colorSlider.setPreferredSize(new Dimension(60, 16));

		colorSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(colorSlider) {
			@Override
			public void paintThumb(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(XP_TEXT);
				g2d.fillOval(thumbRect.x, thumbRect.y + 2, 8, 8);
				g2d.dispose();
			}

			@Override
			public void paintTrack(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(new Color(60, 60, 60));
				g2d.fillRoundRect(trackRect.x, trackRect.y + 4, trackRect.width, 4, 2, 2);
				g2d.dispose();
			}
		});

		colorSlider.addChangeListener(e -> {
			// Only update when slider stops moving to reduce lag
			if (!colorSlider.getValueIsAdjusting()) {
				int hue = colorSlider.getValue();
				XP_TEXT = Color.getHSBColor(hue / 360.0f, 0.7f, 0.9f);

				for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
					if (Skills.SKILL_ENABLED[i] && skills[i] != null) {
						skills[i].xpLabel.setForeground(XP_TEXT);
					}
				}
			}
		});

		// Toggle buttons
		JButton sortToggleButton = createToggleButton(autoSort ? "Fixed" : "Auto-sort");
		sortToggleButton.addActionListener(e -> {
			autoSort = !autoSort;
			sortToggleButton.setText(autoSort ? "Fixed" : "Auto-sort");
			refreshSkillOrder();
		});

		JButton xpToggleButton = createToggleButton(truncateXP ? "Full XP" : "Short XP");
		xpToggleButton.addActionListener(e -> {
			truncateXP = !truncateXP;
			xpToggleButton.setText(truncateXP ? "Full XP" : "Short XP");
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (Skills.SKILL_ENABLED[i] && skills[i] != null) {
					long xp = Client.currentExp[i] & 0xFFFFFFFFL;
					skills[i].xpLabel.setText(formatXP(xp));
				}
			}
		});

		controlsPanel.add(colorLabel);
		controlsPanel.add(colorSlider);
		controlsPanel.add(sortToggleButton);
		controlsPanel.add(xpToggleButton);

		headerPanel.add(titleLabel, BorderLayout.WEST);
		headerPanel.add(controlsPanel, BorderLayout.EAST);

		return headerPanel;
	}

	private JButton createToggleButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		button.setForeground(TEXT_SECONDARY);
		button.setBackground(TILE_BACKGROUND);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			new EmptyBorder(2, 4, 2, 4)
		));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(55, 18));
		return button;
	}

	private void populateSkills() {
		contentPanel.removeAll();

		// Always add total stats button first (not affected by sorting)
		contentPanel.add(createTotalStatsButton());

		if (autoSort) {
			java.util.List<Integer> enabledSkills = new java.util.ArrayList<>();
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (Skills.SKILL_ENABLED[i]) {
					enabledSkills.add(i);
				}
			}

			// Sort skills by last update time (most recent first)
			enabledSkills.sort((a, b) -> Long.compare(lastUpdateTimes[b], lastUpdateTimes[a]));

			// Add sorted skills (total stats button stays at top)
			for (int skillId : enabledSkills) {
				contentPanel.add(createSkillTile(skillId));
			}
		} else {
			// Default order - add skills in original order (total stats button stays at top)
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (!Skills.SKILL_ENABLED[i]) continue;
				contentPanel.add(createSkillTile(i));
			}
		}

		contentPanel.revalidate();
		contentPanel.repaint();
	}

	private JPanel createTotalStatsButton() {
		// Calculate totals
		int totalGrades = 0;
		long totalExp = 0;
		int totalAdvances = 0;

		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {
				totalGrades += Client.currentStats[i];
				totalExp += Client.currentExp[i] & 0xFFFFFFFFL;
				// Add advancement tracking when available
				if (Client.currentAdvances != null) {
					totalAdvances += Client.currentAdvances[i];
				}
			}
		}

		// Create wrapper panel
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setPreferredSize(new Dimension(132, 50));
		wrapperPanel.setOpaque(false);

		// Create layered pane
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(132, 50));

		// Create golden tile
		JPanel tile = createGoldenTile();
		tile.setBounds(0, 0, 132, 50);

		// Multi-line tooltip for total stats
		String totalTooltip = "<html>Total Statistics<br>" +
			"Combined Grades: " + totalGrades + "<br>" +
			"Combined Experience: " + formatter.format(totalExp);

		if (totalAdvances > 0) {
			totalTooltip += "<br>Combined Advances: " + totalAdvances;
		}
		totalTooltip += "</html>";

		tile.setToolTipText(totalTooltip);

		// Total icon from file
		ImageIcon totalIcon = SpriteUtil.loadIconScaled("sprites/skills/total.png", 24);
		if (totalIcon == null) {
			// Fallback to Sigma symbol if icon not found
			JLabel iconLabel = new JLabel("Σ");
			iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
			iconLabel.setForeground(GOLD_COLOR);
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconLabel.setBounds(32, 10, 24, 24);
			layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		} else {
			JLabel iconLabel = new JLabel(totalIcon);
			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
			iconLabel.setBounds(32, 10, 24, 24);
			layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		}

		// Total grades
		totalGradesLabel = new JLabel(String.valueOf(totalGrades));
		totalGradesLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		totalGradesLabel.setForeground(GOLD_COLOR);
		totalGradesLabel.setBounds(60, 10, 50, 24);
		totalGradesLabel.setToolTipText(totalTooltip);

		// Total XP
		totalXpLabel = new JLabel(formatXP(totalExp));
		totalXpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		totalXpLabel.setForeground(GOLD_COLOR);
		totalXpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalXpLabel.setBounds(0, 34, 132, 12);
		totalXpLabel.setToolTipText(totalTooltip);

		// Click overlay
		totalOverlayButton = new JButton();
		totalOverlayButton.setOpaque(false);
		totalOverlayButton.setContentAreaFilled(false);
		totalOverlayButton.setBorderPainted(false);
		totalOverlayButton.setFocusPainted(false);
		totalOverlayButton.setBounds(0, 0, 132, 50);
		totalOverlayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		totalOverlayButton.setToolTipText(totalTooltip); // Add tooltip to overlay button

		totalOverlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tile.setBackground(new Color(90, 75, 45));
				tile.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tile.setBackground(new Color(75, 60, 30));
				tile.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				tile.setBackground(new Color(60, 45, 15));
				tile.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tile.setBackground(new Color(90, 75, 45));
				tile.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastClickTime < 500) return;
				lastClickTime = currentTime;

				try {
					int clientButtonId = calculateClientButtonId(94144);
					Client.stream.createFrame(185);
					Client.stream.writeWord(clientButtonId);
					System.out.println("Total Stats clicked - sent button ID: " + clientButtonId);
				} catch (Exception ex) {
					System.out.println("Error: " + ex.getMessage());
				}
			}
		});

		// Add to layers
		layeredPane.add(tile, JLayeredPane.DEFAULT_LAYER);
		// Icon already added above with fallback logic
		layeredPane.add(totalGradesLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(totalXpLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(totalOverlayButton, JLayeredPane.MODAL_LAYER);

		// Add advancement indicator when available
		if (totalAdvances > 0) {
			JLabel advanceLabel = createAdvancementBadge(totalAdvances);
			advanceLabel.setBounds(114, -2, 18, 16); // Repositioned slightly left for better overlap balance
			layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
		}

		wrapperPanel.add(layeredPane, BorderLayout.CENTER);
		return wrapperPanel;
	}

	private String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private JPanel createSkillTile(int id) {
		ImageIcon icon = SpriteUtil.loadIconScaled("sprites/skills/" + Skills.SKILL_NAMES[id] + ".png", 24);
		if (icon == null) {
			icon = new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
		}

		int grade = Client.currentStats[id];
		long xp = Client.currentExp[id] & 0xFFFFFFFFL;
		String name = capitalize(Skills.SKILL_NAMES[id]);
		int advance = (Client.currentAdvances != null) ? Client.currentAdvances[id] : 0;

		// Create wrapper panel
		JPanel wrapperPanel = new JPanel(new BorderLayout());
		wrapperPanel.setPreferredSize(new Dimension(132, 50));
		wrapperPanel.setOpaque(false);

		// Create layered pane
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setPreferredSize(new Dimension(132, 50));

		// Create tile
		JPanel tile = createModernTile();
		tile.setBounds(0, 0, 132, 50);

		// Create multi-line tooltip with XP progression
		String progressionInfo = getXpToNextLevel(xp, grade);
		String multiLineTooltip = "<html>" + name + "<br>" +
			"Grade: " + grade + "<br>" +
			"Experience: " + formatter.format(xp) + "<br>" +
			"Progress: " + progressionInfo;

		if (advance > 0) {
			multiLineTooltip += "<br>Advance Level: " + advance;
		}
		multiLineTooltip += "</html>";

		tile.setToolTipText(multiLineTooltip);

		// Icon
		JLabel iconLabel = new JLabel(icon);
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setBounds(32, 10, 24, 24);

		// Grade
		JLabel gradeLabel = new JLabel(String.valueOf(grade));
		gradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		gradeLabel.setForeground(TEXT_PRIMARY);
		gradeLabel.setBounds(60, 10, 50, 24);

		// XP
		JLabel xpLabel = new JLabel(formatXP(xp));
		xpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		xpLabel.setForeground(XP_TEXT);
		xpLabel.setHorizontalAlignment(SwingConstants.CENTER);
		xpLabel.setBounds(0, 34, 132, 12);

		// Click overlay
		JButton overlayButton = new JButton();
		overlayButton.setOpaque(false);
		overlayButton.setContentAreaFilled(false);
		overlayButton.setBorderPainted(false);
		overlayButton.setFocusPainted(false);
		overlayButton.setBounds(0, 0, 132, 50);
		overlayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		overlayButton.setToolTipText(multiLineTooltip); // Add tooltip to overlay button

		overlayButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				tile.setBackground(TILE_HOVER);
				tile.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tile.setBackground(TILE_BACKGROUND);
				tile.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				tile.setBackground(TILE_PRESSED);
				tile.repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				tile.setBackground(TILE_HOVER);
				tile.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				long currentTime = System.currentTimeMillis();
				String skillName = Skills.SKILL_NAMES[id].toLowerCase();

				if (currentTime - lastClickTime < 500) return;
				lastClickTime = currentTime;

				Integer serverButtonId = SKILL_BUTTON_MAP.get(skillName);

				if (serverButtonId != null) {
					int clientButtonId = calculateClientButtonId(serverButtonId);

					try {
						Client.stream.createFrame(185);
						Client.stream.writeWord(clientButtonId);
						System.out.println("Skill " + skillName + " clicked - sent ID: " + clientButtonId);
					} catch (Exception ex) {
						System.out.println("Error: " + ex.getMessage());
					}
				}
			}
		});

		// Add to layers
		layeredPane.add(tile, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(iconLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(gradeLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(xpLabel, JLayeredPane.PALETTE_LAYER);
		layeredPane.add(overlayButton, JLayeredPane.MODAL_LAYER);

		// Add advancement indicator when available
		JLabel advanceLabel = null;
		if (advance > 0) {
			advanceLabel = createAdvancementBadge(advance);
			layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
		}

		wrapperPanel.add(layeredPane, BorderLayout.CENTER);

		skills[id] = new SkillComponent(tile, iconLabel, gradeLabel, xpLabel, overlayButton, advanceLabel);
		lastXpValues[id] = xp;
		lastAdvanceValues[id] = advance;

		return wrapperPanel;
	}

	private JPanel createModernTile() {
		JPanel tile = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				g2d.setColor(BORDER_COLOR);
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

				g2d.dispose();
			}
		};

		tile.setOpaque(false);
		tile.setBackground(TILE_BACKGROUND);
		tile.setLayout(null);
		return tile;
	}

	private JPanel createGoldenTile() {
		JPanel tile = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				g2d.setColor(GOLD_COLOR);
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

				g2d.dispose();
			}
		};

		tile.setOpaque(false);
		tile.setBackground(new Color(75, 60, 30));
		tile.setLayout(null);
		return tile;
	}

	private int calculateClientButtonId(int serverButtonId) {
		int byte1 = serverButtonId / 1000;
		int byte2 = serverButtonId % 1000;

		if (byte1 > 255 || byte2 > 255) {
			return -1;
		}

		return (byte1 << 8) | byte2;
	}

	private boolean isInTopThree(int skillId) {
		if (!autoSort) return false;

		java.util.List<Integer> sortedSkills = new java.util.ArrayList<>();
		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {
				sortedSkills.add(i);
			}
		}
		sortedSkills.sort((a, b) -> Long.compare(lastUpdateTimes[b], lastUpdateTimes[a]));

		return sortedSkills.size() >= 3 && sortedSkills.subList(0, 3).contains(skillId);
	}

	private void refreshSkillOrder() {
		SwingUtilities.invokeLater(() -> {
			populateSkills();
		});
	}

	private void updateTotalStats() {
		// Calculate new totals
		int totalGrades = 0;
		long totalExp = 0;
		int totalAdvances = 0;

		for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
			if (Skills.SKILL_ENABLED[i]) {
				totalGrades += Client.currentStats[i];
				totalExp += Client.currentExp[i] & 0xFFFFFFFFL;
				// Add advancement tracking when available
				if (Client.currentAdvances != null) {
					totalAdvances += Client.currentAdvances[i];
				}
			}
		}

		// Update the labels if they exist
		if (totalGradesLabel != null) {
			totalGradesLabel.setText(String.valueOf(totalGrades));
		}

		if (totalXpLabel != null) {
			totalXpLabel.setText(formatXP(totalExp));
		}

		// Update tooltip
		if (totalOverlayButton != null) {
			String updatedTooltip = "<html>Total Statistics<br>" +
				"Combined Grades: " + totalGrades + "<br>" +
				"Combined Experience: " + formatter.format(totalExp);

			if (totalAdvances > 0) {
				updatedTooltip += "<br>Combined Advances: " + totalAdvances;
			}
			updatedTooltip += "</html>";

			totalOverlayButton.setToolTipText(updatedTooltip);

			if (totalGradesLabel != null) {
				totalGradesLabel.setToolTipText(updatedTooltip);
			}

			if (totalXpLabel != null) {
				totalXpLabel.setToolTipText(updatedTooltip);
			}
		}
	}

	private void styleScrollBar(JScrollPane scrollPane) {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(100, 100, 100);
				this.trackColor = new Color(40, 40, 40);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createInvisibleButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createInvisibleButton();
			}

			private JButton createInvisibleButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});
		verticalBar.setPreferredSize(new Dimension(8, 0));
	}

	private String formatXP(long xp) {
		if (truncateXP) {
			if (xp >= 1_000_000_000L) {
				return String.format("%.1fB XP", xp / 1_000_000_000.0);
			} else if (xp >= 1_000_000L) {
				return String.format("%.1fM XP", xp / 1_000_000.0);
			} else if (xp >= 1_000L) {
				return String.format("%.1fK XP", xp / 1_000.0);
			}
		}
		return formatter.format(xp) + " XP";
	}

	@Override
	public void updateText() {
		if (!Client.loggedIn) return;

		SwingUtilities.invokeLater(() -> {
			for (int i = 0; i < Skills.SKILLS_COUNT; i++) {
				if (!Skills.SKILL_ENABLED[i]) continue;
				updateSkill(i);
			}
		});
	}

	public void updateSkill(int id) {
		if (!Client.loggedIn) return;
		if (id < 0 || id >= skills.length || skills[id] == null || !Skills.SKILL_ENABLED[id]) return;

		SwingUtilities.invokeLater(() -> {
			int grade = Client.currentStats[id];
			long xp = Client.currentExp[id] & 0xFFFFFFFFL;
			String name = capitalize(Skills.SKILL_NAMES[id]);
			int advance = (Client.currentAdvances != null) ? Client.currentAdvances[id] : 0;

			boolean xpChanged = lastXpValues[id] != xp;
			boolean advanceChanged = lastAdvanceValues[id] != advance;
			lastXpValues[id] = xp;
			lastAdvanceValues[id] = advance;

			skills[id].gradeLabel.setText(String.valueOf(grade));
			skills[id].xpLabel.setText(formatXP(xp));

			// Update advancement label
			if (skills[id].advanceLabel != null) {
				if (advance > 0) {
					// Update existing label with new styling
					JLabel newAdvanceLabel = createAdvancementBadge(advance);

					// Find the layered pane and replace the old label
					Component parent = skills[id].tile.getParent();
					if (parent instanceof JLayeredPane) {
						JLayeredPane layeredPane = (JLayeredPane) parent;
						layeredPane.remove(skills[id].advanceLabel);
						layeredPane.add(newAdvanceLabel, JLayeredPane.PALETTE_LAYER);
						skills[id].advanceLabel = newAdvanceLabel;
						layeredPane.revalidate();
						layeredPane.repaint();
					}
				} else {
					skills[id].advanceLabel.setVisible(false);
				}
			} else if (advance > 0) {
				// Create advancement label if it doesn't exist but should
				JLabel advanceLabel = createAdvancementBadge(advance);

				// Find the layered pane and add the advance label
				Component parent = skills[id].tile.getParent();
				if (parent instanceof JLayeredPane) {
					JLayeredPane layeredPane = (JLayeredPane) parent;
					layeredPane.add(advanceLabel, JLayeredPane.PALETTE_LAYER);
					skills[id].advanceLabel = advanceLabel;
				}
			}

			// Update tooltips with new values including XP progression for all components
			String progressionInfo = getXpToNextLevel(xp, grade);
			String updatedTooltip = "<html>" + name + "<br>" +
				"Grade: " + grade + "<br>" +
				"Experience: " + formatter.format(xp) + "<br>" +
				"Progress: " + progressionInfo;

			if (advance > 0) {
				updatedTooltip += "<br>Advance Level: " + advance;
			}
			updatedTooltip += "</html>";

			skills[id].gradeLabel.setToolTipText(updatedTooltip);
			skills[id].iconLabel.setToolTipText(updatedTooltip);
			skills[id].xpLabel.setToolTipText(updatedTooltip);
			skills[id].tile.setToolTipText(updatedTooltip);
			skills[id].overlayButton.setToolTipText(updatedTooltip); // Update overlay button directly

			if ((xpChanged || advanceChanged) && autoSort) {
				boolean wasInTopThree = isInTopThree(id);
				lastUpdateTimes[id] = System.currentTimeMillis();

				if (!wasInTopThree) {
					refreshSkillOrder();
				}
			}

			// Update total stats when any skill changes
			updateTotalStats();
		});
	}

	private static class SkillComponent {
		JPanel tile;
		JLabel iconLabel;
		JLabel gradeLabel;
		JLabel xpLabel;
		JButton overlayButton;
		JLabel advanceLabel; // Add reference to advancement label

		SkillComponent(JPanel tile, JLabel iconLabel, JLabel gradeLabel, JLabel xpLabel, JButton overlayButton, JLabel advanceLabel) {
			this.tile = tile;
			this.iconLabel = iconLabel;
			this.gradeLabel = gradeLabel;
			this.xpLabel = xpLabel;
			this.overlayButton = overlayButton;
			this.advanceLabel = advanceLabel;
		}
	}

	private String getXpToNextLevel(long currentXp, int currentGrade) {
		if (currentGrade >= 420) {
			return "MAX LEVEL";
		}

		// Find the XP required for the next level
		int nextLevel = currentGrade + 1;
		if (nextLevel >= XP_TABLE.length) {
			return "MAX LEVEL";
		}

		long xpForNextLevel = XP_TABLE[nextLevel];
		long xpNeeded = xpForNextLevel - currentXp;

		// Format the XP needed
		return formatter.format(xpNeeded) + " XP to level " + nextLevel;
	}

	@Override
	public String getPanelID() {
		return "Skills";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		updateText();
	}



	@Override
	public void onDeactivate() {
		// No-op
	}

	@Override
	public void updateDockText(int index, String text) {
		// Advancement data comes from packet 134 (SendProfession), not interface tooltips
		// This method is for interface text updates only
		// No-op for advancement parsing
	}

	/**
	 * Parses advancement level from server text
	 * @param text Server text like "Assault lv: @dre@5@bla@/@dre@10\nAdvance lv: @dre@3"
	 * @return advancement level or -1 if not found/parseable
	 */
	private int parseAdvancementLevel(String text) {
		if (text == null || text.isEmpty()) {
			System.out.println("parseAdvancementLevel: text is null or empty");
			return 0; // No advancement if no text
		}

		System.out.println("parseAdvancementLevel: parsing text: '" + text + "'");

		try {
			// Look for "Advance lv: @dre@" pattern
			String advancePattern = "Advance lv: @dre@";
			int advanceIndex = text.indexOf(advancePattern);

			System.out.println("parseAdvancementLevel: advancePattern found at index: " + advanceIndex);

			if (advanceIndex != -1) {
				// Extract the number after "@dre@"
				int startIndex = advanceIndex + advancePattern.length();
				int endIndex = startIndex;

				// Find where the number ends (look for non-digit character)
				while (endIndex < text.length() && Character.isDigit(text.charAt(endIndex))) {
					endIndex++;
				}

				if (endIndex > startIndex) {
					String advanceStr = text.substring(startIndex, endIndex);
					int result = Integer.parseInt(advanceStr);
					System.out.println("parseAdvancementLevel: successfully parsed: " + result);
					return result;
				} else {
					System.out.println("parseAdvancementLevel: no digits found after pattern");
				}
			} else {
				// Try alternative patterns in case the format is different
				String[] alternativePatterns = {
					"Advance lv: ",
					"\\nAdvance lv: @dre@",
					"Advance grade: @dre@",
					"\\nAdvance grade: @dre@"
				};

				for (String pattern : alternativePatterns) {
					int patternIndex = text.indexOf(pattern);
					if (patternIndex != -1) {
						System.out.println("parseAdvancementLevel: found alternative pattern: " + pattern + " at index: " + patternIndex);
						int startIndex = patternIndex + pattern.length();
						int endIndex = startIndex;

						while (endIndex < text.length() && Character.isDigit(text.charAt(endIndex))) {
							endIndex++;
						}

						if (endIndex > startIndex) {
							String advanceStr = text.substring(startIndex, endIndex);
							int result = Integer.parseInt(advanceStr);
							System.out.println("parseAdvancementLevel: successfully parsed with alternative pattern: " + result);
							return result;
						}
					}
				}

				System.out.println("parseAdvancementLevel: no advancement pattern found");
			}

			return 0; // Default to 0 if no advancement found
		} catch (NumberFormatException e) {
			System.out.println("parseAdvancementLevel: NumberFormatException parsing: " + text);
			return 0;
		}
	}

	/**
	 * Gets the appropriate color for an advancement level
	 */
	private Color getAdvancementColor(int level) {
		if (level <= 0) return ADVANCE_COLORS[0];
		if (level >= ADVANCE_COLORS.length) return ADVANCE_COLORS[ADVANCE_COLORS.length - 1];
		return ADVANCE_COLORS[Math.min(level, ADVANCE_COLORS.length - 1)];
	}

	/**
	 * Creates a styled advancement badge
	 */
	private JLabel createAdvancementBadge(int advanceLevel) {
		JLabel badge = new JLabel(String.valueOf(advanceLevel)) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				// Get advancement color with transparency
				Color advanceColor = getAdvancementColor(advanceLevel);
				Color transparentColor = new Color(
					advanceColor.getRed(),
					advanceColor.getGreen(),
					advanceColor.getBlue(),
					140 // 55% opacity for subtle appearance
				);
				Color transparentDarker = new Color(
					advanceColor.darker().getRed(),
					advanceColor.darker().getGreen(),
					advanceColor.darker().getBlue(),
					140
				);

				// Draw background with subtle gradient - no border, overlapping style
				GradientPaint gradient = new GradientPaint(
					0, 0, transparentColor,
					0, getHeight(), transparentDarker
				);
				g2d.setPaint(gradient);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

				// Draw subtle inner highlight
				g2d.setColor(new Color(255, 255, 255, 30)); // Slightly more visible highlight
				g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 3, 4, 4);

				g2d.dispose();
				super.paintComponent(g);
			}
		};

		badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
		badge.setForeground(Color.WHITE);
		badge.setHorizontalAlignment(SwingConstants.CENTER);
		badge.setVerticalAlignment(SwingConstants.CENTER);
		badge.setBounds(114, -2, 18, 16); // Repositioned slightly left for better overlap balance
		badge.setOpaque(false);

		// Enhanced tooltip
		String levelName = getAdvancementLevelName(advanceLevel);
		badge.setToolTipText("<html><b>Advancement Level " + advanceLevel + "</b><br>" + levelName + "</html>");

		return badge;
	}

	/**
	 * Gets a descriptive name for advancement levels
	 */
	private String getAdvancementLevelName(int level) {
		switch (level) {
			case 1: return "Novice";
			case 2: return "Adept";
			case 3: return "Expert";
			case 4: return "Master";
			case 5: return "Grandmaster";
			default: return level > 5 ? "Legendary" : "None";
		}
	}
}