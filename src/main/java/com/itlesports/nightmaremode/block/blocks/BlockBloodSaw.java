package com.itlesports.nightmaremode.block.blocks;


import btw.block.BTWBlocks;
import btw.block.MechanicalBlock;
import btw.block.util.MechPowerUtils;
import btw.client.render.util.RenderUtils;
import btw.crafting.manager.SawCraftingManager;
import btw.item.BTWItems;
import btw.util.CustomDamageSource;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockBloodSaw
        extends Block
        implements MechanicalBlock {
    private static final int POWER_CHANGE_TICK_RATE = 10;
    private static final int SAW_TIME_BASE_TICK_RATE = 20;
    private static final int SAW_TIME_TICK_RATE_VARIANCE = 4;
    public static final float BASE_HEIGHT = 0.75f;
    public static final float BLADE_LENGTH = 0.625f;
    public static final float BLADE_HALF_LENGTH = 0.3125f;
    public static final float BLADE_WIDTH = 0.015625f;
    public static final float BLADE_HALF_WIDTH = 0.0078125f;
    public static final float BLADE_HEIGHT = 0.25f;
    @Environment(value = EnvType.CLIENT)
    private Icon iconFront;
    @Environment(value = EnvType.CLIENT)
    private Icon iconBladeOff;
    @Environment(value = EnvType.CLIENT)
    private Icon iconBladeOn;

    public BlockBloodSaw(int iBlockID) {
        super(iBlockID, BTWBlocks.plankMaterial);
        this.setHardness(2.0f);
        this.setAxesEffectiveOn(true);
        this.setBuoyancy(1.0f);
        this.initBlockBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        this.setFireProperties(5, 20);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("nmBloodSawBlock");
        this.setTextureName("nmBloodSaw");
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    public int tickRate(World world) {
        return 10;
    }

    @Override
    public int onBlockPlaced(World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ, int iMetadata) {
        return this.setFacing(iMetadata, Block.getOppositeFacing(iFacing));
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entityLiving, ItemStack stack) {
        int iFacing = MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving);
        this.setFacing(world, i, j, k, iFacing);
    }

    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        super.onBlockAdded(world, i, j, k);
        world.scheduleBlockUpdate(i, j, k, this.blockID, 10);
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
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
        float fBaseHeight = 0.71875f;
        return this.getBlockBoundsFromPoolForBaseHeight(world, i, j, k, fBaseHeight).offset(i, j, k);
    }

    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int i, int j, int k) {
        return this.getBlockBoundsFromPoolForBaseHeight(blockAccess, i, j, k, 0.75f);
    }

    @Override
    public void onNeighborBlockChange(World world, int i, int j, int k, int iBlockID) {
        if (!world.isUpdatePendingThisTickForBlock(i, j, k, this.blockID)) {
            this.scheduleUpdateIfRequired(world, i, j, k);
        }
    }

    @Override
    public void updateTick(World world, int i, int j, int k, Random rand) {
        boolean bReceivingPower = true;
        boolean bOn = this.isBlockOn(world, i, j, k);
        if (bOn != bReceivingPower) {
            this.emitSawParticles(world, i, j, k, rand);
            this.setBlockOn(world, i, j, k, bReceivingPower);
            world.playSoundEffect((double) i + 0.5, (double) j + 0.5, (double) k + 0.5, "minecart.base", 0.2f + rand.nextFloat() * 0.1f, 1.5f + rand.nextFloat() * 0.1f);
            this.scheduleUpdateIfRequired(world, i, j, k);
        } else {
            this.sawBlockToFront(world, i, j, k, rand);
        }
    }

    @Override
    public void randomUpdateTick(World world, int i, int j, int k, Random rand) {
        if (!world.isUpdateScheduledForBlock(i, j, k, this.blockID)) {
            this.scheduleUpdateIfRequired(world, i, j, k);
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
        if (world.isRemote) {
            return;
        }
        if (this.isBlockOn(world, i, j, k) && entity instanceof EntityLivingBase) {
            int iFacing = this.getFacing(world, i, j, k);
            float fHalfLength = 0.3125f;
            float fHalfWidth = 0.0078125f;
            float fBlockHeight = 0.25f;
            AxisAlignedBB sawBox = switch (iFacing) {
                case 0 ->
                        AxisAlignedBB.getAABBPool().getAABB(0.5f - fHalfLength, 0.0, 0.5f - fHalfWidth, 0.5f + fHalfLength, fBlockHeight, 0.5f + fHalfWidth);
                case 1 ->
                        AxisAlignedBB.getAABBPool().getAABB(0.5f - fHalfLength, 1.0f - fBlockHeight, 0.5f - fHalfWidth, 0.5f + fHalfLength, 1.0, 0.5f + fHalfWidth);
                case 2 ->
                        AxisAlignedBB.getAABBPool().getAABB(0.5f - fHalfLength, 0.5f - fHalfWidth, 0.0, 0.5f + fHalfLength, 0.5f + fHalfWidth, fBlockHeight);
                case 3 ->
                        AxisAlignedBB.getAABBPool().getAABB(0.5f - fHalfLength, 0.5f - fHalfWidth, 1.0f - fBlockHeight, 0.5f + fHalfLength, 0.5f + fHalfWidth, 1.0);
                case 4 ->
                        AxisAlignedBB.getAABBPool().getAABB(0.0, 0.5f - fHalfWidth, 0.5f - fHalfLength, fBlockHeight, 0.5f + fHalfWidth, 0.5f + fHalfLength);
                default ->
                        AxisAlignedBB.getAABBPool().getAABB(1.0f - fBlockHeight, 0.5f - fHalfWidth, 0.5f - fHalfLength, 1.0, 0.5f + fHalfWidth, 0.5f + fHalfLength);
            };
            sawBox = sawBox.getOffsetBoundingBox(i, j, k);
            List collisionList = null;
            collisionList = world.getEntitiesWithinAABB(EntityLivingBase.class, sawBox);
            if (collisionList != null && !collisionList.isEmpty()) {
                DamageSource source = CustomDamageSource.damageSourceSaw;
                int iDamage = 8;
                BlockPos targetPos = new BlockPos(i, j, k);
                targetPos.addFacingAsOffset(iFacing);
                int iTargetBlockID = world.getBlockId(targetPos.x, targetPos.y, targetPos.z);
                int iTargetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
                if (iTargetBlockID == BTWBlocks.aestheticOpaque.blockID && (iTargetMetadata == 13 || iTargetMetadata == 12)) {
                    source = CustomDamageSource.damageSourceChoppingBlock;
                    iDamage *= 2;
                    if (iTargetMetadata == 13) {
                        world.setBlockMetadataWithNotify(targetPos.x, targetPos.y, targetPos.z, 12);
                    }
                }
                for (int iTempListIndex = 0; iTempListIndex < collisionList.size(); ++iTempListIndex) {
                    EntityLivingBase tempTargetEntity = (EntityLivingBase) collisionList.get(iTempListIndex);
                    if (!tempTargetEntity.attackEntityFrom(source, iDamage)) continue;
                    world.playAuxSFX(2223, i, j, k, iFacing);
                }
            }
        }
    }

    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
        return iFacing != this.getFacing(blockAccess, i, j, k);
    }

    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing, boolean bIgnoreTransparency) {
        return Block.getOppositeFacing(iFacing) == this.getFacing(blockAccess, i, j, k);
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 2;
    }

    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        this.dropItemsIndividually(world, i, j, k, Item.goldNugget.itemID, 1, 0, fChanceOfDrop);
        this.dropItemsIndividually(world, i, j, k, Item.stick.itemID, 2, 0, fChanceOfDrop);
        this.dropItemsIndividually(world, i, j, k, BTWItems.hellfireDust.itemID, 3, 0, fChanceOfDrop);
        this.dropItemsIndividually(world, i, j, k, NMItems.refinedDiamondIngot.itemID, 1, 0, fChanceOfDrop);
        this.dropItemsIndividually(world, i, j, k, NMItems.bloodIngot.itemID, 1, 0, fChanceOfDrop);
        return true;
    }

    @Override
    public int getFacing(int iMetadata) {
        return iMetadata & 7;
    }

    @Override
    public int setFacing(int iMetadata, int iFacing) {
        iMetadata &= 0xFFFFFFF8;
        return iMetadata |= iFacing;
    }

    @Override
    public boolean canRotateOnTurntable(IBlockAccess iBlockAccess, int i, int j, int k) {
        int iFacing = this.getFacing(iBlockAccess, i, j, k);
        return iFacing != 0;
    }

    @Override
    public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int i, int j, int k) {
        int iFacing = this.getFacing(blockAccess, i, j, k);
        return iFacing != 0 && iFacing != 1;
    }

    @Override
    public boolean rotateAroundJAxis(World world, int i, int j, int k, boolean bReverse) {
        if (super.rotateAroundJAxis(world, i, j, k, bReverse)) {
            world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate(world));
            return true;
        }
        return false;
    }

    @Override
    public boolean toggleFacing(World world, int i, int j, int k, boolean bReverse) {
        int iFacing = this.getFacing(world, i, j, k);
        iFacing = Block.cycleFacing(iFacing, bReverse);
        this.setFacing(world, i, j, k, iFacing);
        world.scheduleBlockUpdate(i, j, k, this.blockID, this.tickRate(world));
        world.notifyBlockChange(i, j, k, this.blockID);
        return true;
    }

    @Override
    public boolean isIncineratedInCrucible() {
        return false;
    }

    protected boolean isCurrentPowerStateValid(World world, int i, int j, int k) {
        boolean bReceivingPower = true;
        boolean bOn = this.isBlockOn(world, i, j, k);
        return bOn == bReceivingPower;
    }

    public boolean isBlockOn(IBlockAccess iBlockAccess, int i, int j, int k) {
        return (iBlockAccess.getBlockMetadata(i, j, k) & 8) > 0;
    }

    public void setBlockOn(World world, int i, int j, int k, boolean bOn) {
        int iMetaData = world.getBlockMetadata(i, j, k) & 7;
        if (bOn) {
            iMetaData |= 8;
        }
        world.setBlockMetadataWithNotify(i, j, k, iMetaData);
    }

    protected void scheduleUpdateIfRequired(World world, int i, int j, int k) {
        if (!this.isCurrentPowerStateValid(world, i, j, k)) {
            world.scheduleBlockUpdate(i, j, k, this.blockID, 10);
        }
        else if (this.isBlockOn(world, i, j, k)) {
            int iFacing = this.getFacing(world, i, j, k);
            BlockPos targetPos = new BlockPos(i, j, k, iFacing);
            Block targetBlock = Block.blocksList[world.getBlockId(targetPos.x, targetPos.y, targetPos.z)];
            int targetMetadata = world.getBlockMetadata(targetPos.x, targetPos.y, targetPos.z);
            if (targetBlock != null && (targetBlock.blockMaterial.isSolid() || SawCraftingManager.instance.getRecipe(targetBlock, targetMetadata) != null || targetBlock.doesBlockDropAsItemOnSaw(world, targetPos.x, targetPos.y, targetPos.z))) {
                world.playSoundEffect((double) i + 0.5, (double) j + 0.5, (double) k + 0.5, "minecart.base", 0.2f + world.rand.nextFloat() * 0.1f, 1.9f + world.rand.nextFloat() * 0.1f);
                world.scheduleBlockUpdate(i, j, k, this.blockID, 10);
            }
        }
    }

    public AxisAlignedBB getBlockBoundsFromPoolForBaseHeight(IBlockAccess blockAccess, int i, int j, int k, float fBaseHeight) {
        int iFacing = this.getFacing(blockAccess, i, j, k);
        switch (iFacing) {
            case 0: {
                return AxisAlignedBB.getAABBPool().getAABB(0.0, 1.0f - fBaseHeight, 0.0, 1.0, 1.0, 1.0);
            }
            case 1: {
                return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, 1.0, fBaseHeight, 1.0);
            }
            case 2: {
                return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 1.0f - fBaseHeight, 1.0, 1.0, 1.0);
            }
            case 3: {
                return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, 1.0, 1.0, fBaseHeight);
            }
            case 4: {
                return AxisAlignedBB.getAABBPool().getAABB(1.0f - fBaseHeight, 0.0, 0.0, 1.0, 1.0, 1.0);
            }
        }
        return AxisAlignedBB.getAABBPool().getAABB(0.0, 0.0, 0.0, fBaseHeight, 1.0, 1.0);
    }

    void emitSawParticles(World world, int i, int j, int k, Random random) {
        int iFacing = this.getFacing(world, i, j, k);
        float fBladeXPos = i;
        float fBladeYPos = j;
        float fBladeZPos = k;
        float fBladeXExtent = 0.0f;
        float fBladeZExtent = 0.0f;
        switch (iFacing) {
            case 0: {
                fBladeXPos += 0.5f;
                fBladeZPos += 0.5f;
                fBladeXExtent = 1.0f;
                break;
            }
            case 1: {
                fBladeXPos += 0.5f;
                fBladeZPos += 0.5f;
                fBladeYPos += 1.0f;
                fBladeXExtent = 1.0f;
                break;
            }
            case 2: {
                fBladeXPos += 0.5f;
                fBladeYPos += 0.5f;
                fBladeXExtent = 1.0f;
                break;
            }
            case 3: {
                fBladeXPos += 0.5f;
                fBladeYPos += 0.5f;
                fBladeZPos += 1.0f;
                fBladeXExtent = 1.0f;
                break;
            }
            case 4: {
                fBladeYPos += 0.5f;
                fBladeZPos += 0.5f;
                fBladeZExtent = 1.0f;
                break;
            }
            default: {
                fBladeYPos += 0.5f;
                fBladeZPos += 0.5f;
                fBladeXPos += 1.0f;
                fBladeZExtent = 1.0f;
            }
        }
        for (int counter = 0; counter < 5; ++counter) {
            float smokeX = fBladeXPos + (random.nextFloat() - 0.5f) * fBladeXExtent;
            float smokeY = fBladeYPos + random.nextFloat() * 0.1f;
            float smokeZ = fBladeZPos + (random.nextFloat() - 0.5f) * fBladeZExtent;
            world.spawnParticle("smoke", smokeX, smokeY, smokeZ, 0.0, 0.0, 0.0);
        }
    }

    protected void sawBlockToFront(World world, int i, int j, int k, Random random) {
        Block targetBlock;
        int iFacing = this.getFacing(world, i, j, k);
        BlockPos targetPos = new BlockPos(i, j, k, iFacing);
        if (!world.isAirBlock(targetPos.x, targetPos.y, targetPos.z) && !this.handleSawingExceptionCases(world, targetPos.x, targetPos.y, targetPos.z, i, j, k, iFacing, random) && (targetBlock = Block.blocksList[world.getBlockId(targetPos.x, targetPos.y, targetPos.z)]) != null) {
            if (targetBlock.doesBlockBreakSaw(world, targetPos.x, targetPos.y, targetPos.z)) {
                this.breakSaw(world, i, j, k);
            } else if (targetBlock.onBlockSawed(world, targetPos.x, targetPos.y, targetPos.z, i, j, k)) {
                this.emitSawParticles(world, targetPos.x, targetPos.y, targetPos.z, random);
            }
        }
    }

    private boolean handleSawingExceptionCases(World world, int i, int j, int k, int iSawI, int iSawJ, int iSawK, int iSawFacing, Random random) {
        int iTargetBlockID = world.getBlockId(i, j, k);
        return iTargetBlockID == Block.pistonMoving.blockID;
    }

    public void breakSaw(World world, int i, int j, int k) {
        this.dropComponentItemsOnBadBreak(world, i, j, k, world.getBlockMetadata(i, j, k), 1.0f);
        world.playAuxSFX(2235, i, j, k, 0);
        world.setBlockWithNotify(i, j, k, 0);
    }

    @Override
    public boolean canOutputMechanicalPower() {
        return false;
    }

    @Override
    public boolean canInputMechanicalPower() {
        return false;
    }

    @Override
    public boolean isInputtingMechanicalPower(World world, int i, int j, int k) {
        return true;
    }

    @Override
    public boolean canInputAxlePowerToFacing(World world, int i, int j, int k, int iFacing) {
        int iBlockFacing = this.getFacing(world, i, j, k);
        return iFacing != iBlockFacing;
    }

    @Override
    public boolean isOutputtingMechanicalPower(World world, int i, int j, int k) {
        return false;
    }

    @Override
    public void overpower(World world, int i, int j, int k) {
        this.breakSaw(world, i, j, k);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.iconFront = register.registerIcon("nm_saw_front");
        this.iconBladeOff = register.registerIcon("nm_saw_blade");
        this.iconBladeOn = register.registerIcon("saw_blade_powered");
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public Icon getIcon(int iSide, int iMetadata) {
        if (iSide == 1) {
            return this.iconFront;
        }
        return this.blockIcon;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int i, int j, int k, int iSide) {
        int iFacing = this.getFacing(blockAccess, i, j, k);
        if (iSide == iFacing) {
            return this.iconFront;
        }
        return this.blockIcon;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void randomDisplayTick(World world, int i, int j, int k, Random random) {
        if (this.isBlockOn(world, i, j, k)) {
            this.emitSawParticles(world, i, j, k, random);
        }
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        return this.currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(iNeighborI, iNeighborJ, iNeighborK, iSide);
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int i, int j, int k) {
        IBlockAccess blockAccess = renderer.blockAccess;
        float fHalfLength = 0.5f;
        float fHalfWidth = 0.5f;
        float fBlockHeight = 0.75f;
        int iFacing = this.getFacing(blockAccess, i, j, k);
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, i, j, k));
        renderer.renderStandardBlock(this, i, j, k);
        fHalfLength = 0.3125f;
        fHalfWidth = 0.0078125f;
        fBlockHeight = 0.25f;
        switch (iFacing) {
            case 0: {
                renderer.setRenderBounds(0.5f - fHalfLength, 0.0, 0.5f - fHalfWidth, 0.5f + fHalfLength, 0.999f, 0.5f + fHalfWidth);
                renderer.setUVRotateEast(3);
                renderer.setUVRotateWest(3);
                renderer.setUVRotateSouth(1);
                renderer.setUVRotateNorth(2);
                renderer.setUVRotateBottom(3);
                break;
            }
            case 1: {
                renderer.setRenderBounds(0.5f - fHalfLength, 0.001f, 0.5f - fHalfWidth, 0.5f + fHalfLength, 1.0, 0.5f + fHalfWidth);
                renderer.setUVRotateSouth(2);
                renderer.setUVRotateNorth(1);
                break;
            }
            case 2: {
                renderer.setRenderBounds(0.5f - fHalfLength, 0.5f - fHalfWidth, 0.0, 0.5f + fHalfLength, 0.5f + fHalfWidth, fBlockHeight);
                renderer.setUVRotateSouth(3);
                renderer.setUVRotateNorth(4);
                renderer.setUVRotateEast(3);
                renderer.setUVRotateWest(3);
                break;
            }
            case 3: {
                renderer.setRenderBounds(0.5f - fHalfLength, 0.5f - fHalfWidth, 1.0f - fBlockHeight, 0.5f + fHalfLength, 0.5f + fHalfWidth, 1.0);
                renderer.setUVRotateSouth(4);
                renderer.setUVRotateNorth(3);
                renderer.setUVRotateTop(3);
                renderer.setUVRotateBottom(3);
                break;
            }
            case 4: {
                renderer.setRenderBounds(0.0, 0.5f - fHalfWidth, 0.5f - fHalfLength, fBlockHeight, 0.5f + fHalfWidth, 0.5f + fHalfLength);
                renderer.setUVRotateEast(4);
                renderer.setUVRotateWest(3);
                renderer.setUVRotateTop(2);
                renderer.setUVRotateBottom(1);
                renderer.setUVRotateNorth(3);
                renderer.setUVRotateSouth(4);
                break;
            }
            default: {
                renderer.setRenderBounds(1.0f - fBlockHeight, 0.5f - fHalfWidth, 0.5f - fHalfLength, 1.0, 0.5f + fHalfWidth, 0.5f + fHalfLength);
                renderer.setUVRotateEast(3);
                renderer.setUVRotateWest(4);
                renderer.setUVRotateTop(1);
                renderer.setUVRotateBottom(2);
                renderer.setUVRotateSouth(4);
                renderer.setUVRotateNorth(3);
            }
        }
        renderer.setRenderAllFaces(true);
        Icon bladeIcon = this.iconBladeOff;
        if (this.isBlockOn(blockAccess, i, j, k)) {
            bladeIcon = this.iconBladeOn;
        }
        RenderUtils.renderStandardBlockWithTexture(renderer, this, i, j, k, bladeIcon);
        renderer.setRenderAllFaces(false);
        renderer.clearUVRotation();
        return true;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int iItemDamage, float fBrightness) {
        renderBlocks.setRenderBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, this, -0.5f, -0.5f, -0.5f, 1);
        renderBlocks.setRenderBounds(0.1875, 0.001f, 0.4921875, 0.8125, 1.0, 0.5078125);
        RenderUtils.renderInvBlockWithTexture(renderBlocks, this, -0.5f, -0.5f, -0.5f, this.iconBladeOff);
    }
}