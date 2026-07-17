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


@Mixin(ChewedLogBlock.class)
public abstract class ChewedLogBlockMixin extends Block {

    protected ChewedLogBlockMixin(int id, Material material) {
        super(id, material);
    }

    @ModifyArg(method = "convertBlock", at = @At(value = "INVOKE", target = "Lapi/item/util/ItemUtils;ejectStackFromBlockTowardsFacing(Lnet/minecraft/src/World;IIILnet/minecraft/src/ItemStack;I)V", ordinal = 2), index = 4)
    private ItemStack aVoid(ItemStack stack){
        return new ItemStack(NMItems.woodClump, 1, 199);
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
    public int tickRate(World par1World) {
        if (NMUtils.shouldWoodBlocksHaveSkybaseGravity(par1World)) {
            return 4;
        }
        return super.tickRate(par1World);
    }

}
