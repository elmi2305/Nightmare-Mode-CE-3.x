package com.itlesports.nightmaremode.block.blocks;

import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemHammer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

import java.util.Arrays;
import java.util.Random;

public class BlockIronBloom extends Block {
    private static final int HITS_TO_FINISH = 8;

    @Environment(EnvType.CLIENT)
    private Icon[] icons;

    public BlockIronBloom(int blockID) {
        super(blockID, Material.rock);
        this.setHardness(0.6f);
        this.setResistance(2.0f);
        this.setStepSound(Block.soundStoneFootstep);
        this.setCreativeTab(CreativeTabs.tabMaterials);
        this.setUnlocalizedName("ifhyIronBloom");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return super.canPlaceBlockAt(world, x, y, z) && world.doesBlockHaveSolidTopSurface(x, y - 1, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        return this.tryHammerBloom(world, x, y, z, player);
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        this.tryHammerBloom(world, x, y, z, player);
    }

    private boolean tryHammerBloom(World world, int x, int y, int z, EntityPlayer player) {
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null || !(heldStack.getItem() instanceof ItemHammer)) {
            return false;
        }

        if (!world.isRemote) {
            int metadata = world.getBlockMetadata(x, y, z);
            heldStack.damageItem(1, player);
            if (heldStack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }

            world.playAuxSFX(2001, x, y, z, this.blockID + (metadata << 12));
            if (metadata >= HITS_TO_FINISH - 1) {
                world.setBlockToAir(x, y, z);
                world.spawnEntityInWorld(new FloatingItemEntity(world, x + 0.5, y + 0.25, z + 0.5, new ItemStack(BTWItems.ironNugget)));
            } else {
                world.setBlockMetadataWithNotify(x, y, z, metadata + 1);
            }
        }

        return true;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
    }

    @Override
    public void setBlockBoundsForItemRender() {
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        return this.getBloomBounds();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int itemDamage) {
        return this.getBloomBounds();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        return renderer.renderStandardBlock(this, x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
        return this.currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
    }

    private AxisAlignedBB getBloomBounds() {
        return AxisAlignedBB.getAABBPool().getAABB(0.1875, 0.0, 0.1875, 0.8125, 0.25, 0.8125);
    }

    @Override
    public int idDropped(int metadata, Random random, int fortuneModifier) {
        return NMItems.ironBloom.itemID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.icons[Math.min(metadata, this.icons.length - 1)];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[HITS_TO_FINISH];
        Icon bloomIcon = register.registerIcon("nightmare:ifhyIronBloom");
        Arrays.fill(this.icons, bloomIcon);
        this.blockIcon = this.icons[0];
    }
}
