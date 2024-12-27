package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.OreBlock;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.BlockOre;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin extends BlockOre implements OreBlockAccessor {
    public OreBlockMixin(int iBlockID) {
        super(iBlockID);
    }

    // this gets the proper icons
    @Environment(EnvType.CLIENT)
    @Inject(method = "registerIcons", at = @At("TAIL"))
    private void registerSteelIcons(IconRegister register, CallbackInfo ci){
        if (this.blockID == NMBlocks.steelOre.blockID) {
            this.setIconArray(this.getSteelIconArray(register));
        }
    }

    // icons are in resources/assets/minecraft/textures/blocks
    @Unique
    private Icon[] getSteelIconArray(IconRegister register){
        Icon[] array = new Icon[16];
        array[0] = this.blockIcon;
        array[1] = register.registerIcon(this.getTextureName() + "_strata_2");
        array[2] = register.registerIcon(this.getTextureName() + "_strata_3");
        for (int iTempIndex = 3; iTempIndex < 16; ++iTempIndex) {
            array[iTempIndex] = this.blockIcon;
        }
        return array;
    }
}
