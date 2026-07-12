package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockLog;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.World;

public class BlockSapTap extends Block {
    @Environment(EnvType.CLIENT)
    private Icon[] icons;

    public BlockSapTap(int blockID) {
        super(blockID, Material.wood);
        this.setHardness(0.2f);
        this.setStepSound(Block.soundWoodFootstep);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhySapTap");
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
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
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int facing) {
        return this.getAttachedLogType(world, x, y, z, facing) >= 0;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.canPlaceBlockOnSide(world, x, y, z, 2)
                || this.canPlaceBlockOnSide(world, x, y, z, 3)
                || this.canPlaceBlockOnSide(world, x, y, z, 4)
                || this.canPlaceBlockOnSide(world, x, y, z, 5);
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int facing, float clickX, float clickY, float clickZ, int metadata) {
        int logType = this.getAttachedLogType(world, x, y, z, facing);
        if (logType < 0) {
            facing = this.getFirstValidFacing(world, x, y, z);
            logType = this.getAttachedLogType(world, x, y, z, facing);
        }

        if (logType < 0) {
            logType = 0;
        }

        return logType | (this.getAttachmentIndexForFacing(facing) << 2);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborBlockID) {
        if (!this.hasAttachedLog(world, x, y, z)) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int facing, float clickX, float clickY, float clickZ) {
        ItemStack heldStack = player.getHeldItem();
        if (heldStack == null || heldStack.itemID != NMItems.woodCup.itemID) {
            return false;
        }

        if (!world.isRemote) {
            if (!player.capabilities.isCreativeMode) {
                --heldStack.stackSize;
                if (heldStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }

            ItemStack sapStack = new ItemStack(NMItems.cupOfSap, 1, this.getLogType(world.getBlockMetadata(x, y, z)));
            if (!player.inventory.addItemStackToInventory(sapStack)) {
                player.dropPlayerItem(sapStack);
            }

            world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "random.pop", 0.25f, 1.0f);
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
        return this.getTapBounds(blockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getBlockBoundsFromPoolForItemRender(int itemDamage) {
        return this.getItemRenderBounds();
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

    private AxisAlignedBB getItemRenderBounds() {
        return AxisAlignedBB.getAABBPool().getAABB(0.25, 0.25, 0.3125, 0.75, 0.75, 0.6875);
    }

    private AxisAlignedBB getTapBounds(int metadata) {
        int attachment = this.getAttachmentFromMetadata(metadata);
        float min = 0.25f;
        float max = 0.75f;
        float depth = 0.1875f;

        if (attachment == 1) {
            return AxisAlignedBB.getAABBPool().getAABB(0.0f, min, min, depth, max, max);
        } else if (attachment == 2) {
            return AxisAlignedBB.getAABBPool().getAABB(1.0f - depth, min, min, 1.0f, max, max);
        } else if (attachment == 3) {
            return AxisAlignedBB.getAABBPool().getAABB(min, min, 0.0f, max, max, depth);
        }

        return AxisAlignedBB.getAABBPool().getAABB(min, min, 1.0f - depth, max, max, 1.0f);
    }

    private boolean hasAttachedLog(World world, int x, int y, int z) {
        int placementFacing = this.getPlacementFacingFromAttachment(this.getAttachmentFromMetadata(world.getBlockMetadata(x, y, z)));
        return this.getAttachedLogType(world, x, y, z, placementFacing) >= 0;
    }

    private int getFirstValidFacing(World world, int x, int y, int z) {
        for (int facing = 2; facing <= 5; ++facing) {
            if (this.getAttachedLogType(world, x, y, z, facing) >= 0) {
                return facing;
            }
        }
        return 2;
    }

    private int getAttachedLogType(World world, int x, int y, int z, int facing) {
        int logX = x;
        int logZ = z;

        if (facing == 2) {
            ++logZ;
        } else if (facing == 3) {
            --logZ;
        } else if (facing == 4) {
            ++logX;
        } else if (facing == 5) {
            --logX;
        } else {
            return -1;
        }

        if (world.getBlockId(logX, y, logZ) == Block.wood.blockID) {
            return BlockLog.limitToValidMetadata(world.getBlockMetadata(logX, y, logZ));
        }

        return -1;
    }

    private int getAttachmentIndexForFacing(int facing) {
        if (facing == 5) {
            return 0;
        }
        if (facing == 4) {
            return 1;
        }
        if (facing == 3) {
            return 2;
        }
        return 3;
    }

    private int getAttachmentFromMetadata(int metadata) {
        return ((metadata >> 2) & 3) + 1;
    }

    private int getPlacementFacingFromAttachment(int attachment) {
        if (attachment == 1) {
            return 5;
        }
        if (attachment == 2) {
            return 4;
        }
        if (attachment == 3) {
            return 3;
        }
        return 2;
    }

    private int getLogType(int metadata) {
        return metadata & 3;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.icons[this.getLogType(metadata)];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.icons = new Icon[BlockLog.woodType.length];
        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = register.registerIcon("nightmare:ifhySapTap_" + BlockLog.woodType[i]);
        }
    }
}
