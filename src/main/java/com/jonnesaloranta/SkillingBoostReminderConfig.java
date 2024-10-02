package com.jonnesaloranta;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

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
			description = "Remind in chat",
			section = "notification",
			position = 0
	)
	default boolean remindChat()
	{
		return true;
	}

	@ConfigItem(
			keyName = "remindFlash",
			name = "Remind with flash",
			description = "Remind with flash",
			section = "notification",
			position = 1
	)
	default boolean remindNotify()
	{
		return true;
	}

	@ConfigItem(
			keyName = "remindTimer",
			name = "Remind timer",
			description = "Remind timer",
			section = "notification",
			position = 2
	)
	default int remindTimer()
	{
		return 5;
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
		description = "Remind about mining boost",
		section = "skills",
		position = 0
	)
	default boolean mining()
	{
		return true;
	}

}
