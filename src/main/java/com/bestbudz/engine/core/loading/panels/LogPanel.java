package com.bestbudz.engine.core.loading.panels;

import com.bestbudz.engine.core.loading.LoadingUtilities;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

public class LogPanel extends JPanel {
	private JScrollPane logScrollPane;
	private JTextArea logArea;
	private JLabel logTitleLabel;
	private final ConcurrentLinkedQueue<LogEntry> logQueue;
	private final AtomicLong startTime;
	private final LoadingUtilities.FontManager fontManager;

	public LogPanel(LoadingUtilities.FontManager fontManager, AtomicLong startTime) {
		this.fontManager = fontManager;
		this.startTime = startTime;
		this.logQueue = new ConcurrentLinkedQueue<>();

		setLayout(new BorderLayout());
		setOpaque(false);

		createComponents();
		layoutComponents();
	}

	private void createComponents() {

		logTitleLabel = new JLabel("Loading Progress");
		logTitleLabel.setFont(fontManager.detailFont);
		logTitleLabel.setForeground(SECONDARY_TEXT);

		logArea = new JTextArea();
		logArea.setBackground(new Color(22, 22, 24));
		logArea.setForeground(PRIMARY_TEXT);
		logArea.setFont(fontManager.monospaceFont);
		logArea.setEditable(false);
		logArea.setMargin(new Insets(8, 8, 8, 8));

		logScrollPane = new JScrollPane(logArea);
		logScrollPane.setPreferredSize(new Dimension(600, 120));
		logScrollPane.setBackground(new Color(22, 22, 24));
		logScrollPane.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 44), 1));
		logScrollPane.getViewport().setBackground(new Color(22, 22, 24));

		LoadingUtilities.styleScrollBar(logScrollPane);
	}

	private void layoutComponents() {
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setOpaque(false);
		titlePanel.add(logTitleLabel, BorderLayout.WEST);

		add(titlePanel, BorderLayout.NORTH);
		add(Box.createVerticalStrut(8), BorderLayout.CENTER);
		add(logScrollPane, BorderLayout.SOUTH);
	}

	public void addLogEntry(String message, LogLevel level) {
		logQueue.offer(new LogEntry(message, level));
	}

	public void processLogQueue() {
		LogEntry entry;
		int processed = 0;
		while ((entry = logQueue.poll()) != null && processed < 10) {
			appendLogEntry(entry);
			processed++;
		}
	}

	private void appendLogEntry(LogEntry entry) {
		String timestamp = LoadingUtilities.formatElapsedTime(entry.timestamp, startTime.get());
		String logLine = String.format("[%s] %s %s: %s\n",
			timestamp, entry.level.emoji, entry.level.prefix, entry.message);

		SwingUtilities.invokeLater(() -> {
			logArea.append(logLine);
			logArea.setCaretPosition(logArea.getDocument().getLength());

			String[] lines = logArea.getText().split("\n");
			if (lines.length > 500) {
				StringBuilder newText = new StringBuilder();
				for (int i = lines.length - 500; i < lines.length; i++) {
					newText.append(lines[i]).append("\n");
				}
				logArea.setText(newText.toString());
			}
		});
	}

	public void clearLog() {
		SwingUtilities.invokeLater(() -> logArea.setText(""));
	}

	public String getLogContent() {
		return logArea.getText();
	}

	public void setLogTitle(String title) {
		SwingUtilities.invokeLater(() -> logTitleLabel.setText(title));
	}
}
