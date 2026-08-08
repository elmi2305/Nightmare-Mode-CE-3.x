package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.types.BulkRecipe;
import com.itlesports.nightmaremode.integration.emi.EmiIconHelper;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.recipe.btw.EmiBulkRecipe;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EmiBulkRecipe.class, remap = false)
public abstract class EmiBulkRecipeMixin {
    private static final int BASE_HEIGHT = 52;
    private static final int SKILL_ROW_Y = 53;

    @Unique private BulkRecipe nightmareMode$recipe;

    @Shadow(remap = false) public abstract int getDisplayWidth();

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void captureRecipe(ResourceLocation id, BulkRecipe recipe, boolean stoked, EmiRecipeCategory category, CallbackInfo ci) {
        this.nightmareMode$recipe = recipe;
    }

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void wrapSkillRequirementHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EmiIconHelper.getSkillRequirementDisplayHeight(
                BASE_HEIGHT,
                this.getDisplayWidth(),
                SkillLockedCrafting.getRequiredSkills(this.nightmareMode$recipe)));
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addWrappedSkillRequirements(WidgetHolder widgets, CallbackInfo ci) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(this.nightmareMode$recipe);
        EmiIconHelper.addSkillRequirements(widgets, SKILL_ROW_Y, this.getDisplayWidth(), skills);
    }
}
