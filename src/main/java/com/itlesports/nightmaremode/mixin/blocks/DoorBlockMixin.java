package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.DoorBlockWood;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.BlockDoor;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DoorBlockWood.class)
public class DoorBlockMixin extends BlockDoor {
    protected DoorBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        if(NightmareMode.isAprilFools && par1World.rand.nextInt(100) == 0){
            par1World.playSound(par2, par3, par4, "mob.ghast.scream", 4f, 1.0f);
        }
        return super.onBlockActivated(par1World, par2, par3, par4, par5EntityPlayer, par6, par7, par8, par9);
    }
}
