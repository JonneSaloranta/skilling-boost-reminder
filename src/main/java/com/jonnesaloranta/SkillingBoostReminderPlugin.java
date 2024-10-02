package com.jonnesaloranta;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.jonnesaloranta.enums.BoostItems;
import com.jonnesaloranta.enums.PlayerAnim;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Skilling Boost Reminder"
)
public class SkillingBoostReminderPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private SkillingBoostReminderConfig config;

	@Inject
	private Notifier notifier;

    @Getter
	private final int main_hand_slot = EquipmentInventorySlot.WEAPON.getSlotIdx();

	@Getter
	private int mainHandItemID = -1;

	@Getter
	private boolean canBoost = false;

	@Getter
	private boolean isSkilling = false;

	@Getter
	private int timer = 0;

	@Getter
	private boolean isNotified = false;

	@Getter
	private int specWidgetID = 10485796;



    @Override
	protected void startUp() throws Exception
	{
		log.info("SkillBoostReminder started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("SkillBoostReminder stopped!");
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		final ItemContainer itemContainer = event.getItemContainer();
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId())
		{
            Item[] equippedItems = itemContainer.getItems();
			// get the item in the main hand slot
			final Item mainHandItem = equippedItems[main_hand_slot];
			final int itemID = mainHandItem.getId();
			if (mainHandItemID != itemID)
			{
				mainHandItemID = itemID;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{

		Widget widget = client.getWidget(specWidgetID);

		switch (client.getLocalPlayer().getAnimation())
		{
			case PlayerAnim.ANIM_MINING:
				if(config.mining()){
					isSkilling = true;
				}
				break;
			default:
				isSkilling = false;
				break;
		}


		if (isSkilling) {
			switch (mainHandItemID){
				case BoostItems.DRAGON_PICKAXE:
				case BoostItems.DRAGON_PICKAXE_OR:
				case BoostItems.INFERNAL_PICKAXE:
				case BoostItems.CRYSTAL_PICKAXE:
				case BoostItems.THIRD_AGE_PICKAXE:

				case BoostItems.DRAGON_AXE:
				case BoostItems.DRAGON_AXE_OR:
				case BoostItems.INFERNAL_AXE:
				case BoostItems.CRYSTAL_AXE:
				case BoostItems.THIRD_AGE_AXE:

				case BoostItems.DRAGON_HARPOON:
				case BoostItems.DRAGON_HARPOON_OR:
				case BoostItems.INFERNAL_HARPOON:
					canBoost = canBoost();
					break;
			}
		}

		if(canBoost && !isNotified){
			if(config.remindChat()){
				client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "You can boost with special attack!", null);
			}
			if(config.remindNotify()){
				notifier.notify("You can boost with special attack!");
			}
			isNotified = true;
		}

		if(timer > 0 && canBoost){
			timer--;


			if(timer % 2 == 1){
				if(widget != null){
					widget.setHidden(true);
				}
			}else{
				if(widget != null){
					widget.setHidden(false);
				}
			}
		}

		if(timer <= 0){
			timer = config.remindTimer();
			if(widget != null){
				widget.setHidden(false);
			}
			canBoost = false;
			isNotified = false;
		}
	}

	private boolean canBoost(){

        Widget widget = client.getWidget(specWidgetID);
		if (widget != null)
		{
			String text = widget.getText();
			if (text != null)
			{
				int specialAttack = Integer.parseInt(text);
                return specialAttack == 100;
			}
		}

		return false;
	}

	@Provides
	SkillingBoostReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SkillingBoostReminderConfig.class);
	}
}
