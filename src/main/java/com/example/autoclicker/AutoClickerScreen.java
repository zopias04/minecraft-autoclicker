package com.example.autoclicker;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AutoClickerScreen extends Screen {
	private final Screen parent;
	private Button statusLabel;

	public AutoClickerScreen(Screen parent) {
		super(Component.literal("Autoclicker"));
		this.parent = parent;
	}

	private static Component onOffText() {
		return Component.literal("Autoclicker: " + (ClickerConfig.enabled ? "ON" : "OFF"));
	}

	private static Component targetText() {
		return Component.literal("Spams: " + ClickerConfig.TARGET_NAMES[ClickerConfig.targetIndex]);
	}

	private void label(int x, int y, int w, String text) {
		Button l = Button.builder(Component.literal(text), b -> {}).pos(x, y).size(w, 20).build();
		l.active = false; // greyed out, just used as a text label
		this.addRenderableWidget(l);
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int y = this.height / 2 - 80;

		this.addRenderableWidget(Button.builder(onOffText(), b -> {
			ClickerConfig.enabled = !ClickerConfig.enabled;
			b.setMessage(onOffText());
		}).pos(cx - 110, y).size(220, 20).build());
		y += 26;

		this.addRenderableWidget(Button.builder(targetText(), b -> {
			ClickerConfig.targetIndex = (ClickerConfig.targetIndex + 1) % ClickerConfig.TARGET_NAMES.length;
			b.setMessage(targetText());
			ClickerConfig.save();
		}).pos(cx - 110, y).size(220, 20).build());
		y += 30;

		label(cx - 110, y, 220, "Time between clicks (500ms, 1s, 1.5s)");
		y += 22;

		EditBox delayBox = new EditBox(this.font, cx - 110, y, 220, 20, Component.literal("Delay"));
		delayBox.setValue(ClickerConfig.formatDelay());
		delayBox.setResponder(text -> {
			long ms = ClickerConfig.parseDelay(text);
			if (ms > 0) ClickerConfig.delayMs = ms; // applies as soon as the text is valid
		});
		this.addRenderableWidget(delayBox);
		y += 30;

		label(cx - 110, y, 220, "On/Off key: Options > Controls > Key Binds");
		y += 30;

		this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
			.pos(cx - 110, y).size(220, 20).build());
	}

	@Override
	public void onClose() {
		ClickerConfig.save();
		this.minecraft.gui.setScreen(parent);
	}
}
