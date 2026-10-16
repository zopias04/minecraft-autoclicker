package com.example.autoclicker;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;

/** Settings, saved to config/autoclicker.properties. */
public final class ClickerConfig {
	public static final long MIN_DELAY_MS = 50; // the game ticks every 50 ms, so this is the fastest possible

	public static final String[] TARGET_NAMES = {
		"Left click (attack / mine)", "Right click (use / place)", "Jump", "Sneak", "Sprint", "Drop item", "Swap hands"
	};

	public static boolean enabled = false; // never starts ON
	public static int targetIndex = 0;
	public static long delayMs = 1000;

	private ClickerConfig() {}

	public static KeyMapping target(Options o) {
		return switch (targetIndex) {
			case 1 -> o.keyUse;
			case 2 -> o.keyJump;
			case 3 -> o.keyShift;
			case 4 -> o.keySprint;
			case 5 -> o.keyDrop;
			case 6 -> o.keySwapOffhand;
			default -> o.keyAttack;
		};
	}

	/** Accepts "500", "500ms", "1.5s", "1s". Returns -1 if it can't be read. */
	public static long parseDelay(String text) {
		try {
			String s = text.trim().toLowerCase(Locale.ROOT).replace(',', '.');
			double v;
			if (s.endsWith("ms")) v = Double.parseDouble(s.substring(0, s.length() - 2).trim());
			else if (s.endsWith("s")) v = Double.parseDouble(s.substring(0, s.length() - 1).trim()) * 1000.0;
			else v = Double.parseDouble(s);
			if (Double.isNaN(v) || v <= 0) return -1;
			return Math.max(MIN_DELAY_MS, Math.round(v));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static String formatDelay() {
		return delayMs + "ms";
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("autoclicker.properties");
	}

	public static void load() {
		Path f = file();
		if (!Files.exists(f)) return;
		try (Reader r = Files.newBufferedReader(f)) {
			Properties p = new Properties();
			p.load(r);
			targetIndex = Math.floorMod(Integer.parseInt(p.getProperty("target", "0")), TARGET_NAMES.length);
			long d = parseDelay(p.getProperty("delayMs", "1000"));
			if (d > 0) delayMs = d;
		} catch (IOException | NumberFormatException ignored) {
		}
	}

	public static void save() {
		Properties p = new Properties();
		p.setProperty("target", Integer.toString(targetIndex));
		p.setProperty("delayMs", Long.toString(delayMs));
		try (Writer w = Files.newBufferedWriter(file())) {
			p.store(w, "Simple Autoclicker");
		} catch (IOException ignored) {
		}
	}
}
