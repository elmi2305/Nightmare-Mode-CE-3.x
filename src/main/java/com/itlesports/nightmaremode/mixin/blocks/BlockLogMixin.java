package com.itlesports.nightmaremode.mixin.blocks;

import api.item.items.AxeItem;
import api.item.items.ToolItem;
import btw.community.nightmaremode.NightmareMode;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

import static com.itlesports.nightmaremode.util.NMFields.PREHARDMODE;

@Mixin(BlockLog.class)
public abstract class BlockLogMixin extends BlockRotatedPillar {
    @Shadow public abstract boolean getIsStump(int iMetadata);

    protected BlockLogMixin(int i, Material material) {
        super(i, material);
    }

    @Override
    public boolean canConvertBlock(ItemStack stack, World world, int i, int j, int k) {
        return stack != null && (stack.itemID == NMItems.sharpTwig.itemID || stack.itemID == NMItems.sharpBarkTwig.itemID || stack.getItem() instanceof ChiselItem || stack.getItem() instanceof AxeItem);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        if(player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemTool || player.getHeldItem().getItem() instanceof ToolItem)){
            return 0.0F;
        }
        return super.getPlayerRelativeBlockHardness(player, world, i, j, k);
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        // drops nothing
        return true;
    }

    public boolean isFallingBlock() {
        return NightmareMode.noSkybases || super.isFallingBlock();
    }

    public void onBlockAdded(World world, int i, int j, int k) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onBlockAdded(world,i,j,k);
    }

    public void onNeighborBlockChange(World world, int i, int j, int k, int iNeighborBlockID) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.scheduleCheckForFall(world, i, j, k);
        }
        super.onNeighborBlockChange(world,i,j,k,iNeighborBlockID);
    }

    public void updateTick(World world, int i, int j, int k, Random rand) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) {
            this.checkForFall(world, i, j, k);
        }
        super.updateTick(world,i,j,k,rand);
    }

    public int tickRate(World par1World) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(par1World)) {
            return 4;
        }
        return super.tickRate(par1World);
    }

    @Override
    public boolean canSupportLeaves(IBlockAccess blockAccess, int x, int y, int z) {
        int iMetadata = blockAccess.getBlockMetadata(x, y, z);
        return super.canSupportLeaves(blockAccess, x, y, z) && !this.getIsStump(iMetadata);
    }

    @Override
    public boolean isBreakableBarricade(World world, int i, int j, int k, boolean adv) {
        return true;
    }
}
