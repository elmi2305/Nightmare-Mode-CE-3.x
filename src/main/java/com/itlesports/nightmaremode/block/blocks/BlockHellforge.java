package com.itlesports.nightmaremode.block.blocks;


import btw.block.BTWBlocks;
import btw.block.model.BlockModel;
import btw.block.model.OvenModel;
import btw.world.util.BlockPos;
import btw.world.util.WorldUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.HellforgeTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

import static com.itlesports.nightmaremode.block.tileEntities.HellforgeTileEntity.FUEL_MAP;

public class BlockHellforge
        extends BlockFurnace {
    protected final BlockModel modelBlockInterior = new OvenModel();
    protected final float clickYTopPortion = 0.375f;
    protected final float clickYBottomPortion = 0.375f;
    @Environment(value = EnvType.CLIENT)
    private Icon[] fuelOverlays;
    @Environment(value = EnvType.CLIENT)
    private Icon currentFuelOverlay;
    @Environment(value = EnvType.CLIENT)
    private Icon blankOverlay;
    @Environment(value = EnvType.CLIENT)
    protected boolean isRenderingInterior;
    @Environment(value = EnvType.CLIENT)
    private int interiorBrightness;

    public BlockHellforge(int iBlockID, boolean bIsLit) {
        super(iBlockID, bIsLit);
        this.setPicksEffectiveOn();
        this.setHardness(2.0f);
        this.setResistance(3.33f);
        this.setStepSound(BTWBlocks.clayBrickStepSound);
        this.setUnlocalizedName("nmHellforgeBlock");
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new HellforgeTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        int iItemDamage;
        Item item;
        int iMetadata = world.getBlockMetadata(i, j, k);
        int iBlockFacing = iMetadata & 7;
        if (iBlockFacing != iFacing) {
            return false;
        }
        ItemStack heldStack = player.getCurrentEquippedItem();
        HellforgeTileEntity tileEntity = (HellforgeTileEntity) world.getBlockTileEntity(i, j, k);
        ItemStack cookStack = tileEntity.getCookStack();
        if (fYClick > 0.375f) {
            if (cookStack != null) {
                tileEntity.givePlayerCookStack(player, iFacing);
                return true;
            }
            if (heldStack != null && this.isValidCookItem(heldStack)) {
                if (!world.isRemote) {
                    tileEntity.addCookStack(new ItemStack(heldStack.itemID, 1, heldStack.getItemDamage()));
                }
                --heldStack.stackSize;
                return true;
            }
        } else if (fYClick < 0.375f && heldStack != null && ((item = heldStack.getItem()).getCanBeFedDirectlyIntoBrickOven(iItemDamage = heldStack.getItemDamage()) || FUEL_MAP.containsKey(heldStack.itemID))) {
            int iItemsConsumed;
            if (!world.isRemote && (iItemsConsumed = tileEntity.attemptToAddFuel(heldStack)) > 0) {
                if (this.isActive) {
                    world.playSoundEffect((double) i + 0.5, (double) j + 0.5, (double) k + 0.5, "mob.ghast.fireball", 0.2f + world.rand.nextFloat() * 0.1f, world.rand.nextFloat() * 0.25f + 1.25f);
                } else {
                    world.playSoundEffect((double) i + 0.5, (double) j + 0.5, (double) k + 0.5, "random.pop", 0.25f, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                }
                heldStack.stackSize -= iItemsConsumed;
            }
            return true;
        }
        return false;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 4 + rand.nextInt(6);
    }

    @Override
    public int idDropped(int iMetaData, Random random, int iFortuneModifier) {
        return Item.netherrackBrick.itemID;
    }

    @Override
    protected boolean canSilkHarvest(int iMetadata) {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7) {
        if (!par1World.isRemote) {
            this.dropBlockAsItem_do(par1World, par2, par3, par4, new ItemStack(NMBlocks.hellforge));
        }
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int i, int j, int k, int iMetadata) {
        this.dropBlockAsItem(world, i, j, k, iMetadata, 0);
    }

    @Override
    public boolean canPlaceBlockAt(World world, int i, int j, int k) {
        if (!WorldUtils.doesBlockHaveSolidTopSurface(world, i, j - 1, k)) {
            return false;
        }
        return super.canPlaceBlockAt(world, i, j, k);
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID) {
        if (!WorldUtils.doesBlockHaveSolidTopSurface(world, i, j - 1, k)) {
            this.dropBlockAsItem(world, i, j, k, world.getBlockMetadata(i, j, k), 0);
            world.setBlockWithNotify(i, j, k, 0);
        }
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
        int iBlockFacing = blockAccess.getBlockMetadata(i, j, k) & 7;
        return iBlockFacing != iFacing;
    }

    @Override
    public void updateFurnaceBlockState(boolean bBurning, World world, int i, int j, int k, boolean bHasContents) {
        int iMetadata = world.getBlockMetadata(i, j, k);
        TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
        keepFurnaceInventory = true;
        world.setBlock(i, j, k, NMBlocks.hellforge.blockID);
        keepFurnaceInventory = false;
        iMetadata = !bHasContents ? (iMetadata &= 7) : (iMetadata |= 8);
        world.SetBlockMetadataWithNotify(i, j, k, iMetadata, 2);
        if (tileEntity != null) {
            tileEntity.validate();
            world.setBlockTileEntity(i, j, k, tileEntity);
        }
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(IBlockAccess blockAccess, int i, int j, int k) {
        HellforgeTileEntity tileEntity;
        return !this.isActive && (tileEntity = (HellforgeTileEntity) blockAccess.getBlockTileEntity(i, j, k)).getVisualFuelLevel() > 0;
    }

    @Override
    public boolean setOnFireDirectly(World world, int i, int j, int k) {
        HellforgeTileEntity tileEntity;
        if (!this.isActive && (tileEntity = (HellforgeTileEntity) world.getBlockTileEntity(i, j, k)).attemptToLight()) {
            world.playSoundEffect((double) i + 0.5, (double) j + 0.5, (double) k + 0.5, "mob.ghast.fireball", 1.0f, world.rand.nextFloat() * 0.4f + 0.8f);
            return true;
        }
        return false;
    }

    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int i, int j, int k) {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    protected int getIDDroppedOnSilkTouch() {
        return NMBlocks.hellforge.blockID;
    }

    @Override
    public boolean getIsBlockWarm(IBlockAccess blockAccess, int i, int j, int k) {
        return this.isActive;
    }

    @Override
    public boolean doesBlockHopperInsert(World world, int i, int j, int k) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return HellforgeTileEntity.calcRedstoneFromOven((HellforgeTileEntity) world.getBlockTileEntity(x, y, z));
    }

    public boolean isValidCookItem(ItemStack stack) {
        return FurnaceRecipes.smelting().getSmeltingResult(stack.getItem().itemID) != null;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("hellforge");
        this.furnaceIconTop = register.registerIcon("hellforge_top");
        this.furnaceIconFront = register.registerIcon("hellforge_front_lit");
        this.fuelOverlays = new Icon[9];
        for (int iTempIndex = 0; iTempIndex < 9; ++iTempIndex) {
            this.fuelOverlays[iTempIndex] = register.registerIcon("btw:oven_fuel_overlay_" + iTempIndex);
        }
        this.blankOverlay = register.registerIcon("btw:blank_overlay");
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public int idPicked(World world, int i, int j, int k) {
        return NMBlocks.hellforge.blockID;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        int iFacing = iMetadata & 7;
        if (iFacing < 2 || iFacing > 5) {
            iFacing = 3;
        }
        if (this.currentFuelOverlay == null) {
            if (iFacing == iSide) {
                return this.furnaceIconFront;
            }
            if (iSide < 2) {
                return this.furnaceIconTop;
            }
            return this.blockIcon;
        }
        if (iFacing == iSide) {
            return this.currentFuelOverlay;
        }
        return this.blankOverlay;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        if (this.isRenderingInterior) {
            BlockPos myPos = new BlockPos(iNeighborI, iNeighborJ, iNeighborK, Block.getOppositeFacing(iSide));
            int iFacing = blockAccess.getBlockMetadata(myPos.x, myPos.y, myPos.z) & 7;
            return iSide != Block.getOppositeFacing(iFacing);
        }
        return super.shouldSideBeRendered(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));
        renderer.renderStandardBlock(this, i, j, k);
        int iFacing = renderer.blockAccess.getBlockMetadata(i, j, k) & 7;
        BlockModel transformedModel = this.modelBlockInterior.makeTemporaryCopy();
        transformedModel.rotateAroundYToFacing(iFacing);
        BlockPos interiorFacesPos = new BlockPos(i, j, k, iFacing);
        this.interiorBrightness = this.getMixedBrightnessForBlock(renderer.blockAccess, interiorFacesPos.x, interiorFacesPos.y, interiorFacesPos.z);
        renderer.setOverrideBlockTexture(this.blockIcon);
        this.isRenderingInterior = true;
        boolean bReturnValue = transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);
        this.isRenderingInterior = false;
        renderer.clearOverrideBlockTexture();
        return bReturnValue;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void renderBlockSecondPass(RenderBlocks renderer, int i, int j, int k, boolean bFirstPassResult) {
        int iFuelLevel;
        TileEntity tileEntity;
        if (bFirstPassResult && (tileEntity = renderer.blockAccess.getBlockTileEntity(i, j, k)) instanceof HellforgeTileEntity && (iFuelLevel = ((HellforgeTileEntity) tileEntity).getVisualFuelLevel()) > 0) {
            iFuelLevel = MathHelper.clamp_int(iFuelLevel - 2, 0, 8);
            this.currentFuelOverlay = this.fuelOverlays[iFuelLevel];
            renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));
            renderer.renderStandardBlock(this, i, j, k);
            this.currentFuelOverlay = null;
        }
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public int getMixedBrightnessForBlock(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
        if (this.isRenderingInterior) {
            return this.interiorBrightness;
        }
        return super.getMixedBrightnessForBlock(par1IBlockAccess, par2, par3, par4);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean renderBlockWithTexture(RenderBlocks renderer, int i, int j, int k, Icon texture) {
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));
        renderer.setOverrideBlockTexture(texture);
        boolean bReturnValue = renderer.renderStandardBlock(this, i, j, k);
        renderer.clearOverrideBlockTexture();
        return bReturnValue;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
        renderBlocks.renderBlockAsItemVanilla(this, iItemDamage, fBrightness);
        BlockModel transformedModel = this.modelBlockInterior.makeTemporaryCopy();
        transformedModel.rotateAroundYToFacing(3);
        transformedModel.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        if (this.isActive) {
            ItemStack cookStack;
            HellforgeTileEntity tileEntity = (HellforgeTileEntity) world.getBlockTileEntity(x, y, z);
            int iFuelLevel = tileEntity.getVisualFuelLevel();
            if (iFuelLevel == 1) {
                int iFacing = world.getBlockMetadata(x, y, z) & 7;
                float fX = (float) x + 0.5f;
                float fY = (float) y + 0.0f + rand.nextFloat() * 6.0f / 16.0f;
                float fZ = (float) z + 0.5f;
                float fFacingOffset = 0.52f;
                float fRandOffset = rand.nextFloat() * 0.6f - 0.3f;
                if (iFacing == 4) {
                    world.spawnParticle("largesmoke", fX - fFacingOffset, fY, fZ + fRandOffset, 0.0, 0.0, 0.0);
                } else if (iFacing == 5) {
                    world.spawnParticle("largesmoke", fX + fFacingOffset, fY, fZ + fRandOffset, 0.0, 0.0, 0.0);
                } else if (iFacing == 2) {
                    world.spawnParticle("largesmoke", fX + fRandOffset, fY, fZ - fFacingOffset, 0.0, 0.0, 0.0);
                } else if (iFacing == 3) {
                    world.spawnParticle("largesmoke", fX + fRandOffset, fY, fZ + fFacingOffset, 0.0, 0.0, 0.0);
                }
            }
            if ((cookStack = tileEntity.getCookStack()) != null && this.isValidCookItem(cookStack)) {
                for (int iTempCount = 0; iTempCount < 1; ++iTempCount) {
                    float fX = (float) x + 0.375f + rand.nextFloat() * 0.25f;
                    float fY = (float) y + 0.45f + rand.nextFloat() * 0.1f;
                    float fZ = (float) z + 0.375f + rand.nextFloat() * 0.25f;
                    world.spawnParticle("fcwhitecloud", fX, fY, fZ, 0.0, 0.0, 0.0);
                }
            }
        }
        super.randomDisplayTick(world, x, y, z, rand);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void renderBlockMovedByPiston(RenderBlocks renderBlocks, int i, int j, int k) {
        this.renderBlock(renderBlocks, i, j, k);
    }
}