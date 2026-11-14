package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.crafting.recipe.types.FishingRodUpgradeRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EnumChatFormatting;
import net.minecraft.src.I18n;
import net.minecraft.src.ItemFishingRod;
import net.minecraft.src.ItemStack;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class FishingAttachmentTooltip {
    private static final String[][] ATTACHMENTS = {
            {"IfhyFishingBell", "ifhyFishingBellUpgrade"},
            {"IfhyFishingLure", "ifhyFishingLureUpgrade"},
            {"IfhyFishingAutoReel", "ifhyFishingAutoReelUpgrade"},
            {"IfhyRareFishLure", "ifhyRareFishLureUpgrade"},
            {"IfhyTwinHook", "ifhyTwinHookUpgrade"},
            {"IfhyDepthWinch", "ifhyDepthWinchUpgrade"},
            {"IfhyWideHook", "ifhyWideHookUpgrade"}
    };

    private FishingAttachmentTooltip() {}

    public static void addNames(ItemStack stack, List<String> tooltip) {
        if (!(stack.getItem() instanceof ItemFishingRod)) {
            for (String[] attachment : ATTACHMENTS) {
                if (stack.getItem().getUnlocalizedName().equals("item." + attachment[1]))
                    tooltip.add(EnumChatFormatting.GRAY + I18n.getString("item." + attachment[1] + ".tooltip"));
            }
            return;
        }
        for (String[] attachment : ATTACHMENTS) {
            if (FishingRodUpgradeRecipe.hasUpgrade(stack, attachment[0]))
                tooltip.add(EnumChatFormatting.GRAY + I18n.getString("item." + attachment[1] + ".name"));
        }
    }
}
