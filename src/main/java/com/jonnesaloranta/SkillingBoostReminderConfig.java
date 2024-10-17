package com.jonnesaloranta;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("skillboostreminder")
public interface SkillingBoostReminderConfig extends Config
{
	@ConfigSection(
		name = "Notification",
		description = "Notification settings",
		position = 0
	)
	String notification = "notification";

	@ConfigItem(
			keyName = "remindChat",
			name = "Remind in chat",
			description = "Sends a chat message when you should boost",
			section = "notification",
			position = 0
	)
	default boolean remindChat()
	{
		return true;
	}

	@ConfigItem(
			keyName = "remindNotification",
			name = "Remind with notification",
			description = "Sends a notification per your Runelite settings when you should boost",
			section = "notification",
			position = 1
	)
	default boolean remindNotify()
	{
		return true;
	}

	@ConfigItem(
			keyName = "remindTimer",
			name = "Remind timer in ticks",
			description = "A delay in ticks before reminding again",
			section = "notification",
			position = 2
	)
	// 1 tick = 0.6sec, 50 ticks = 30sec
	default int remindTimer()
	{
		return 50;
	}

	@ConfigItem(
			keyName = "highlightSpecWidget",
			name = "Highlight arrow",
			description = "Highlights the special attack widget with an arrow",
			section = "notification",
			position = 3
	)
	default boolean highlightSpecWidget()
	{
		return true;
	}


	@Alpha
	@ConfigItem(
			keyName = "highlightColor",
			name = "Highlight color",
			description = "Color of the highlight",
			section = "notification",
			position = 4
	)
	default Color highlightColor()
	{
		return Color.GREEN;
	}

	@Range(
			min = 0,
			max = 200
	)
	@ConfigItem(
			keyName = "offsetY",
			name = "Notifier arrow Y Offset",
			description = "Adjusts the blinking arrow position up or down under the special attack button",
			section = "notification",
			position = 5
	)
	default int offsetY()
	{
		return 0;
	}

	@ConfigSection(
			name = "Skills",
			description = "Skill settings",
			position = 1
	)
	String skills = "skills";

	@ConfigItem(
		keyName = "miningBoost",
		name = "Remind mining boost",
		description = "Reminds you to boost when mining",
		section = "skills",
		position = 0
	)
	default boolean mining()
	{
		return true;
	}

	@ConfigItem(
		keyName = "fishingBoost",
		name = "Remind fishing boost",
		description = "Reminds you to boost when fishing",
		section = "skills",
		position = 1
	)
	default boolean fishing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "woodcuttingBoost",
		name = "Remind woodcutting boost",
		description = "Reminds you to boost when woodcutting",
		section = "skills",
		position = 2
	)
	default boolean woodcutting()
	{
		return true;
	}

}
