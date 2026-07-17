package com.itlesports.nightmaremode.integration.emi;

import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.src.Gui;
import net.minecraft.src.Icon;
import net.minecraft.src.Minecraft;
import net.minecraft.src.TextureMap;

final class EmiIconHelper {
    private EmiIconHelper() {
    }

    public static void addIcon(WidgetHolder widgets, int x, int y, Supplier<Icon> icon, String tooltip) {
        widgets.addDrawable(x, y, 16, 16, (draw, mouseX, mouseY, delta) -> {
            Icon resolved = icon.get();
            if (resolved == null) {
                return;
            }
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            new Gui().drawTexturedModelRectFromIcon(0, 0, resolved, 16, 16);
        }).tooltip((mouseX, mouseY) -> List.of(
                TooltipComponent.of(EmiPort.ordered(EmiPort.literal(tooltip)))));
    }

    public static void addTooltip(WidgetHolder widgets, int x, int y, int width, int height, String tooltip) {
        widgets.addDrawable(x, y, width, height, (draw, mouseX, mouseY, delta) -> {
        }).tooltip((mouseX, mouseY) -> List.of(
                TooltipComponent.of(EmiPort.ordered(EmiPort.literal(tooltip)))));
    }
}
