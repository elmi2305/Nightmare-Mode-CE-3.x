package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.skill.SkillLockedCrafting;
import com.itlesports.nightmaremode.skill.SkillNode;
import com.itlesports.nightmaremode.integration.emi.EmiIconHelper;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.src.ResourceLocation;
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
    @Unique private static final int SKILL_ROW_Y = 54;

    @Shadow(remap = false) public abstract ResourceLocation getId();
    @Shadow(remap = false) public abstract int getDisplayWidth();

    @Inject(method = "getDisplayHeight", at = @At("HEAD"), cancellable = true, remap = false)
    private void addHeightForSkillRequirement(CallbackInfoReturnable<Integer> cir) {
        if (!SkillLockedCrafting.getRequiredSkills(this.getId()).isEmpty()) {
            cir.setReturnValue(EmiIconHelper.getSkillRequirementDisplayHeight(
                    SKILL_ROW_Y,
                    this.getDisplayWidth(),
                    SkillLockedCrafting.getRequiredSkills(this.getId())));
        }
    }

    @Inject(method = "addWidgets", at = @At("TAIL"), remap = false)
    private void addSkillRequirement(WidgetHolder widgets, CallbackInfo ci) {
        List<SkillNode> skills = SkillLockedCrafting.getRequiredSkills(this.getId());
        if (skills.isEmpty()) {
            return;
        }

        EmiIconHelper.addSkillRequirements(widgets, SKILL_ROW_Y, this.getDisplayWidth(), skills);
    }
}
