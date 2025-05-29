package com.bestbudz.dock.ui.modal.dialogue.helper;

/**
 * DialogueTextCleaner - Helper class for cleaning dialogue text
 * Handles removal of formatting codes, continue prompts, and visual artifacts
 */
public class DialogueTextCleaner {

	/**
	 * Clean and filter text from dialogue interfaces
	 */
	public static String cleanAndFilterText(String text) {
		if (text == null) return "";

		// Remove common continue prompts (case insensitive)
		String cleaned = text.replaceAll("(?i)click here to continue", "");
		cleaned = cleaned.replaceAll("(?i)\\[click to continue\\]", "");
		cleaned = cleaned.replaceAll("(?i)\\(click to continue\\)", "");
		cleaned = cleaned.replaceAll("(?i)press any key to continue", "");
		cleaned = cleaned.replaceAll("(?i)continue\\.\\.\\.", "");

		// Remove /n/n/n** patterns and other formatting artifacts
		cleaned = cleaned.replaceAll("/n+", " "); // Remove /n, /n/n, /n/n/n patterns
		cleaned = cleaned.replaceAll("\\\\n+", " "); // Remove \n, \n\n, \n\n\n patterns
		cleaned = cleaned.replaceAll("\\*{2,}", ""); // Remove ** patterns
		cleaned = cleaned.replaceAll("\\^+", ""); // Remove ^ patterns
		cleaned = cleaned.replaceAll("~+", ""); // Remove ~ patterns
		cleaned = cleaned.replaceAll("#{2,}", ""); // Remove ## patterns
		cleaned = cleaned.replaceAll("_{2,}", ""); // Remove __ patterns
		cleaned = cleaned.replaceAll("-{3,}", ""); // Remove --- patterns

		// Remove HTML-like tags
		cleaned = cleaned.replaceAll("<[^>]+>", "");

		// Remove RuneScape color codes
		cleaned = cleaned.replaceAll("@\\w+@", "");

		// Remove image tags
		cleaned = cleaned.replaceAll("<img=\\d+>", "");

		// Clean up extra whitespace and punctuation
		cleaned = cleaned.replaceAll("\\s+", " "); // Multiple spaces to single space
		cleaned = cleaned.replaceAll("^[\\s\\.\\,\\;\\:]+", ""); // Leading punctuation
		cleaned = cleaned.replaceAll("[\\s\\.\\,\\;\\:]+$", ""); // Trailing punctuation

		// Remove multiple consecutive punctuation
		cleaned = cleaned.replaceAll("\\.{2,}", "."); // Multiple dots to single
		cleaned = cleaned.replaceAll("\\?{2,}", "?"); // Multiple ? to single
		cleaned = cleaned.replaceAll("!{2,}", "!"); // Multiple ! to single

		// Remove orphaned punctuation at start of sentences
		cleaned = cleaned.replaceAll("\\s+[\\.,;:]+\\s+", " ");

		cleaned = cleaned.trim();

		return cleaned;
	}

	/**
	 * Find continue prompt in text
	 */
	public static String findContinuePrompt(String text) {
		if (text == null) return null;

		String lowerText = text.toLowerCase();

		if (lowerText.contains("click here to continue")) {
			return "Click here to continue";
		} else if (lowerText.contains("click to continue")) {
			return "Click to continue";
		} else if (lowerText.contains("press any key to continue")) {
			return "Press any key to continue";
		} else if (lowerText.contains("continue...")) {
			return "Continue...";
		}

		return null;
	}

	/**
	 * Check if text looks like an NPC name
	 */
	public static boolean isNpcName(String text) {
		if (text == null || text.isEmpty()) return false;

		return text.length() < 25 &&
			!text.contains(".") &&
			!text.contains("?") &&
			!text.contains("!") &&
			Character.isUpperCase(text.charAt(0)) &&
			text.split(" ").length <= 2; // Names usually 1-2 words
	}
}