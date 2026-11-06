package com.itlesports.nightmaremode.mixin.blocks;

import api.item.items.AxeItem;
import btw.block.blocks.ChewedLogBlock;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import org.spongepowered.asm.mixin.Mixin;


import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ChewedLogBlock.class)
public abstract class ChewedLogBlockMixin extends Block {

    protected ChewedLogBlockMixin(int id, Material material) {
        super(id, material);
    }

    @ModifyArg(method = "convertBlock", at = @At(value = "INVOKE", target = "Lapi/item/util/ItemUtils;ejectStackFromBlockTowardsFacing(Lnet/minecraft/src/World;IIILnet/minecraft/src/ItemStack;I)V", ordinal = 2), index = 4)
    private ItemStack aVoid(ItemStack stack){
        return new ItemStack(NMItems.woodClump, 1, 119);
    }
    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            return 0.0F;
        }

        Item item = heldItem.getItem();
        if (item instanceof ChiselItem || item instanceof AxeItem || item.itemID == NMItems.sharpBarkTwig.itemID || item.itemID == NMItems.sharpTwig.itemID) {
            return super.getPlayerRelativeBlockHardness(player, world, i, j, k);
        }

        return 0.0F;
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        return true;
    }

    @Override
    public boolean isFallingBlock() {
        return NightmareMode.noSkybases || super.isFallingBlock();
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) this.scheduleCheckForFall(world, x, y, z);
    }

    @Inject(method = "onNeighborBlockChange", at = @At("HEAD"), cancellable = true)
    private void scheduleChewedLogFall(World world, int x, int y, int z, int neighbor, CallbackInfo ci) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world) && this.canFallIntoBlockAtPos(world, x, y - 1, z)) {
            this.scheduleCheckForFall(world, x, y, z);
            ci.cancel();
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, java.util.Random random) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(world)) this.checkForFall(world, x, y, z);
        super.updateTick(world, x, y, z, random);
    }

    @Override
    protected boolean checkForFall(World world, int x, int y, int z) {
        if (world.isRemote || y < 0 || !this.canFallIntoBlockAtPos(world, x, y - 1, z)) return false;
        int metadata = world.getBlockMetadata(x, y, z);
        if (world.checkChunksExist(x - 32, y - 32, z - 32, x + 32, y + 32, z + 32)) {
            world.spawnEntityInWorld(new EntityFallingSand(world, x + 0.5D, y + 0.5D, z + 0.5D, this.blockID, metadata));
        } else {
            world.setBlockToAir(x, y, z);
            while (y > 0 && this.canFallIntoBlockAtPos(world, x, y - 1, z)) --y;
            if (y > 0) world.setBlock(x, y, z, this.blockID, metadata, 3);
        }
        return true;
    }

    @Override
    public int tickRate(World par1World) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(par1World)) {
            return 4;
        }
        return super.tickRate(par1World);
    }

}
