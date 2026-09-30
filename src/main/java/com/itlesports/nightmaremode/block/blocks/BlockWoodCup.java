package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.util.NMFields;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockWoodCup extends BlockFlowerPot {
    private final int cupItemID;
    private final String liquidTexture;
    @Environment(EnvType.CLIENT) private Icon liquidIcon;

    public BlockWoodCup(int id, int cupItemID, String name, String liquidTexture) {
        super(id);
        this.cupItemID = cupItemID;
        this.liquidTexture = liquidTexture;
        this.setHardness(0.1F);
        this.setStepSound(Block.soundWoodFootstep);
        this.setUnlocalizedName(name);
        this.setTextureName("nightmare:ifhyWoodCup");
    }

    @Override public String getModId() { return NMFields.modID; }
    @Override public boolean isFlowerPot() { return false; }
    @Override public int idDropped(int metadata, Random random, int fortune) { return this.cupItemID; }
    @Override public int damageDropped(int metadata) { return metadata; }
    @Override public int getDamageValue(World world, int x, int y, int z) { return world.getBlockMetadata(x, y, z); }
    @Override public ItemStack getStackRetrievedByBlockDispenser(World world, int x, int y, int z) {
        return new ItemStack(this.cupItemID, 1, world.getBlockMetadata(x, y, z));
    }
    @Override public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        return 1.0F;
    }
    @Override public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                               int side, float clickX, float clickY, float clickZ) {
        return false;
    }
    @Override public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata,
                                                    float chance, int fortune) {
        if (!world.isRemote && world.rand.nextFloat() <= chance) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.cupItemID, 1, metadata));
        }
    }
    @Override public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player,
                                                           int x, int y, int z, int metadata) {
        this.dropBlockAsItem(world, x, y, z, metadata, 0);
    }

    @Override @Environment(EnvType.CLIENT)
    public int idPicked(World world, int x, int y, int z) { return this.cupItemID; }

    @Override @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = register.registerIcon("nightmare:ifhyWoodCup");
        if (this.liquidTexture != null) this.liquidIcon = register.registerIcon(this.liquidTexture);
    }

    @Override @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        // the flowerpot's six-pixel-wide, six-pixel-tall model, with a hollow wooden bowl.
        boolean allFaces = renderer.getRenderAllFaces();
        renderer.setRenderAllFaces(true);
        this.renderPart(renderer, x, y, z, 5, 0, 5, 11, 1, 11);
        this.renderPart(renderer, x, y, z, 5, 1, 5, 6, 6, 11);
        this.renderPart(renderer, x, y, z, 10, 1, 5, 11, 6, 11);
        this.renderPart(renderer, x, y, z, 6, 1, 5, 10, 6, 6);
        this.renderPart(renderer, x, y, z, 6, 1, 10, 10, 6, 11);
        if (this.liquidIcon != null) {
            renderer.setRenderBounds(6 / 16.0D, 1 / 16.0D, 6 / 16.0D,
                    10 / 16.0D, 5 / 16.0D, 10 / 16.0D);
            Tessellator.instance.setBrightness(this.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
            Tessellator.instance.setColorOpaque_F(1.0F, 1.0F, 1.0F);
            renderer.renderFaceYPos(this, x, y, z, this.liquidIcon);
        }
        renderer.setRenderAllFaces(allFaces);
        renderer.setRenderBounds(this.getBlockBoundsFromPoolBasedOnState(renderer.blockAccess, x, y, z));
        return true;
    }

    @Environment(EnvType.CLIENT)
    private void renderPart(RenderBlocks renderer, int x, int y, int z,
                            int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        renderer.setRenderBounds(minX / 16.0D, minY / 16.0D, minZ / 16.0D,
                maxX / 16.0D, maxY / 16.0D, maxZ / 16.0D);
        renderer.renderStandardBlock(this, x, y, z);
    }
}
