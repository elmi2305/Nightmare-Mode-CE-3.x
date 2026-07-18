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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EmiCraftingRecipe.class, remap = false)
public abstract class EmiCraftingRecipeMixin {
    @Shadow(remap = false) public abstract ResourceLocation getId();

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void addHeightForSkillRequirement(CallbackInfoReturnable<Integer> cir) {
        if (!SkillLockedCrafting.getRequiredSkills(this.getId()).isEmpty()) {
            cir.setReturnValue(72);
        }
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addSkillRequirement(WidgetHolder widgets, CallbackInfo ci) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(this.getId());
        int x = 0;
        for (SkillNode skill : skills) {
            EmiStack icon = EmiStack.of(skill.icon);
            int iconX = x;
            widgets.addDrawable(iconX, 54, 18, 18, (draw, mouseX, mouseY, delta) -> {
                Icon background = NMFields.ICON_SKILL_REQUIREMENT_BACKGROUND;
                if (background != null) {
                    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                    Minecraft.getMinecraft().ingameGUI.drawTexturedModelRectFromIcon(0, 0, background, 18, 18);
                }
                icon.render(draw, 1, 1, delta);
            }).tooltip((mouseX, mouseY) -> List.of(
                    TooltipComponent.of(EmiPort.ordered(EmiPort.literal("Required: " + skill.name)))));
            x += 19;
        }
    }
}
