package com.itlesports.nightmaremode.block.blocks;

import api.item.util.ItemUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TerrainExtractorTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerTerrainExtractor;
import net.minecraft.src.*;

import java.util.List;

public class BlockTerrainExtractor extends BlockContainer {
    public static final String[] TYPES = {"Potassium", "Nitrogen", "Moisture", "Porosity", "Acidity"};

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
    public TileEntity createNewTileEntity(World world) {
        return new TerrainExtractorTileEntity();
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata % TYPES.length;
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
