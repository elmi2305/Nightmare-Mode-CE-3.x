package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.GroundCoverBlock;
import api.item.items.ShovelItem;
import api.item.util.ItemUtils;
import btw.block.blocks.SnowCoverBlock;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.StatList;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Random;

@Mixin(SnowCoverBlock.class)
public abstract class SnowCoverBlockMixin extends GroundCoverBlock {
    protected SnowCoverBlockMixin(int id) {
        super(id, Material.snow);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getHeldItem();
        int dropItemId = NMItems.snowPile.itemID;
        int amount = 1;

        if (held != null && (held.getItem() instanceof ShovelItem shovel)) {
            dropItemId = Item.snowball.itemID;
            if (shovel.toolMaterial.getHarvestLevel() >= 4) {
                amount = 2;
            }
        }

        this.dropBlockAsItem_do(world, x, y, z, new ItemStack(dropItemId, amount, 0));
        world.setBlockToAir(x, y, z);
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return NMItems.snowPile.itemID;
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ShovelItem shovel) {
            int amount = shovel.toolMaterial.getHarvestLevel() >= 4 ? 2 : 1;
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Item.snowball, amount, 0));
            return;
        }

        this.dropBlockAsItem(world, x,y,z, metadata, 0);
    }

    @Override
    public boolean convertBlock(ItemStack stack, World world, int i, int j, int k, int iFromSide) {
        int dropItemId = NMItems.snowPile.itemID;
        int amount = 1;

        if (stack != null && stack.getItem() instanceof ShovelItem shovel) {
            dropItemId = Item.snowball.itemID;
            if (shovel.toolMaterial.getHarvestLevel() >= 4) {
                amount = 2;
            }
        }

        int iMetadata = world.getBlockMetadata(i, j, k);
        if (!world.isRemote && iMetadata > 1) {
            world.setBlockMetadataWithNotify(i, j, k, iMetadata - 2);
            world.playAuxSFX(2001, i, j, k, this.blockID);
            ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(dropItemId, amount, 0), iFromSide);
        } else if (!world.isRemote) {
            world.setBlockToAir(i, j, k);
            ItemUtils.ejectStackFromBlockTowardsFacing(world, i, j, k, new ItemStack(dropItemId, amount, 0), iFromSide);
        }
        return true;
    }
    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        System.out.println("hi");
        return super.dropComponentItemsOnBadBreak(world, i, j, k, iMetadata, fChanceOfDrop);
    }
}
