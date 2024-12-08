package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieBreakBarricadeBehavior.class)
public class ZombieBreakBarricadeBehaviorMixin extends EntityAIBase {
    @Shadow protected EntityLiving associatedEntity;

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Inject(method = "shouldBreakBarricadeAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I"),cancellable = true)
    private void manageBlockBreakingForHeldTool(World world, int i, int j, int k, CallbackInfoReturnable<Block> cir){
        if((this.associatedEntity.getHeldItem() != null && this.associatedEntity.getHeldItem().itemID == Item.pickaxeStone.itemID) || NightmareUtils.getIsBloodMoon()){
            int iBlockID = world.getBlockId(i, j, k);
            if (iBlockID != 0) {
                Block block = Block.blocksList[iBlockID];
                if (block.blockID == Block.obsidian.blockID || block.blockID == Block.bedrock.blockID || block.getLocalizedName().contains("portal") || block.blockID == Block.mobSpawner.blockID || block.blockID == BTWBlocks.lavaPillow.blockID) {
                    cir.setReturnValue(null);
                } else if (block.blockMaterial == Material.rock || block.blockMaterial == Material.wood || block.blockMaterial == Material.glass || block.blockMaterial == Material.iron) {
                    cir.setReturnValue(block);
                }
            }
        }
    }
}
