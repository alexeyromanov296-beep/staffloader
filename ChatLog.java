package com.example.chatfilter;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class ChatLog {
	private static final int MAX = 2000;
	private static final List<Text> MESSAGES = new ArrayList<>();

	private ChatLog() {}

	public static synchronized void add(Text message) {
		MESSAGES.add(message);
		if (MESSAGES.size() > MAX) MESSAGES.remove(0);
	}

	public static synchronized void clear() {
		MESSAGES.clear();
	}

	public static synchronized List<Text> snapshot() {
		return new ArrayList<>(MESSAGES);
	}
}
