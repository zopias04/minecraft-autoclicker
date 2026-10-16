package com.example.autoclicker;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class AutoClickerMod implements ClientModInitializer {
	public static KeyMapping TOGGLE_KEY;

	private static long nextClickAtMs = 0;
	private static KeyMapping pressedLastTick = null;

	@Override
	public void onInitializeClient() {
		ClickerConfig.load();

		KeyMapping.Category category =
			KeyMapping.Category.register(Identifier.fromNamespaceAndPath("autoclicker", "main"));
		// The on/off key. Change it in Options > Controls > Key Binds > Autoclicker.
		TOGGLE_KEY = KeyMappingHelper.registerKeyMapping(
			new KeyMapping("key.autoclicker.toggle", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, category));

		ClientTickEvents.END_CLIENT_TICK.register(AutoClickerMod::tick);

		// Add the "Autoclicker" button to the top-right of the inventory screens.
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen) {
				Button button = Button.builder(buttonLabel(),
						b -> client.gui.setScreen(new AutoClickerScreen(screen)))
					.pos(scaledWidth - 94, 4)
					.size(90, 20)
					.build();
				Screens.getWidgets(screen).add(button);
			}
		});
	}

	public static Component buttonLabel() {
		return Component.literal("Autoclicker: " + (ClickerConfig.enabled ? "ON" : "OFF"));
	}

	private static void tick(Minecraft mc) {
		// Let go of the key we "pressed" last tick.
		if (pressedLastTick != null) {
			pressedLastTick.setDown(false);
			pressedLastTick = null;
		}

		while (TOGGLE_KEY.consumeClick()) {
			ClickerConfig.enabled = !ClickerConfig.enabled;
			nextClickAtMs = 0;
			if (mc.player != null) {
				mc.player.displayClientMessage(Component.literal(
					"Autoclicker " + (ClickerConfig.enabled ? "ON" : "OFF")), true);
			}
		}

		// Runs inside the game's own tick, so it can only ever click in Minecraft.
		// Extra guard: do nothing unless the game window is focused and no menu is open.
		if (!ClickerConfig.enabled) return;
		if (mc.player == null || mc.level == null) return;
		if (mc.screen != null) return;
		if (!mc.isWindowActive()) return;

		long now = System.nanoTime() / 1_000_000L;
		if (now < nextClickAtMs) return;
		nextClickAtMs = now + ClickerConfig.delayMs;

		KeyMapping target = ClickerConfig.target(mc.options);
		target.setDown(true);
		KeyMapping.click(KeyMappingHelper.getBoundKeyOf(target));
		pressedLastTick = target;
	}
}
