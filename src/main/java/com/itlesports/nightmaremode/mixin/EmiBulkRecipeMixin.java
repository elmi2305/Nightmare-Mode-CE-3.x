package com.itlesports.nightmaremode.mixin;

import btw.crafting.recipe.types.BulkRecipe;
import com.itlesports.nightmaremode.integration.emi.EmiIconHelper;
import com.itlesports.nightmaremode.integration.emi.EmiBulkLayout;
import com.itlesports.nightmaremode.integration.emi.ExpandedBulkRecipeWidgets;
import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import emi.dev.emi.emi.recipe.btw.EmiBulkRecipe;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EmiBulkRecipe.class, remap = false)
public abstract class EmiBulkRecipeMixin implements com.itlesports.nightmaremode.integration.emi.SkillRecipeSource {
    @Override
    public List<SkillNode> nightmareMode$getRequiredSkills() {
        return SkillLockedCrafting.getRequiredSkills(this.nightmareMode$recipe);
    }
    @Shadow(remap = false) @Final protected List<EmiIngredient> input;
    @Shadow(remap = false) @Final protected List<EmiStack> output;
    @Shadow(remap = false) @Final private boolean stoked;
    @Shadow(remap = false) @Final private EmiRecipeCategory category;

    @Unique private BulkRecipe nightmareMode$recipe;

    @Shadow(remap = false) public abstract int getDisplayWidth();

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void captureRecipe(ResourceLocation id, BulkRecipe recipe, boolean stoked, EmiRecipeCategory category, CallbackInfo ci) {
        this.nightmareMode$recipe = recipe;
    }

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void wrapSkillRequirementHeight(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(EmiIconHelper.getSkillRequirementDisplayHeight(
                EmiBulkLayout.baseHeight(this.input.size(), this.output.size()),
                this.getDisplayWidth(),
                SkillLockedCrafting.getRequiredSkills(this.nightmareMode$recipe)));
    }

    @Inject(method = "addWidgets", at = @At("HEAD"), cancellable = true, remap = false)
    private void expandOversizedInputs(WidgetHolder widgets, CallbackInfo ci) {
        if (this.input.size() <= 6) return;
        ExpandedBulkRecipeWidgets.add(widgets, this.input, this.output, this.stoked,
                this.category, (EmiRecipe)(Object)this);
        this.addSkillRequirements(widgets);
        ci.cancel();
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addWrappedSkillRequirements(WidgetHolder widgets, CallbackInfo ci) {
        this.addSkillRequirements(widgets);
    }

    @Unique
    private void addSkillRequirements(WidgetHolder widgets) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(this.nightmareMode$recipe);
        int skillY = EmiBulkLayout.baseHeight(this.input.size(), this.output.size()) + 1;
        EmiIconHelper.addSkillRequirements(widgets, skillY, this.getDisplayWidth(), skills);
    }
}
