package com.itlesports.nightmaremode.mixin.gui;

import com.itlesports.nightmaremode.crafting.BrewingStandRecipeHelper;
import net.minecraft.src.GuiBrewingStand;
import net.minecraft.src.TileEntityBrewingStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiBrewingStand.class)
public class GuiBrewingStandMixin {
    @Shadow private TileEntityBrewingStand brewingStand;

    @ModifyConstant(method = "drawGuiContainerBackgroundLayer", constant = @Constant(floatValue = 400.0F))
    private float reduceBrewTimeVisual(float constant){
        return BrewingStandRecipeHelper.getBrewTime(this.brewingStand);
    }
}
