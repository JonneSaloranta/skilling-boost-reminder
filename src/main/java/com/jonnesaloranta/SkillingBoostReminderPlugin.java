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
	private ConfigManager configManager;

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

		checkConfigValues();
	}

	private void checkConfigValues() {
		if(config.offsetX() > 999 || config.offsetX() < -999){
			configManager.setConfiguration("notification", "offsetX", 0);
		}

		if(config.offsetY() > 999 || config.offsetY() < -999){
			configManager.setConfiguration("notification", "offsetY", 0);
		}

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
			case PlayerAnim.ANIM_MINING1:
			case PlayerAnim.ANIM_MINING2:
			case PlayerAnim.ANIM_MINING3:
			case PlayerAnim.ANIM_MINING4:
			case PlayerAnim.ANIM_MINING5:
			case PlayerAnim.ANIM_MINING6:
			case PlayerAnim.ANIM_MINING7:
			case PlayerAnim.ANIM_MINING8:
			case PlayerAnim.ANIM_MINING9:
			case PlayerAnim.ANIM_MINING10:
			case PlayerAnim.ANIM_MINING11:
			case PlayerAnim.ANIM_MINING12:
			case PlayerAnim.ANIM_MINING13:
			case PlayerAnim.ANIM_MINING14:
			case PlayerAnim.ANIM_MINING15:
			case PlayerAnim.ANIM_MINING16:
			case PlayerAnim.ANIM_MINING17:
			case PlayerAnim.ANIM_MINING18:
			case PlayerAnim.ANIM_MINING19:
			case PlayerAnim.ANIM_MINING20:
			case PlayerAnim.ANIM_MINING21:
				isSkilling = config.mining();
				break;
			case PlayerAnim.ANIM_FISHING1:
			case PlayerAnim.ANIM_FISHING2:
			case PlayerAnim.ANIM_FISHING3:
			case PlayerAnim.ANIM_FISHING4:
			case PlayerAnim.ANIM_FISHING5:
			case PlayerAnim.ANIM_FISHING6:
			case PlayerAnim.ANIM_FISHING7:
			case PlayerAnim.ANIM_FISHING8:
			case PlayerAnim.ANIM_FISHING9:
			case PlayerAnim.ANIM_FISHING10:
			case PlayerAnim.ANIM_FISHING11:
			case PlayerAnim.ANIM_FISHING12:
			case PlayerAnim.ANIM_FISHING13:
			case PlayerAnim.ANIM_FISHING14:
			case PlayerAnim.ANIM_FISHING15:
			case PlayerAnim.ANIM_FISHING16:
			case PlayerAnim.ANIM_FISHING17:
				isSkilling = config.fishing();
				break;
			case PlayerAnim.ANIM_WOODCUTTING1:
			case PlayerAnim.ANIM_WOODCUTTING2:
			case PlayerAnim.ANIM_WOODCUTTING3:
			case PlayerAnim.ANIM_WOODCUTTING4:
			case PlayerAnim.ANIM_WOODCUTTING5:
			case PlayerAnim.ANIM_WOODCUTTING6:
			case PlayerAnim.ANIM_WOODCUTTING7:
			case PlayerAnim.ANIM_WOODCUTTING8:
			case PlayerAnim.ANIM_WOODCUTTING9:
			case PlayerAnim.ANIM_WOODCUTTING10:
			case PlayerAnim.ANIM_WOODCUTTING11:
			case PlayerAnim.ANIM_WOODCUTTING12:
			case PlayerAnim.ANIM_WOODCUTTING13:
			case PlayerAnim.ANIM_WOODCUTTING14:
			case PlayerAnim.ANIM_WOODCUTTING15:
			case PlayerAnim.ANIM_WOODCUTTING16:
			case PlayerAnim.ANIM_WOODCUTTING17:
			case PlayerAnim.ANIM_WOODCUTTING18:
			case PlayerAnim.ANIM_WOODCUTTING19:
			case PlayerAnim.ANIM_WOODCUTTING20:
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

		if(canBoost){
			if (timer > 0) {
				timer--;
				if (widget != null) {
					overlay.setHighlighted(timer % 2 == 1);
				}
			} else {
				resetTimer();
			}
		}else{
			resetTimer();
		}
	}

	private void resetTimer() {
		timer = config.remindTimer();
		canBoost = false;
		overlay.setHighlighted(false);
		isNotified = false;
	}

	private boolean canBoost() {
		Widget widget = client.getWidget(specTextWidgetID);
		if (widget != null) {
			String text = widget.getText();
			try {
				return text != null && Integer.parseInt(text) == 100;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}

	@Provides
	SkillingBoostReminderConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(SkillingBoostReminderConfig.class);
	}
}
