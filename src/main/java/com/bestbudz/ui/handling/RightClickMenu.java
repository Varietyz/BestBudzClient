package com.bestbudz.ui.handling;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.ui.handling.ActionHandler.doAction;
import static com.bestbudz.ui.handling.input.MouseActions.handleFallbackAction;
import static com.bestbudz.ui.handling.input.MouseActions.handleMenuClick;
import static com.bestbudz.ui.handling.input.MouseActions.handleMenuCloseOnOutsideClick;
import static com.bestbudz.ui.handling.input.MouseActions.handleWidgetDragStart;
import static com.bestbudz.ui.handling.input.MouseActions.isClickInsideOrbBounds;
import static com.bestbudz.ui.handling.input.MouseActions.processMainScreenClick;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.InterfaceManagement.handle3DOrInterfaceScreenMenu;
import static com.bestbudz.ui.InterfaceManagement.handleInputFieldSubmission;
import static com.bestbudz.ui.InterfaceManagement.handleOverlayInterfaces;
import static com.bestbudz.ui.TabArea.handleResizableTabAreaClick;
import static com.bestbudz.ui.TabArea.handleTabAreaInterfaces;
import static com.bestbudz.ui.TabArea.processTabClick;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.Stoner;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.ui.RSInterface;
import static com.bestbudz.ui.interfaces.StatusOrbs.handleMinimapInteractions;
import static com.bestbudz.util.FormatHelpers.combatDiffColor;
import java.util.HashSet;
import java.util.Set;

public class RightClickMenu extends Client
{
	public static void rightClickMenu(int xOffSet, int yOffSet) {
		final int PAD_TOP = 1;
		final int PAD_BOTTOM = 3;

		int xPos = menuOffsetX - (xOffSet - 4);
		int yPos = menuOffsetY + 4 - yOffSet;
		int menuW = menuWidth;
		int bgY = yPos + 18 - PAD_TOP;
		int bgH = menuActionRow * 15 + PAD_TOP + PAD_BOTTOM;

		drawMenuBackground(xPos, bgY, menuW, bgH);
		drawMenuEntries(xPos, yPos, menuW);

		inputTaken = true;
		tabAreaAltered = true;
	}

	private static void drawMenuBackground(int x, int y, int width, int height) {
		int panel = 0x1E1E1E;
		int border = 0x353535;

		DrawingArea.drawAlphaPixels(x, y, width, height, panel, 170);
		DrawingArea.drawPixels(height, y, x, border, 1);
		DrawingArea.drawPixels(height, y, x + width - 1, border, 1);
		DrawingArea.drawPixels(1, y, x, border, width);
		DrawingArea.drawPixels(1, y + height - 1, x, border, width);
	}

	private static void drawMenuEntries(int x, int y, int width) {
		final int hover = 0x2B2B2B;

		for (int i = 0; i < menuActionRow; i++) {
			int textY = y + 31 + (menuActionRow - 1 - i) * 15;
			String entry = menuActionName[i];
			int entryWidth = newBoldFont.getTextWidth(entry);
			int textX = x + (width - entryWidth) / 2;
			int textColor = ColorConfig.WHITE_COLOR;

			if (MouseState.x > x && MouseState.x < x + width &&
				MouseState.y > textY - 13 && MouseState.y < textY + 3) {
				DrawingArea.drawPixels(15, textY - 12, x + 2, hover, width - 4);
				textColor = 0xFFFFA0;
			}

			newBoldFont.drawBasicString(entry, textX, textY, textColor, 1);
		}
	}


	public static void deterquarryMenuSize() {
		int boxWidth = getMaxMenuTextWidth() + 8;
		int boxHeight = 15 * menuActionRow + 22;

		if (MouseState.x <= 0 || MouseState.y <= 0 ||
			MouseState.x >= frameWidth || MouseState.y >= frameHeight)
			return;

		int xClick = MouseState.x - boxWidth / 2;
		int yClick = MouseState.y - MENU_Y_SHIFT;

		// Clamp within canvas
		if (xClick + boxWidth > frameWidth - 4)
			xClick = frameWidth - 4 - boxWidth;
		if (xClick < 0)
			xClick = 0;

		if (yClick + boxHeight > frameHeight - 6)
			yClick = frameHeight - 6 - boxHeight;
		if (yClick < 0)
			yClick = 0;

		menuOpen = true;
		menuOffsetX = xClick;
		menuOffsetY = yClick;
		menuWidth = boxWidth;
		menuHeight = boxHeight;
	}


	private static int getMaxMenuTextWidth() {
		int max = newBoldFont.getTextWidth("Choose already");
		for (int i = 0; i < menuActionRow; i++) {
			int width = newBoldFont.getTextWidth(menuActionName[i]);
			if (width > max) max = width;
		}
		return max;
	}

	private int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}

	public static void processMenuClick(boolean leftClick, boolean rightClick) {
		if (activeInterfaceType != 0) {
			//System.out.println("🚫 BLOCKED: activeInterfaceType = " + activeInterfaceType);
			return;
		}

		if (spellSelected == 1 && MouseState.x >= 516 && MouseState.y >= 160 && MouseState.x <= 765 && MouseState.y <= 205)
			return;

		if (!leftClick && !rightClick) {
			if (menuOpen) handleMenuCloseOnOutsideClick();
			return;
		}

		if (menuOpen) {
			handleMenuClick();
			handleInputFieldSubmission();

		} else {
			if (handleResizableTabAreaClick(leftClick, rightClick)) return;
			handleWidgetDragStart(leftClick, rightClick);
			handleFallbackAction(leftClick, rightClick);
			processMainScreenClick(leftClick, rightClick);
			processTabClick(leftClick, rightClick);
			handleMinimapInteractions(leftClick, rightClick);
			if (!rightClick && menuActionRow > 0 && !isClickInsideOrbBounds()) {
				doAction(menuActionRow - 1);
				menuOpen = false;
			}


		}

		MouseState.leftClicked = false;
		MouseState.rightClicked = false;
	}

	public static void processRightClick() {
		if (activeInterfaceType != 0)
			return;

		resetRightClickMenu();

		Set<Integer> menuHandledInterfaces = new HashSet<>();

		handle3DOrInterfaceScreenMenu(menuHandledInterfaces);
		handleOverlayInterfaces(menuHandledInterfaces);
		handleTabAreaInterfaces(menuHandledInterfaces);

		sortMenuEntries();
	}

	private static void resetRightClickMenu() {
		menuActionName[0] = "Uuuuhhh, no..";
		menuActionID[0] = 1107;
		menuActionRow = 1;
		anInt886 = 0;
		anInt1315 = 0;
	}

	public static void sortMenuEntries() {
		boolean sorted = false;
		while (!sorted) {
			sorted = true;
			for (int i = 0; i < menuActionRow - 1; i++) {
				if (menuActionID[i] < 1000 && menuActionID[i + 1] > 1000) {
					swapMenuEntry(i, i + 1);
					sorted = false;
				}
			}
		}
	}

	private static void swapMenuEntry(int a, int b) {
		String tempName = menuActionName[a];
		menuActionName[a] = menuActionName[b];
		menuActionName[b] = tempName;

		int temp;

		temp = menuActionID[a];
		menuActionID[a] = menuActionID[b];
		menuActionID[b] = temp;

		temp = menuActionCmd1[a];
		menuActionCmd1[a] = menuActionCmd1[b];
		menuActionCmd1[b] = temp;

		temp = menuActionCmd2[a];
		menuActionCmd2[a] = menuActionCmd2[b];
		menuActionCmd2[b] = temp;

		temp = menuActionCmd3[a];
		menuActionCmd3[a] = menuActionCmd3[b];
		menuActionCmd3[b] = temp;
	}

	public static void drawContextMenu() {
		if (!menuOpen) {
			processRightClick();
			drawTooltip();
		} else if (menuScreenArea == 0) {
			rightClickMenu(0, 0);
		}
	}

	public static void drawTooltip(int xPos, int yPos, String text)
	{
		String[] results = text.split("\n");
		int height = (results.length * 16) + 6;
		int width;
		width = regularText.getTextWidth(results[0]) + 6;
		for (int i = 1; i < results.length; i++)
		{
			if (width <= regularText.getTextWidth(results[i]) + 6)
			{
				width = regularText.getTextWidth(results[i]) + 6;
			}
		}
		DrawingArea.drawPixels(height, yPos, xPos, 0x1e1e1e, width);
		DrawingArea.fillPixels(xPos, width, height, 0, yPos);
		yPos += 14;
		for (int i = 0; i < results.length; i++)
		{
			regularText.method389(false, xPos + 3, 0, results[i], yPos);
			yPos += 16;
		}
	}

	public static void drawTooltip()
	{
		if (menuActionRow < 2 && itemSelected == 0 && spellSelected == 0)
		{
			return;
		}

		String s;

		if (itemSelected == 1 && menuActionRow < 2)
		{
			s = "Select " + selectedItemName + ", combine with...";
		}
		else if (spellSelected == 1 && menuActionRow < 2)
		{
			s = spellTooltip + "...";
		}
		else
		{
			s = menuActionName[menuActionRow - 1];
		}
		s = s + "@gre@";
		int tooltipWidth   = Integer.sum(newBoldFont.getTextWidth(s.trim()), 4);
		int tooltipHeight  = 17;
		int tooltipX       = MouseState.x;
		int tooltipY       = Math.subtractExact(MouseState.y, 11);
		int maxX           = Math.subtractExact(Client.screenAreaWidth, tooltipWidth);
		int maxY           = Math.subtractExact(Client.screenAreaHeight, tooltipHeight);

		if (SettingsConfig.menuHovers && !s.contains("Shuffle over here"))
		{
			tooltipX = Math.min(Math.max(0, tooltipX), maxX);
			tooltipY = Math.min(Math.max(0, tooltipY), maxY);
			DrawingArea.drawAlphaPixels(tooltipX, tooltipY, tooltipWidth, tooltipHeight, 0, 100);
			newBoldFont.drawBasicString(s, Integer.sum(tooltipX, 2), Integer.sum(tooltipY + 10, 2), ColorConfig.WHITE_COLOR, 1);
		} else if (!s.contains("Shuffle over here")) {
			newBoldFont.drawBasicString(s, (Client.screenAreaWidth / 2) - (tooltipWidth / 2), 15, ColorConfig.WHITE_COLOR, 1);
		}
	}

	public static void buildAtStonerMenu(int i, int j, Stoner stoner, int k)
	{
		if (stoner == myStoner)
			return;
		if (menuActionRow >= 400)
			return;
		String s;
		String title = stoner.title.length() > 0
			? (stoner.titlePrefix ? " " : "") + "<col=" + stoner.titleColor + ">" + stoner.title + "</col>"
			+ (stoner.titlePrefix ? "" : " ")
			: "";
		if (stoner.skill == 0) {
			String diffColor = combatDiffColor(myStoner.combatLevel, stoner.combatLevel);

			if (!stoner.titlePrefix) {
				s = title
					+ diffColor + stoner.name + "</col>"  // name in diff color
					+ diffColor + " <img=11> " + stoner.combatLevel + "</col>";
			} else {
				s = diffColor + stoner.name + "</col>"
					+ title
					+ diffColor + " <img=11> " + stoner.combatLevel + "</col>";
			}
		}

		else
		{
			if (!stoner.titlePrefix)
			{
				s = title + stoner.name + " (profession-" + stoner.skill + ")";
			}
			else
			{
				s = stoner.name + title + " (profession-" + stoner.skill + ")";
			}
		}
		if (itemSelected == 1)
		{
			menuActionName[menuActionRow] = "Select " + selectedItemName + ", combine with @whi@" + s;
			menuActionID[menuActionRow] = 491;
			menuActionCmd1[menuActionRow] = j;
			menuActionCmd2[menuActionRow] = i;
			menuActionCmd3[menuActionRow] = k;
			menuActionRow++;
		}
		else if (spellSelected == 1)
		{
			if ((spellUsableOn & 8) == 8)
			{
				menuActionName[menuActionRow] = spellTooltip + " @whi@" + s;
				menuActionID[menuActionRow] = 365;
				menuActionCmd1[menuActionRow] = j;
				menuActionCmd2[menuActionRow] = i;
				menuActionCmd3[menuActionRow] = k;
				menuActionRow++;
			}
		}
		else
		{
			for (int l = 4; l >= 0; l--)
				if (atStonerActions[l] != null)
				{
					menuActionName[menuActionRow] = atStonerActions[l] + " @whi@" + s;
					char c = '\0';
					if (atStonerActions[l].equalsIgnoreCase("attack"))
					{
						if (SettingsConfig.entityAttackPriority && stoner.combatLevel > myStoner.combatLevel)
							c = '\u07D0';
						if (myStoner.team != 0 && stoner.team != 0)
							if (myStoner.team == stoner.team)
								c = '\u07D0';
							else
								c = '\0';
					}
					else if (atStonerArray[l])
						c = '\u07D0';
					if (l == 0)
						menuActionID[menuActionRow] = 561 + c;
					if (l == 1)
						menuActionID[menuActionRow] = 779 + c;
					if (l == 2)
						menuActionID[menuActionRow] = 27 + c;
					if (l == 3)
						menuActionID[menuActionRow] = 577 + c;
					if (l == 4)
						menuActionID[menuActionRow] = 729 + c;
					menuActionCmd1[menuActionRow] = j;
					menuActionCmd2[menuActionRow] = i;
					menuActionCmd3[menuActionRow] = k;
					menuActionRow++;
				}

		}
		for (int i1 = 0; i1 < menuActionRow; i1++)
		{
			if (menuActionID[i1] == 519)
			{
				menuActionName[i1] = "Shuffle over here @whi@" + s;
				return;
			}
		}
	}

	public static void buildAtNPCMenu(EntityDef entityDef, int i, int j, int k)
	{
		if (menuActionRow >= 400)
			return;
		if (entityDef.childrenIDs != null)
			entityDef = entityDef.method161();
		if (entityDef == null)
			return;
		if (!entityDef.aBoolean84)
			return;
		String s = entityDef.name;

		if (entityDef.combatGrade != 0)
			s = s + combatDiffColor(myStoner.combatLevel, entityDef.combatGrade) + " <img=11> " + entityDef.combatGrade;
		if (itemSelected == 1)
		{
			menuActionName[menuActionRow] = "Select " + selectedItemName + ", combine with @yel@" + s;
			menuActionID[menuActionRow] = 582;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
			return;
		}
		if (spellSelected == 1)
		{
			if ((spellUsableOn & 2) == 2)
			{
				menuActionName[menuActionRow] = spellTooltip + " @yel@" + s;
				menuActionID[menuActionRow] = 413;
				menuActionCmd1[menuActionRow] = i;
				menuActionCmd2[menuActionRow] = k;
				menuActionCmd3[menuActionRow] = j;
				menuActionRow++;
			}
		}
		else
		{
			if (entityDef.actions != null)
			{
				for (int l = 4; l >= 0; l--)
					if (entityDef.actions[l] != null && !entityDef.actions[l].equalsIgnoreCase("attack"))
					{
						menuActionName[menuActionRow] = entityDef.actions[l] + " @yel@" + s;
						if (l == 0)
							menuActionID[menuActionRow] = 20;
						if (l == 1)
							menuActionID[menuActionRow] = 412;
						if (l == 2)
							menuActionID[menuActionRow] = 225;
						if (l == 3)
							menuActionID[menuActionRow] = 965;
						if (l == 4)
							menuActionID[menuActionRow] = 478;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (entityDef.actions != null)
			{
				for (int i1 = 4; i1 >= 0; i1--)
					if (entityDef.actions[i1] != null && entityDef.actions[i1].equalsIgnoreCase("attack"))
					{
						char c = '\0';
						if (SettingsConfig.entityAttackPriority && entityDef.combatGrade > myStoner.combatLevel)
						{
							c = '\u07D0';
						}
						menuActionName[menuActionRow] = entityDef.actions[i1] + " @yel@" + s;
						if (i1 == 0)
							menuActionID[menuActionRow] = 20 + c;
						if (i1 == 1)
							menuActionID[menuActionRow] = 412 + c;
						if (i1 == 2)
							menuActionID[menuActionRow] = 225 + c;
						if (i1 == 3)
							menuActionID[menuActionRow] = 965 + c;
						if (i1 == 4)
							menuActionID[menuActionRow] = 478 + c;
						menuActionCmd1[menuActionRow] = i;
						menuActionCmd2[menuActionRow] = k;
						menuActionCmd3[menuActionRow] = j;
						menuActionRow++;
					}

			}
			if (EngineConfig.DEBUG_MODE)
			{
				menuActionName[menuActionRow] = "Inspect @yel@" + s + "@whi@(ID: @yel@" + entityDef.interfaceType
					+ "@whi@)";
			}
			else
			{
				menuActionName[menuActionRow] = "Inspect @yel@" + s;
			}
			menuActionID[menuActionRow] = 1025;
			menuActionCmd1[menuActionRow] = i;
			menuActionCmd2[menuActionRow] = k;
			menuActionCmd3[menuActionRow] = j;
			menuActionRow++;
		}
	}

	public static boolean buildStonersListMenu(RSInterface class9)
	{
		int i = class9.contentType;
		if (i >= 1 && i <= 200 || i >= 701 && i <= 900)
		{
			if (i >= 801)
				i -= 701;
			else if (i >= 701)
				i -= 601;
			else if (i >= 101)
				i -= 101;
			else
				i--;
			menuActionName[menuActionRow] = "Socialize with @whi@" + stonersList[i];
			menuActionID[menuActionRow] = 639;
			menuActionRow++;
			return true;
		}
		if (i >= 401 && i <= 500)
		{
			menuActionName[menuActionRow] = "Take out @whi@" + class9.disabledMessage;
			menuActionID[menuActionRow] = 322;
			menuActionRow++;
			return true;
		}
		else
		{
			return false;
		}
	}

}
