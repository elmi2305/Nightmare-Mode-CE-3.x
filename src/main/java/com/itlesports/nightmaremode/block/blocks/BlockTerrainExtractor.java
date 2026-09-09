package com.itlesports.nightmaremode.block.blocks;

import api.item.util.ItemUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerTerrainExtractor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

public class BlockTerrainExtractor extends BlockContainer {
    public static final String[] TYPES = {"Potassium", "Nitrogen", "Moisture", "Porosity", "Acidity"};
    @Environment(EnvType.CLIENT) private Icon[] sideIcons;
    @Environment(EnvType.CLIENT) private Icon[] topIcons;
    @Environment(EnvType.CLIENT) private Icon[] bottomIcons;

    public BlockTerrainExtractor(int id) {
        super(id, Material.iron);
        this.setHardness(4.0F);
        this.setResistance(10.0F);
        this.setPicksEffectiveOn();
        this.setStepSound(BTWBlocks.oreStepSound);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setUnlocalizedName("ifhyTerrainExtractor");
        this.setTextureName("nightmare:ifhyTerrainExtractor");
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
    public boolean renderBlock(RenderBlocks renderer, int x, int y, int z) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.sideIcons = new Icon[TYPES.length];
        this.topIcons = new Icon[TYPES.length];
        this.bottomIcons = new Icon[TYPES.length];
        for (int type = 0; type < TYPES.length; ++type) {
            this.sideIcons[type] = register.registerIcon("nightmare:ifhyExtractor" + TYPES[type] + "Side");
            this.topIcons[type] = register.registerIcon("nightmare:ifhyExtractor" + TYPES[type] + "Top");
            this.bottomIcons[type] = register.registerIcon("nightmare:ifhyExtractor" + TYPES[type] + "Bottom");
        }
        this.blockIcon = this.sideIcons[0];
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TerrainExtractorTileEntity();
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata % TYPES.length;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        int type = Math.max(0, Math.min(this.sideIcons.length - 1, metadata % TYPES.length));
        if (side == 0) return this.bottomIcons[type];
        if (side == 1) return this.topIcons[type];
        return this.sideIcons[type];
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderer, int metadata, float brightness) {
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
        RenderUtils.renderInvBlockWithMetadata(renderer, this, -0.5F, -0.5F, -0.5F, metadata);
        renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    }

    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
        for (int metadata = 0; metadata < TYPES.length; ++metadata) {
            list.add(new ItemStack(id, 1, metadata));
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (tile instanceof TerrainExtractorTileEntity extractor) {
            BTWMod.serverOpenCustomInterface((EntityPlayerMP)player,
                    new ContainerTerrainExtractor(player.inventory, extractor), ContainerTerrainExtractor.ID);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, int id, int metadata) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!world.isRemote && tile instanceof TerrainExtractorTileEntity extractor) {
            for (int slot = 0; slot < extractor.getSizeInventory(); ++slot) {
                ItemStack stack = extractor.getStackInSlot(slot);
                if (stack != null) ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, stack, 1);
            }
        }
        super.breakBlock(world, x, y, z, id, metadata);
    }
}
