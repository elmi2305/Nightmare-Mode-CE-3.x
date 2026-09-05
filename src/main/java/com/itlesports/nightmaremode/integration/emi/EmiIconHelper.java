package com.itlesports.nightmaremode.integration.emi;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.SkillNet;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.skill.gui.GuiSkillTree;
import com.itlesports.nightmaremode.util.NMFields;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.DrawableWidget;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.src.Gui;
import net.minecraft.src.Icon;
import net.minecraft.src.Minecraft;
import net.minecraft.src.TextureMap;
import org.lwjgl.opengl.GL11;

public final class EmiIconHelper {
    private static final int SKILL_ICON_SIZE = 16;
    private static final int SKILL_BACKGROUND_SIZE = 20;
    private static final int SKILL_PADDING = (SKILL_BACKGROUND_SIZE - SKILL_ICON_SIZE) / 2;
    private static final int SKILL_SPACING = 21;

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

    public static int getSkillRequirementDisplayHeight(
            int contentHeight,
            int displayWidth,
            List<SkillNode> skills) {
        if (skills.isEmpty()) {
            return contentHeight;
        }
        return contentHeight + 1 + getSkillRows(displayWidth, skills.size()) * SKILL_SPACING;
    }

    public static void addSkillRequirements(
            WidgetHolder widgets,
            int startY,
            int displayWidth,
            List<SkillNode> skills) {
        if (skills.isEmpty()) {
            return;
        }

        int columns = getSkillColumns(displayWidth);
        int rows = getSkillRows(displayWidth, skills.size());
        List<EmiStack> icons = skills.stream().map(skill -> EmiStack.of(skill.icon)).toList();

        widgets.addDrawable(0, startY, displayWidth, rows * SKILL_SPACING, (draw, mouseX, mouseY, delta) -> {
            for (int index = 0; index < icons.size(); ++index) {
                int iconX = index % columns * SKILL_SPACING;
                int iconY = index / columns * SKILL_SPACING;
                Icon background = NMFields.ICON_SKILL_REQUIREMENT_BACKGROUND;
                if (background != null) {
                    boolean unlocked = SkillHandler.isUnlocked(Minecraft.getMinecraft().thePlayer, skills.get(index));
                    GL11.glColor4f(unlocked ? 0.45F : 1.0F, 1.0F, unlocked ? 0.55F : 1.0F, 1.0F);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    new Gui().drawTexturedModelRectFromIcon(
                            iconX,
                            iconY,
                            background,
                            SKILL_BACKGROUND_SIZE,
                            SKILL_BACKGROUND_SIZE);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
                icons.get(index).render(draw, iconX + SKILL_PADDING, iconY + SKILL_PADDING, delta);
            }
        });

        for (int index = 0; index < skills.size(); ++index) {
            SkillNode skill = skills.get(index);
            int iconX = index % columns * SKILL_SPACING;
            int iconY = startY + index / columns * SKILL_SPACING;
            widgets.add(new DrawableWidget(
                    iconX,
                    iconY,
                    SKILL_BACKGROUND_SIZE,
                    SKILL_BACKGROUND_SIZE,
                    (draw, mouseX, mouseY, delta) -> {
                    }) {
                @Override
                public boolean mouseClicked(int mouseX, int mouseY, int button) {
                    Minecraft mc = Minecraft.getMinecraft();
                    if (button != 0 || mc.thePlayer == null
                            || SkillHandler.isUnlocked(mc.thePlayer, skill)
                            || NMItems.skillBook == null
                            || !mc.thePlayer.inventory.hasItem(NMItems.skillBook.itemID)) {
                        return false;
                    }
                    SkillNet.sendSyncRequest();
                    mc.displayGuiScreen(new GuiSkillTree(mc.currentScreen, skill));
                    return true;
                }
            }).tooltip((mouseX, mouseY) -> List.of(
                            TooltipComponent.of(EmiPort.ordered(EmiPort.literal(
                                    "Required skill: " + skill.name))),
                            TooltipComponent.of(EmiPort.ordered(EmiPort.literal(
                                    "Condition: " + skill.requirementText)))));
        }
    }

    private static int getSkillColumns(int displayWidth) {
        return Math.max(1, (displayWidth + 1) / SKILL_SPACING);
    }

    private static int getSkillRows(int displayWidth, int skillCount) {
        int columns = getSkillColumns(displayWidth);
        return (skillCount + columns - 1) / columns;
    }
}
