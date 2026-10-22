package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.integration.emi.EmiIconHelper;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.recipe.btw.EmiCampfireRecipe;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EmiCampfireRecipe.class, remap = false)
public abstract class EmiCampfireRecipeMixin {
    @Shadow public abstract ResourceLocation getId();
    @Shadow public abstract int getDisplayWidth();

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true)
    private void showSkillRowHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EmiIconHelper.getSkillRequirementDisplayHeight(
                38, getDisplayWidth(), SkillLockedCrafting.getRequiredSkills(getId())));
    }

    @Inject(method = "addWidgets", at = @At("TAIL"))
    private void showRequiredSkills(WidgetHolder widgets, CallbackInfo ci) {
        EmiIconHelper.addSkillRequirements(widgets, 39, getDisplayWidth(), SkillLockedCrafting.getRequiredSkills(getId()));
    }
}
