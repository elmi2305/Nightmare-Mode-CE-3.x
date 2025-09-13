package com.itlesports.nightmaremode.block.blocks;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import btw.block.BTWBlocks;
import btw.block.blocks.BasketBlock;
import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.tileEntities.CustomBasketTileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class CustomBasketBlock extends BasketBlock {
    public BlockModel blockModelBase;
    public BlockModel blockModelBaseOpenCollision;
    public BlockModel blockModelLid;
    public BlockModel blockModelLidFull;
    public BlockModel blockModelInterior;
    private static final AxisAlignedBB boxCollisionLidOpenLip = new AxisAlignedBB(0.0D, 0.9375D, 0.3125D, 1.0D, 1.0D, 0.4375D);
    private static final Vec3 lidRotationPoint = Vec3.createVectorHelper(0.5D, 0.375D, 0.875D);
    @Environment(EnvType.CLIENT)
    private Icon iconBaseOpenTop;
    @Environment(EnvType.CLIENT)
    private Icon iconFront;
    @Environment(EnvType.CLIENT)
    private Icon iconTop;
    @Environment(EnvType.CLIENT)
    private Icon iconBottom;
    @Environment(EnvType.CLIENT)
    private boolean renderingBase;
    @Environment(EnvType.CLIENT)
    private boolean renderingInterior;

    public CustomBasketBlock(int iBlockID) {
        super(iBlockID, BTWBlocks.wickerMaterial);
        this.initBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        this.initModelBase();
        this.initModelBaseOpenCollison();
        this.initModelLid();
        this.initModelLidFull();
        this.initModelInterior();
        this.setUnlocalizedName("fcBlockBasketWicker");
        this.setTextureName("btw:wicker_basket");
        this.setHardness(0.2f);
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        this.dropItemsIndividually(world, i, j, k, NMBlocks.customWickerBasket.blockID, 1, 0, 1);
        return true;
    }

    @Override
    public int idDropped(int par1, Random par2Random, int par3) {
        return NMBlocks.customWickerBasket.blockID;
    }

    public TileEntity createNewTileEntity(World world) {
        return new CustomBasketTileEntity();
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick) {
        int iMetadata = world.getBlockMetadata(i, j, k);
        if (!this.getIsOpen(iMetadata)) {
            if (!world.isRemote) {
                this.setIsOpen(world, i, j, k, true);
            } else {
                player.playSound("step.gravel", 0.25F + world.rand.nextFloat() * 0.1F, 0.5F + world.rand.nextFloat() * 0.1F);
            }

            return true;
        } else {
            if (this.isClickingOnLid(world, i, j, k, iFacing, fXClick, fYClick, fZClick)) {
                CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
                if (!tileEntity.closing) {
                    if (!world.isRemote) {
                        tileEntity.startClosingServerSide();
                    }

                    return true;
                }
            } else {
                if (this.getHasContents(iMetadata)) {
                    if (world.isRemote) {
                        player.playSound("step.gravel", 0.5F + world.rand.nextFloat() * 0.25F, 1.0F + world.rand.nextFloat() * 0.25F);
                    }

                    CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
                    ItemStack storageStack = tileEntity.getStorageStack();
                    if (player.inventory.addItemStackToInventory(storageStack)) {
                        tileEntity.setStorageStack(null);
                    } else if (!world.isRemote) {
                        this.ejectStorageStack(world, i, j, k);
                    }

                    this.setHasContents(world, i, j, k, false);
                    return true;
                }

                ItemStack heldStack = player.getCurrentEquippedItem();
                if (heldStack != null) {
                    if (world.isRemote) {
                        player.playSound("step.gravel", 0.5F + world.rand.nextFloat() * 0.25F, 0.5F + world.rand.nextFloat() * 0.25F);
                    } else {
                        CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
                        tileEntity.setStorageStack(heldStack);
                    }

                    heldStack.stackSize = 0;
                    this.setHasContents(world, i, j, k, true);
                    return true;
                }
            }

            return false;
        }
    }

    private void ejectStorageStack(World world, int i, int j, int k) {
        CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
        ItemStack storageStack = tileEntity.getStorageStack();
        if (storageStack != null) {
            float xOffset = 0.5F;
            float yOffset = 0.4F;
            float zOffset = 0.5F;
            double xPos = (float)i + xOffset;
            double yPos = (float)j + yOffset;
            double zPos = (float)k + zOffset;
            EntityItem entityitem = new EntityItem(world, xPos, yPos, zPos, storageStack);
            entityitem.motionY = 0.2;
            double fFacingFactor = 0.15;
            double fRandomFactor = 0.05;
            int iFacing = this.getFacing(world, i, j, k);
            if (iFacing <= 3) {
                entityitem.motionX = (world.rand.nextDouble() * (double)2.0F - (double)1.0F) * fRandomFactor;
                if (iFacing == 2) {
                    entityitem.motionZ = -fFacingFactor;
                } else {
                    entityitem.motionZ = fFacingFactor;
                }
            } else {
                entityitem.motionZ = (world.rand.nextDouble() * (double)2.0F - (double)1.0F) * fRandomFactor;
                if (iFacing == 4) {
                    entityitem.motionX = -fFacingFactor;
                } else {
                    entityitem.motionX = fFacingFactor;
                }
            }

            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
            tileEntity.setStorageStack(null);
        }

    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        return this.getFixedBlockBoundsFromPool().offset(i, j, k);
    }

    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
        return !this.getIsOpen(blockAccess, i, j, k) ? AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F) : AxisAlignedBB.getAABBPool().getAABB(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
    }

    public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3 startRay, Vec3 endRay) {
        RayTraceUtils rayTrace = new RayTraceUtils(world, i, j, k, startRay, endRay);
        int iMetadata = world.getBlockMetadata(i, j, k);
        int iFacing = this.getFacing(iMetadata);
        BlockModel tempBaseModel;
        if (!this.getIsOpen(iMetadata)) {
            tempBaseModel = this.blockModelBase.makeTemporaryCopy();
            BlockModel tempLidModel;
            if (this.getHasContents(iMetadata)) {
                tempLidModel = this.blockModelLidFull.makeTemporaryCopy();
            } else {
                tempLidModel = this.blockModelLid.makeTemporaryCopy();
            }

            tempLidModel.rotateAroundYToFacing(iFacing);
            tempLidModel.addToRayTrace(rayTrace);
        } else {
            tempBaseModel = this.blockModelBaseOpenCollision.makeTemporaryCopy();
            CustomBasketTileEntity tileEntity = (CustomBasketTileEntity)world.getBlockTileEntity(i, j, k);
            if (tileEntity.lidOpenRatio > 0.95F) {
                AxisAlignedBB tempLidBox = boxCollisionLidOpenLip.makeTemporaryCopy();
                tempLidBox.rotateAroundYToFacing(iFacing);
                rayTrace.addBoxWithLocalCoordsToIntersectionList(tempLidBox);
            }
        }

        tempBaseModel.rotateAroundYToFacing(iFacing);
        tempBaseModel.addToRayTrace(rayTrace);
        return rayTrace.getFirstIntersection();
    }

    public void onCrushedByFallingEntity(World world, int i, int j, int k, EntityFallingSand entity) {
        if (!world.isRemote) {
            this.dropItemsIndividually(world, i, j, k, BTWItems.wickerPane.itemID, 1, 0, 0.75F);
        }

    }

    public BlockModel getLidModel(int iMetadata) {
        return this.getHasContents(iMetadata) ? this.blockModelLidFull : this.blockModelLid;
    }

    public Vec3 getLidRotationPoint() {
        return lidRotationPoint;
    }

    public float mobSpawnOnVerticalOffset(World world, int i, int j, int k) {
        return -0.5F;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(World par1World, int par2, int par3, int par4, int par5) {
        return CustomBasketTileEntity.calculateComparatorPower((CustomBasketTileEntity)par1World.getBlockTileEntity(par2, par3, par4));
    }

    private void initModelBase() {
        this.blockModelBase = new BlockModel();
        this.blockModelBase.addBox(0.0625F, 0.0F, 0.125F, 0.9375F, 0.375F, 0.875F);
    }

    private void initModelBaseOpenCollison() {
        this.blockModelBaseOpenCollision = new BlockModel();
        this.blockModelBaseOpenCollision.addBox(0.0625F, 0.0F, 0.125F, 0.9375F, 0.75F, 0.875F);
    }

    private void initModelLid() {
        this.blockModelLid = new BlockModel();
        this.blockModelLid.addBox(0.0F, 0.375F, 0.0625F, 1.0F, 0.4375F, 0.9375F);
        this.blockModelLid.addBox(0.0625F, 0.4375F, 0.125F, 0.9375F, 0.5F, 0.875F);
        this.blockModelLid.addBox(0.375F, 0.5F, 0.4375F, 0.625F, 0.5625F, 0.5625F);
    }

    private void initModelLidFull() {
        this.blockModelLidFull = new BlockModel();
        this.blockModelLidFull.addBox(0.0F, 0.375F, 0.0625F, 1.0F, 0.4375F, 0.9375F);
        this.blockModelLidFull.addBox(0.0625F, 0.4375F, 0.125F, 0.9375F, 0.5F, 0.875F);
        this.blockModelLidFull.addBox(0.125F, 0.5F, 0.1875F, 0.875F, 0.5625F, 0.8125F);
    }

    private void initModelInterior() {
        this.blockModelInterior = new BlockModel();
        this.blockModelInterior.addBox(0.8760000000474975, 0.375F, 0.8135000000474975, 0.124F, 0.0625F, 0.1865F);
    }

    private boolean isClickingOnLid(World world, int i, int j, int k, int iSideClicked, float fXClick, float fYClick, float fZClick) {
        return fYClick > 0.75F;
    }

    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.iconBaseOpenTop = register.registerIcon("btw:wicker_basket_top_open");
        this.iconFront = register.registerIcon("btw:wicker_basket_front");
        this.iconTop = register.registerIcon("btw:wicker_basket_top");
        this.iconBottom = register.registerIcon("btw:wicker_basket_bottom");
    }

    @Environment(EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        if (iSide == 1 && this.renderingBase) {
            return this.iconBaseOpenTop;
        } else {
            if (this.renderingInterior) {
                if (iSide == 1) {
                    return this.iconBottom;
                }
            } else {
                if (iSide == 1) {
                    return this.iconTop;
                }

                if (iSide == 0) {
                    return this.iconBottom;
                }

                int iFacing = this.getFacing(iMetadata);
                if (iSide == iFacing) {
                    return this.iconFront;
                }
            }

            return super.getIcon(iSide, iMetadata);
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        if (iSide == 0) {
            if (this.renderingInterior) {
                return false;
            } else {
                return !this.renderingBase || super.shouldSideBeRendered(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
            }
        } else {
            return true;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        int iMetadata = renderer.blockAccess.getBlockMetadata(i, j, k);
        int iFacing = this.getFacing(iMetadata);
        this.renderingBase = true;
        BlockModel transformedModel = this.blockModelBase.makeTemporaryCopy();
        transformedModel.rotateAroundYToFacing(this.getFacing(renderer.blockAccess, i, j, k));
        renderer.setUVRotateTop(this.convertFacingToTopTextureRotation(iFacing));
        renderer.setUVRotateBottom(this.convertFacingToBottomTextureRotation(iFacing));
        boolean bReturnValue = transformedModel.renderAsBlock(renderer, this, i, j, k);
        this.renderingBase = false;
        if (!this.getIsOpen(iMetadata)) {
            if (this.getHasContents(iMetadata)) {
                transformedModel = this.blockModelLidFull.makeTemporaryCopy();
            } else {
                transformedModel = this.blockModelLid.makeTemporaryCopy();
            }

            transformedModel.rotateAroundYToFacing(this.getFacing(renderer.blockAccess, i, j, k));
            transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);
        } else {
            transformedModel = this.blockModelInterior.makeTemporaryCopy();
            transformedModel.rotateAroundYToFacing(iFacing);
            this.renderingInterior = true;
            transformedModel.renderAsBlockWithColorMultiplier(renderer, this, i, j, k);
            this.renderingInterior = false;
        }

        renderer.clearUVRotation();
        return bReturnValue;
    }

    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
        this.blockModelLid.renderAsItemBlock(renderBlocks, this, iItemDamage);
        this.blockModelBase.renderAsItemBlock(renderBlocks, this, iItemDamage);
    }

    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, MovingObjectPosition rayTraceHit) {
        int i = rayTraceHit.blockX;
        int j = rayTraceHit.blockY;
        int k = rayTraceHit.blockZ;
        int iMetadata = world.getBlockMetadata(i, j, k);
        int iFacing = this.getFacing(iMetadata);
        double maxYBox = (float)j + 0.5F;
        if (this.getIsOpen(iMetadata)) {
            if (rayTraceHit.hitVec.yCoord - (double) j >= 0.9364999999525025) {
                AxisAlignedBB tempLidBox = boxCollisionLidOpenLip.makeTemporaryCopy();
                tempLidBox.rotateAroundYToFacing(iFacing);
                return tempLidBox.offset(i, j, k);
            }

            maxYBox -= 0.125F;
        }

        double minXBox;
        double maxXBox;
        double minZBox;
        double maxZBox;
        if (iFacing != 2 && iFacing != 3) {
            minXBox = (double)i + (double)0.0625F + (double)0.0625F;
            maxXBox = (double)i + (double)1.0F - (double)0.0625F - (double)0.0625F;
            minZBox = (double)k + (double)0.0625F + (double)0.0F;
            maxZBox = (double)k + (double)1.0F - (double)0.0625F - (double)0.0F;
        } else {
            minXBox = (double)i + (double)0.0625F + (double)0.0F;
            maxXBox = (double)i + (double)1.0F - (double)0.0625F - (double)0.0F;
            minZBox = (double)k + (double)0.0625F + (double)0.0625F;
            maxZBox = (double)k + (double)1.0F - (double)0.0625F - (double)0.0625F;
        }

        return AxisAlignedBB.getAABBPool().getAABB(minXBox, j, minZBox, maxXBox, maxYBox, maxZBox);
    }
}
