package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.entity.mob.behavior.ZombieBreakBarricadeBehavior;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(ZombieBreakBarricadeBehavior.class)
public class ZombieBreakBarricadeBehaviorMixin extends EntityAIBase {
    @Shadow protected EntityLiving associatedEntity;

    @Override
    public boolean shouldExecute() {
        return false;
    }

    @Inject(method = "shouldBreakBarricadeAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;getBlockId(III)I"),cancellable = true)
    private void manageBlockBreakingForHeldTool(World world, int i, int j, int k, boolean advancedBreaker, CallbackInfoReturnable<Block> cir){
        if((this.associatedEntity.getHeldItem() != null && this.associatedEntity.getHeldItem().itemID == Item.pickaxeStone.itemID) || NMUtils.getIsBloodMoon()){
            int iBlockID = world.getBlockId(i, j, k);
            if (iBlockID != 0) {
                Block block = Block.blocksList[iBlockID];
                if (getAvoidedBlocks().contains(iBlockID)) {
                    System.out.println("Encountered avoided block: " + block.getLocalizedName());
                    cir.setReturnValue(null);
                    return;
                }
                if (block.blockMaterial == Material.rock || block.blockMaterial == Material.wood || block.blockMaterial == Material.glass || block.blockMaterial == Material.iron) {
                    cir.setReturnValue(block);
                }
            }
        }
    }

    @Unique private static Set<Integer> AVOIDED_BLOCKS = new HashSet<>();
    @Unique private static Set<Integer> getAvoidedBlocks(){
        if(AVOIDED_BLOCKS.isEmpty()){
            AVOIDED_BLOCKS.add(Block.obsidian.blockID);
            AVOIDED_BLOCKS.add(Block.bedrock.blockID);
            AVOIDED_BLOCKS.add(Block.portal.blockID);
            AVOIDED_BLOCKS.add(Block.endPortal.blockID);
            AVOIDED_BLOCKS.add(Block.mobSpawner.blockID);
            AVOIDED_BLOCKS.add(BTWBlocks.lavaPillow.blockID);
        }
        return AVOIDED_BLOCKS;
    }
}
