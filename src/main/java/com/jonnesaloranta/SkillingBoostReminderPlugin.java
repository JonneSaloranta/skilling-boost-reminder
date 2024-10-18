package com.jonnesaloranta;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.jonnesaloranta.enums.BoostItems;
import com.jonnesaloranta.enums.PlayerAnim;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = "Skilling Boost Reminder",
		description = "Reminds you to use special attack to boost skilling",
		tags = {"skilling", "boost", "reminder", "special attack", "spec", "woodcutting", "fishing", "mining"}
)
public class SkillingBoostReminderPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private SkillingBoostReminderConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private SkillingBoostReminderOverlay overlay;

	@Getter
	private final int mainHandSlot = EquipmentInventorySlot.WEAPON.getSlotIdx();

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

	private final int specTextWidgetID = 10485796;

	private String reminderMessage = "You can boost with special attack!";

	@Override
	protected void startUp() throws Exception {
		log.info("SkillBoostReminder started!");
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		log.info("SkillBoostReminder stopped!");
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event) {
		if (event.getContainerId() == InventoryID.EQUIPMENT.getId()) {
			Item mainHandItem = event.getItemContainer().getItems()[mainHandSlot];
			int itemID = mainHandItem.getId();
			if (mainHandItemID != itemID) {
				mainHandItemID = itemID;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		int animID = client.getLocalPlayer().getAnimation();
		updateSkillingStatus(animID);
		updateBoostStatus();

		if (isSkilling && canBoost && !isNotified) {
			notifyBoost();
		}

		handleWidgetVisibility();
	}

	private void updateSkillingStatus(int animID) {
		switch (animID) {
			case PlayerAnim.ANIM_MINING:
				isSkilling = config.mining();
				break;
			case PlayerAnim.ANIM_FISHING:
			case PlayerAnim.ANIM_FISHING_1:
			case PlayerAnim.ANIM_FISHING_2:
			case PlayerAnim.ANIM_FISHING_3:
			case PlayerAnim.ANIM_FISHING_4:
			case PlayerAnim.ANIM_FISHING_5:
			case PlayerAnim.ANIM_FISHING_6:
				isSkilling = config.fishing();
				break;
			case PlayerAnim.ANIM_WOODCUTTING:
			case PlayerAnim.ANIM_WOODCUTTING_FELLING_AXE:
				isSkilling = config.woodcutting();
				break;
			default:
				isSkilling = false;
				break;
		}
	}


	private void updateBoostStatus() {
		if (isSkilling && isBoostableItem(mainHandItemID)) {
			canBoost = canBoost();
		}
	}

	private boolean isBoostableItem(int itemID) {
		switch (itemID) {
			case BoostItems.DRAGON_PICKAXE:
			case BoostItems.DRAGON_PICKAXE_OR:
			case BoostItems.DRAGON_FELLING_AXE:
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
				return true;
			default:
				return false;
		}
	}

	private void notifyBoost() {
		if (config.remindChat()) {
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", reminderMessage, null);
		}
		if (config.remindNotify()) {
			notifier.notify(reminderMessage);
		}
		isNotified = true;
	}

	private void handleWidgetVisibility() {
		Widget widget = client.getWidget(specTextWidgetID);
		if (timer > 0 && canBoost) {
			timer--;
			if (widget != null) {
				overlay.setHighlighted(timer % 2 == 1);
			}
		} else if (timer <= 0) {
			resetTimer(widget);
		}
	}

	private void resetTimer(Widget widget) {
		timer = config.remindTimer();
		canBoost = false;
		overlay.setHighlighted(false);
		isNotified = false;
	}

	private boolean canBoost() {
		Widget widget = client.getWidget(specTextWidgetID);
		if (widget != null) {
			String text = widget.getText();
			return text != null && Integer.parseInt(text) == 100;
		}
		return false;
	}

	@Provides
	SkillingBoostReminderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SkillingBoostReminderConfig.class);
	}
}
