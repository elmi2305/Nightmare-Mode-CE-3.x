package com.itlesports.nightmaremode.block.blocks;

import api.item.items.PickaxeItem;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.OreNodeTileEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import java.util.Random;

public class BlockOreNode extends BlockContainer {
    private final int droppedItemId;
    private final Block requiredToolBlock;
    private final int requiredDrillTier;

    public BlockOreNode(int id, int droppedItemId, Block requiredToolBlock, String name, String texture) {
        this(id, droppedItemId, requiredToolBlock, 1, name, texture);
    }

    public BlockOreNode(int id, int droppedItemId, Block requiredToolBlock, int requiredDrillTier, String name, String texture) {
        super(id, BTWBlocks.netherRockMaterial);
        this.droppedItemId = droppedItemId;
        this.requiredToolBlock = requiredToolBlock;
        this.requiredDrillTier = Math.max(1, requiredDrillTier);
        this.setHardness(3.0F);
        this.setResistance(20.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName(name);
        this.setTextureName(texture);
    }

    public int getRequiredDrillTier() {
        return this.requiredDrillTier;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new OreNodeTileEntity();
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof OreNodeTileEntity node) {
            node.initializeCapacity();
        }
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return 2;
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess world, int x, int y, int z) {
        return 3;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ItemStack held = player.getCurrentEquippedItem();
        if (!this.isValidMiningTool(held, world, x, y, z)) {
            return 0.0F;
        }
        float normalSpeed = player.getCurrentPlayerStrVsBlock(this, x, y, z) / this.blockHardness / 30.0F;
        return held.getItem() == NMItems.tungstenPickaxe ? normalSpeed / 8.0F : normalSpeed;
    }

    public boolean isValidMiningTool(ItemStack held, World world, int x, int y, int z) {
        if (held == null) {
            return false;
        }
        if (held.getItem() == NMItems.tungstenPickaxe) {
            return true;
        }
        return held.getItem() instanceof PickaxeItem pickaxe
                && pickaxe.canHarvestBlock(held, world, this.requiredToolBlock, x, y, z);
    }

    public void mineNode(World world, EntityPlayer player, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (!(tileEntity instanceof OreNodeTileEntity node)) {
            return;
        }

        this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.droppedItemId, 1, 0));
        player.addStat(net.minecraft.src.StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, 0);
        if (node.consumeOne() <= 0) {
            world.setBlockToAir(x, y, z);
        } else {
            world.markBlockForUpdate(x, y, z);
        }
    }

    public ItemStack mineNodeByMachine(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (!(tileEntity instanceof OreNodeTileEntity node)) {
            return null;
        }
        ItemStack result = new ItemStack(this.droppedItemId, 1, 0);
        if (node.consumeOne() <= 0) {
            world.setBlockToAir(x, y, z);
        } else {
            world.markBlockForUpdate(x, y, z);
        }
        return result;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }
}
