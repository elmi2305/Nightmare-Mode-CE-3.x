package com.itlesports.nightmaremode.mixin.blocks;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemScythe;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import com.itlesports.nightmaremode.network.PollutionVisualNet;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.util.NMFields.CRIMSON_COLOR;

import java.util.Random;

@Mixin(BlockTallGrass.class)
public class BlockTallGrassMixin extends BlockFlower {
    @Unique private World worldObj;
    @Unique private int yPos;
    @Unique private int xPos;
    @Unique private int zPos;

    protected BlockTallGrassMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }


    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemScythe) {
            return 10.0F;
        }
        if(player instanceof EntityPlayerExt ext){
            if(ext.nightmareMode$doesGrassBreakInstantly()){
                return 10f;
            }
        }
        return 0.1f;
    }

    @Inject(method = "dropBlockAsItemWithChance", at = @At("HEAD"))
    private void dropPlantFiber(World world, int x, int y, int z, int metadata, float chance, int fortuneModifier, CallbackInfo ci) {
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 8.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).tallGrassPlantFiberChanceBonus;
        boolean guaranteed = player != null
                && (SkillHandler.getPlayerData(player).tallGrassAlwaysDropsPlantFiber
                || player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemScythe);
        if (!world.isRemote && (guaranteed || world.rand.nextFloat() <= 0.08F + bonus)) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.plantFiber));
        }
    }
    @Inject(method = "updateTick", at = @At("HEAD"), cancellable = true)
    private void declareVariables(World world, int i, int j, int k, Random rand, CallbackInfo ci){
        if (!world.isRemote && rand.nextFloat() < ChunkPollutionManager.getVegetationDecayChance(
                ChunkPollutionManager.get(world, i, k))) {
            world.setBlockToAir(i, j, k);
            ci.cancel();
            return;
        }
        this.xPos = i;
        this.yPos = j;
        this.zPos = k;
        this.worldObj = world;
    }
    @ModifyArg(method = "updateTick", at = @At(
            value = "INVOKE",
            target = "Ljava/util/Random;nextInt(I)I",
            ordinal = 0
    ), index = 0
    )
    private int boostGrassSpreadWithCorrectFertilizer(int bound) {
        if(this.worldObj == null || (this.xPos == 0 && this.yPos == 0 && this.zPos == 0)) return bound;
        return ChunkAttributeManager.hasEffectiveFertilizer(this.worldObj, this.xPos,this.yPos,this.zPos, (Block)(Object)this)
                ? 1
                : bound;
    }

    @Redirect(method = "updateTick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;setBlockAndMetadataWithNotify(IIIII)Z")
    )
    private boolean consumeResourcesForGrassSpread(
            World world,
            int x,
            int y,
            int z,
            int blockId,
            int metadata
    ) {
        if (!ChunkAttributeManager.canGrow(world, x, z, (Block)(Object)this)) {
            return false;
        }
        boolean changed = world.setBlockAndMetadataWithNotify(x, y, z, blockId, metadata);
        if (changed) {
            ChunkAttributeManager.consumeForGrowth(world, x, z, (Block)(Object)this);
        }
        return changed;
    }

    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getBlockColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass0(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "getRenderColor", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass1(CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
        }
    }
    @Environment(value= EnvType.CLIENT)
    @Inject(method = "colorMultiplier", at = @At(value = "RETURN"), cancellable = true)
    private void redGrass2(IBlockAccess blockAccess, int x, int y, int z, CallbackInfoReturnable<Integer> cir){
        if(NightmareMode.crimson){
            cir.setReturnValue(CRIMSON_COLOR);
            return;
        }
        cir.setReturnValue(PollutionVisualNet.tintColor(cir.getReturnValue(), x, z));
    }
}
