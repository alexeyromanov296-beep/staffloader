package com.example.chatfilter;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatFilterScreen extends Screen {
	private static String lastFilter = "";

	private TextFieldWidget searchField;
	private String filter = lastFilter;
	private int scroll = 0; // сколько строк пропущено снизу (0 = самые новые)
	private int matchCount = 0;

	public ChatFilterScreen() {
		super(Text.literal("Chat Filter"));
	}

	@Override
	protected void init() {
		searchField = new TextFieldWidget(textRenderer, width / 2 - 150, 22, 230, 20, Text.literal("Фильтр"));
		searchField.setMaxLength(100);
		searchField.setPlaceholder(Text.literal("Фильтр (слово, ник, текст)..."));
		searchField.setText(filter);
		searchField.setChangedListener(s -> {
			filter = s;
			lastFilter = s;
			scroll = 0;
		});
		addDrawableChild(searchField);
		setInitialFocus(searchField);

		addDrawableChild(ButtonWidget.builder(Text.literal("Очистить чат"), b -> {
			ChatLog.clear();
			scroll = 0;
		}).dimensions(width / 2 + 85, 22, 65, 20).build());
	}

	private List<OrderedText> buildLines() {
		String f = filter.toLowerCase(Locale.ROOT).trim();
		int wrapWidth = width - 40;
		List<OrderedText> lines = new ArrayList<>();
		matchCount = 0;
		for (Text msg : ChatLog.snapshot()) {
			if (!f.isEmpty() && !msg.getString().toLowerCase(Locale.ROOT).contains(f)) continue;
			matchCount++;
			lines.addAll(textRenderer.wrapLines(msg, wrapWidth));
		}
		return lines;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		List<OrderedText> lines = buildLines();
		int top = 50;
		int bottom = height - 20;
		int lineH = 10;
		int visible = Math.max(1, (bottom - top) / lineH);

		int maxScroll = Math.max(0, lines.size() - visible);
		scroll = Math.max(0, Math.min(scroll, maxScroll));

		context.fill(10, top - 4, width - 10, bottom + 2, 0x90000000);

		int end = lines.size() - scroll;
		int start = Math.max(0, end - visible);
		int y = top;
		for (int i = start; i < end; i++) {
			context.drawTextWithShadow(textRenderer, lines.get(i), 20, y, 0xFFFFFFFF);
			y += lineH;
		}

		context.drawTextWithShadow(textRenderer,
				Text.literal("Найдено сообщений: " + matchCount + "   (колесо мыши — прокрутка, Esc — закрыть)"),
				12, height - 14, 0xFFAAAAAA);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		scroll += (int) Math.signum(verticalAmount) * 3;
		if (scroll < 0) scroll = 0;
		return true;
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
