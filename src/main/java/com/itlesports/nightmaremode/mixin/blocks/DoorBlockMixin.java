package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.DoorBlock;
import btw.community.nightmaremode.NightmareMode;
import btw.util.sounds.BTWSoundManager;
import net.minecraft.src.BlockDoor;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DoorBlock.class)
public class DoorBlockMixin extends BlockDoor {
    protected DoorBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "onBlockActivated", at = @At("TAIL"))
    private void chanceToMakeScaryNoiseAprilFools(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9, CallbackInfoReturnable<Boolean> cir){
        if(NightmareMode.isAprilFools && par1World.rand.nextInt(100) == 0){
            par1World.playSound(par2, par3, par4, "mob.ghast.scream", 4f, 1.0f);
        }
    }
}
