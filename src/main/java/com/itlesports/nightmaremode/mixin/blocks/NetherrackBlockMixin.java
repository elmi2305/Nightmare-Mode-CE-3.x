package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.FullBlock;
import api.world.difficulty.DifficultyParam;
import btw.block.blocks.NetherrackBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.items.bloodItems.ItemBloodPickaxe;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(NetherrackBlock.class)
public class NetherrackBlockMixin extends FullBlock {
    @Unique private boolean shouldDropDust = true;

    protected NetherrackBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void setHardnessOfBlock(int par1, CallbackInfo ci){
        this.setHardness(6f);
    }

    @Override
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6) {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 4;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 4;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemBloodPickaxe) {
            float fRelativeHardness = player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness;
            int count = NMUtils.getBloodArmorWornCount(player);
            float armorMult = count > 0 ? ((float) count / 4 + 1): 1.0f;
            return fRelativeHardness / (200.0f * world.getDifficultyParameter(DifficultyParam.NoToolBlockHardnessMultiplier.class) * armorMult);
        }

        return super.getPlayerRelativeBlockHardness(player, world, i, j, k);
    }

    @Override
    protected void dropItemsIndividually(World world, int i, int j, int k, int iIDDropped, int iPileCount, int iDamageDropped, float fChanceOfPileDrop) {
        if (this.shouldDropDust) {
            for (int iTempCount = 0; iTempCount < iPileCount; ++iTempCount) {
                ItemStack stack = new ItemStack(BTWItems.groundNetherrack, 1, iDamageDropped);
                this.dropBlockAsItem_do(world, i, j, k, stack);
            }
        }
    }

    @Override
    public void dropItemsOnDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
        if (!world.isRemote && this.shouldDropDust) {
            this.dropItemsIndividually(world, i, j, k, BTWItems.groundNetherrack.itemID, 4, 0, 0.75f);
        }
    }

    @Override
    public float getExplosionResistance(Entity explosionEntity) {
        if(explosionEntity instanceof EntityTNTPrimed){
            this.shouldDropDust = true;
            return 2f;
        }
        this.shouldDropDust = false;
        return 4f;
    }
}
