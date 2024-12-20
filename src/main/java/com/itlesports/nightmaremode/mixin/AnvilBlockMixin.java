package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.AnvilBlock;
import btw.world.util.WorldUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class AnvilBlockMixin extends Block {

    protected AnvilBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @Inject(method = "onBlockActivated", at = @At("HEAD"), cancellable = true)
    private void vanillaAnvilFunctionality(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick, CallbackInfoReturnable<Boolean> cir){
        if (!world.isRemote && !WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(world, i, j + 1, k, 0) && player instanceof EntityPlayerMP) {
            player.displayGUIAnvil(i,j,k);
//            ContainerRepair container = new ContainerRepair(player.inventory, world, i, j, k, player);
//            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player, container, BTWContainers.anvilContainerID);
        }
        cir.setReturnValue(true);
    }
//    @Override
//    @Environment(value= EnvType.CLIENT)
//    public void registerIcons(IconRegister register) {
//        this.blockIcon = register.registerIcon("nmAnvil");
//    }
}
