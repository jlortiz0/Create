package com.simibubi.create.content.logistics.trains.management.schedule.destination;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public abstract class TextScheduleInstruction extends ScheduleInstruction {

	protected String getLabelText() {
		return textData("Text");
	}

	@Override
	public List<Component> getTitleAs(String type) {
		return ImmutableList.of(Lang.translate("schedule." + type + "." + getId().getPath() + ".summary")
			.withStyle(ChatFormatting.GOLD), Lang.translate("generic.in_quotes", new TextComponent(getLabelText())));
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void initConfigurationWidgets(ModularGuiLineBuilder builder) {
		builder.addTextInput(0, 121, (e, t) -> modifyEditBox(e), "Text");
	}

	@Environment(EnvType.CLIENT)
	protected void modifyEditBox(EditBox box) {}

}