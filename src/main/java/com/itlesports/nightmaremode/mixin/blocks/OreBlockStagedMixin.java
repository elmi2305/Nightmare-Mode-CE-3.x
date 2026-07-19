package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.OreBlock;
import api.block.blocks.OreBlockStaged;
import api.item.items.PickaxeItem;
import btw.block.blocks.RoughStoneBlock;
import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OreBlockStaged.class)
public class OreBlockStagedMixin extends OreBlock {
    public OreBlockStagedMixin(int iBlockID) {
        super(iBlockID);
    }

    @Inject(method = "convertBlock", at = @At("HEAD"), cancellable = true)
    private void convertBlock(ItemStack stack, World world, int x, int y, int z, int side, CallbackInfoReturnable<Boolean> cir){

        if(stack == null) return;

        int blockID = this.blockID;
        int iOldMetadata = world.getBlockMetadata(x, y, z);
        int iStrata = this.getStrata(iOldMetadata);
        EntityPlayer closestPlayer = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 8.0D);
        if (iStrata == 2 && closestPlayer != null && !SkillHandler.getPlayerData(closestPlayer).canMineStrataThreeOre) {
            if (!world.isRemote) {
                SkillHandler.sendStatus(closestPlayer, "Requires skill: Blackstone Authority - Bring 64 blackstone.");
            }
            cir.setReturnValue(false);
            return;
        }
        if(blockID == Block.oreCoal.blockID){
            if(stack.getItem() instanceof PickaxeItem pi){
                int dropCount = pi.toolMaterial.getHarvestLevel();
                for(int i = 0; i < dropCount && world.rand.nextBoolean(); i++){
                    summonEntity(world,x,y,z, BTWItems.coalDust);
                }
            }

            world.setBlockAndMetadataWithNotify(x, y, z, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 4);
            cir.setReturnValue(true);
            return;
        } else
        if(blockID == Block.oreIron.blockID){
            if(stack.getItem() instanceof PickaxeItem pi){
                int dropCount = pi.toolMaterial.getHarvestLevel() - 1;
                for(int i = 0; i < dropCount; i++){
                    summonEntity(world,x,y,z, BTWItems.ironOrePile);
                }
            } else if(stack.getItem() instanceof ChiselItem ch){
                float pileChance = 0.10F + SkillHandler.getWorldData(world).globalIronPileChanceBonus
                        + (closestPlayer == null ? 0.0F : SkillHandler.getPlayerData(closestPlayer).ironPileChanceBonus);
                summonEntity(world,x,y,z,
                        world.rand.nextFloat() < pileChance
                                ? BTWItems.ironOrePile : (world.rand.nextBoolean()
                                                          ? BTWItems.gravelPile : (world.rand.nextInt(3) == 0
                                                                                   ? BTWItems.sharpStone
                                                                                   : BTWItems.stone)));
            }

            world.setBlockAndMetadataWithNotify(x, y, z, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 4);
            cir.setReturnValue(true);
            return;
        }
        if (this.blockID == Block.oreDiamond.blockID) {
            if (closestPlayer != null && !SkillHandler.canHarvestDiamondOre(closestPlayer)) {
                if (!world.isRemote) {
                    SkillHandler.sendStatus(closestPlayer, "Requires all five Diamond Extraction contributions.");
                }
                cir.setReturnValue(false);
                return;
            }

            float rockChance = 0.90F + (closestPlayer == null ? 0.0F : SkillHandler.getPlayerData(closestPlayer).diamondRockDropChanceBonus);
            if (!world.isRemote && world.rand.nextFloat() <= Math.min(1.0F, rockChance)) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.diamondBearingRock));
            }
            world.setBlockAndMetadataWithNotify(x, y, z, RoughStoneBlock.strataLevelBlockArray[iStrata].blockID, 4);
            cir.setReturnValue(true);
            return;

        }
    }

    @Unique
    private static void summonEntity(World world, int x, int y, int z, Item item){
        int meta = 0;
        if(world.isRemote) return;
        if(item.itemID == BTWItems.sharpStone.itemID){
            meta = world.rand.nextInt(3) + 3;
        }
        world.spawnEntityInWorld(new FloatingItemEntity(world, x, y, z, new ItemStack(item, 1, meta)));
    }
}
