package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.util.NMFields;
import emi.dev.emi.emi.EmiPort;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.src.Gui;
import net.minecraft.src.Icon;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.TextureMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EmiCraftingRecipe.class, remap = false)
public abstract class EmiCraftingRecipeMixin {
    @Unique private static final int SKILL_ICON_SIZE = 16;
    @Unique private static final int SKILL_BACKGROUND_SIZE = 20;
    @Unique private static final int SKILL_ICON_PADDING = (SKILL_BACKGROUND_SIZE - SKILL_ICON_SIZE) / 2;
    @Unique private static final int SKILL_ICON_SPACING = 21;
    @Unique private static final int SKILL_ROW_Y = 54;

    @Shadow(remap = false) public abstract ResourceLocation getId();

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void addHeightForSkillRequirement(CallbackInfoReturnable<Integer> cir) {
        if (!SkillLockedCrafting.getRequiredSkills(this.getId()).isEmpty()) {
            cir.setReturnValue(SKILL_ROW_Y + SKILL_BACKGROUND_SIZE);
        }
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addSkillRequirement(WidgetHolder widgets, CallbackInfo ci) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(this.getId());
        if (skills.isEmpty()) {
            return;
        }

        List<EmiStack> icons = skills.stream()
                .map(skill -> EmiStack.of(skill.icon))
                .toList();

        int totalWidth = skills.size() * SKILL_ICON_SPACING - 1;
        widgets.addDrawable(0, SKILL_ROW_Y, totalWidth, SKILL_BACKGROUND_SIZE, (draw, mouseX, mouseY, delta) -> {
            for (int index = 0; index < icons.size(); ++index) {
                int iconX = index * SKILL_ICON_SPACING;
                Icon background = NMFields.ICON_SKILL_REQUIREMENT_BACKGROUND;
                if (background != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    new Gui().drawTexturedModelRectFromIcon(
                            iconX,
                            0,
                            background,
                            SKILL_BACKGROUND_SIZE,
                            SKILL_BACKGROUND_SIZE);
                }
                icons.get(index).render(draw, iconX + SKILL_ICON_PADDING, SKILL_ICON_PADDING, delta);
            }
        });

        for (int index = 0; index < skills.size(); ++index) {
            SkillNode skill = skills.get(index);
            int iconX = index * SKILL_ICON_SPACING;
            widgets.addDrawable(iconX, SKILL_ROW_Y, SKILL_BACKGROUND_SIZE, SKILL_BACKGROUND_SIZE, (draw, mouseX, mouseY, delta) -> {
            }).tooltip((mouseX, mouseY) -> List.of(
                    TooltipComponent.of(EmiPort.ordered(EmiPort.literal("Required skill: " + skill.name))),
                    TooltipComponent.of(EmiPort.ordered(EmiPort.literal("Condition: " + skill.requirementText)))));
        }
    }
}
