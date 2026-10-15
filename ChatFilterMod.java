package com.example.chatfilter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ChatFilterMod implements ClientModInitializer {
	private static KeyBinding openKey;

	@Override
	public void onInitializeClient() {
		openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.chatfilter.open",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_K,
				"category.chatfilter"));

		// Сообщения игроков
		ClientReceiveMessageEvents.CHAT.register((message, signed, sender, params, time) -> ChatLog.add(message));
		// Системные сообщения (смерти, команды, плагины сервера и т.д.)
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
			if (!overlay) ChatLog.add(message);
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openKey.wasPressed()) {
				if (client.currentScreen == null) {
					client.setScreen(new ChatFilterScreen());
				}
			}
		});
	}
}
