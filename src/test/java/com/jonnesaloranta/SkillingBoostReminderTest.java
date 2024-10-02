package com.jonnesaloranta;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class SkillingBoostReminderTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(SkillingBoostReminderPlugin.class);
		RuneLite.main(args);
	}
}