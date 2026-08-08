package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.integration.emi.EmiIconHelper;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.recipe.btw.EmiSoulforgeRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EmiSoulforgeRecipe.class, remap = false)
public abstract class EmiSoulforgeRecipeMixin {
    private static final int BASE_HEIGHT = 73;
    private static final int SKILL_ROW_Y = 74;

    @Shadow(remap = false) public abstract int getDisplayWidth();

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void wrapSkillRequirementHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EmiIconHelper.getSkillRequirementDisplayHeight(
                BASE_HEIGHT,
                this.getDisplayWidth(),
                SkillLockedCrafting.getRequiredSkills(((EmiRecipe)(Object)this).getId())));
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addWrappedSkillRequirements(WidgetHolder widgets, CallbackInfo ci) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(((EmiRecipe)(Object)this).getId());
        EmiIconHelper.addSkillRequirements(widgets, SKILL_ROW_Y, this.getDisplayWidth(), skills);
    }
}
