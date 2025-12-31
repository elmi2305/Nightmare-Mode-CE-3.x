package com.itlesports.nightmaremode.block.blocks;

import api.item.items.HoeItem;
import api.item.util.ItemUtils;
import api.world.difficulty.DifficultyParam;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockUnderGrass extends NMBlock {
    @Environment(EnvType.CLIENT)
    private Icon iconGrassTop;
    @Environment(EnvType.CLIENT)
    private Icon iconEveryOtherSide;

    public BlockUnderGrass(int par1, Material par2Material) {
        super(par1, par2Material);
        this.setHardness(0.6F);
        this.setShovelsEffectiveOn();
        this.setHoesEffectiveOn();
        this.setStepSound(BTWBlocks.grassStepSound);
        this.setUnlocalizedName("nmUnderGrass");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setTickRandomly(true);
    }

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("dirt");
        this.iconEveryOtherSide = register.registerIcon("blight_level_4_side");
        this.iconGrassTop = register.registerIcon("blight_level_4_top");
    }

    public boolean canEndermenPickUpBlock(World world, int x, int y, int z) {
        return true;
    }

    protected ItemStack createStackedBlock(int metadata) {
        return this.isSparse(metadata) ? new ItemStack(Block.dirt) : new ItemStack(this);
    }

    public int idDropped(int metadata, Random rand, int fortuneModifier) {
        return BTWBlocks.looseDirt.blockID;
    }

    public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int metadata, float chanceOfDrop) {
        this.dropItemsIndividually(world, x, y, z, BTWItems.dirtPile.itemID, 6, 0, chanceOfDrop);
        return true;
    }

    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        super.onBlockDestroyedWithImproperTool(world, player, x, y, z, metadata);
        this.onDirtDugWithImproperTool(world, x, y, z);
    }

    public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
        super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
        this.onDirtDugWithImproperTool(world, x, y, z);
    }

    protected void onNeighborDirtDugWithImproperTool(World world, int x, int y, int z, int toFacing) {
        if (world.getDifficultyParameter(DifficultyParam.ShouldGrassLoosenWhenDigging.class) && toFacing == 0) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        }

    }

    public boolean canBePistonShoveled(World world, int x, int y, int z) {
        return true;
    }

    public boolean canBeGrazedOn(IBlockAccess blockAccess, int x, int y, int z, EntityAnimal animal) {
        return !this.isSparse(blockAccess, x, y, z) || animal.isStarving() || animal.getDisruptsEarthOnGraze();
    }

    public void onGrazed(World world, int x, int y, int z, EntityAnimal animal) {
        if (!animal.getDisruptsEarthOnGraze()) {
            if (this.isSparse(world, x, y, z)) {
                world.setBlockWithNotify(x, y, z, Block.dirt.blockID);
            } else {
                this.setSparse(world, x, y, z);
            }
        } else {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseSparseGrass.blockID);
            this.notifyNeighborsBlockDisrupted(world, x, y, z);
        }

    }

    public void onVegetationAboveGrazed(World world, int x, int y, int z, EntityAnimal animal) {
        if (animal.getDisruptsEarthOnGraze()) {
            world.setBlockWithNotify(x, y, z, BTWBlocks.looseSparseGrass.blockID);
            this.notifyNeighborsBlockDisrupted(world, x, y, z);
        }

    }

    public boolean canReedsGrowOnBlock(World world, int x, int y, int z) {
        return true;
    }

    public boolean canSaplingsGrowOnBlock(World world, int x, int y, int z) {
        return true;
    }

    public boolean canWildVegetationGrowOnBlock(World world, int x, int y, int z) {
        return true;
    }

    public boolean getCanBlightSpreadToBlock(World world, int x, int y, int z, int blightLevel) {
        return true;
    }

    public boolean canConvertBlock(ItemStack stack, World world, int x, int y, int z) {
        return stack != null && stack.getItem() instanceof HoeItem;
    }

    public boolean convertBlock(ItemStack stack, World world, int x, int y, int z, int fromSide) {
        world.setBlockWithNotify(x, y, z, BTWBlocks.looseDirt.blockID);
        if (!world.isRemote) {
            world.playAuxSFX(2291, x, y, z, 0);
            if (world.rand.nextInt(10) == 0) {
                ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), fromSide);
            }
        }

        return true;
    }

    public boolean shouldPlayStandardConvertSound(World world, int x, int y, int z) {
        return false;
    }

    public boolean getCanGrassSpreadToBlock(World world, int x, int y, int z) {
        return this.isSparse(world, x, y, z);
    }

    public boolean spreadGrassToBlock(World world, int x, int y, int z) {
        if (this.isSparse(world, x, y, z)) {
            this.setFullyGrown(world, x, y, z);
            return true;
        } else {
            return false;
        }
    }

    public boolean isBreakableBarricade(World world, int i, int j, int k, boolean advancedBreaker) {
        return advancedBreaker;
    }

    public boolean isSparse(IBlockAccess blockAccess, int x, int y, int z) {
        return this.isSparse(blockAccess.getBlockMetadata(x, y, z));
    }

    public boolean isSparse(int metadata) {
        return metadata == 1;
    }

    public void setSparse(World world, int x, int y, int z) {
        world.setBlockMetadataWithNotify(x, y, z, 1);
    }

    public void setFullyGrown(World world, int x, int y, int z) {
        world.setBlockMetadataWithNotify(x, y, z, 0);
    }

    public boolean onCreativeBonemealApplied(World world, int x, int y, int z) {
        if (!world.isRemote) {
            Random rand = world.rand;

            for(int i = 0; i < 128; ++i) {
                int dx = x;
                int dy = y + 1;
                int dz = z;

                for(int j = 0; j < i / 16; ++j) {
                    dx += rand.nextInt(3) - 1;
                    dy += (rand.nextInt(3) - 1) * rand.nextInt(3) / 2;
                    dz += rand.nextInt(3) - 1;
                    if (world.getBlockId(dx, dy - 1, dz) != Block.grass.blockID || world.isBlockNormalCube(dx, dy, dz)) {
                        break;
                    }
                }

                if (world.getBlockId(dx, dy, dz) == 0) {
                    if (rand.nextInt(10) != 0) {
                        if (Block.tallGrass.canBlockStay(world, dx, dy, dz)) {
                            world.setBlock(dx, dy, dz, Block.tallGrass.blockID, 1, 3);
                        }
                    } else if (rand.nextInt(3) != 0) {
                        if (Block.plantYellow.canBlockStay(world, dx, dy, dz)) {
                            world.setBlock(dx, dy, dz, Block.plantYellow.blockID);
                        }
                    } else if (Block.plantRed.canBlockStay(world, dx, dy, dz)) {
                        world.setBlock(dx, dy, dz, Block.plantRed.blockID);
                    }
                }
            }
        }

        return true;
    }



    @Environment(EnvType.CLIENT)
    public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
        return 16777215;
    }


    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        if (side == 1) {
            return this.iconGrassTop;
        } else if (side > 1) {
            return this.iconEveryOtherSide;
        } else {
            return Block.dirt.blockIcon;
        }

    }

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
        render.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        return render.renderStandardBlock(this, x, y, z);
    }

    @Environment(EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks render, int x, int y, int z, boolean firstPassResult) {
        render.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        render.renderStandardBlock(this, x, y, z);
    }

    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par1 == 1 ? this.iconGrassTop : (par1 == 0 ? this.blockIcon : this.iconEveryOtherSide);
    }
}
