package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.underworld.IUnderworldTieredBlock;
import com.itlesports.nightmaremode.util.underworld.UnderworldToolTier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class BlockUnderworldOre extends NMBlock implements IUnderworldTieredBlock {
    public static final int TITANIUM = 0;
    public static final int TUNGSTEN = 1;
    public static final int TYPE_MASK = 1;
    private Icon[] titaniumIcons;
    private Icon[] tungstenIcons;

    public BlockUnderworldOre(int id) {
        super(id, Material.rock);
        this.setHardness(18.0F);
        this.setResistance(6000.0F);
        this.setPicksEffectiveOn();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setUnlocalizedName("nmUnderworldOre");
    }

    @Override
    public UnderworldToolTier getRequiredUnderworldTier(IBlockAccess world, int x, int y, int z) {
        return getOreType(world.getBlockMetadata(x, y, z)) == TUNGSTEN ? UnderworldToolTier.TITANIUM : UnderworldToolTier.STEEL;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return getOreType(world.getBlockMetadata(x, y, z)) == TUNGSTEN ? 5 : 3;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return getOreType(metadata) == TUNGSTEN ? NMItems.rawTungsten.itemID : NMItems.rawTitanium.itemID;
    }

    @Override public int damageDropped(int metadata) { return 0; }
    @Override protected boolean canSilkHarvest(int metadata) { return false; }
    @Override public boolean canDropFromExplosion(Explosion explosion) { return false; }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        return 1 + (fortune > 0 && random.nextInt(fortune + 2) > 1 ? 1 : 0);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        titaniumIcons = new Icon[3];
        tungstenIcons = new Icon[3];
        for (int strata = 0; strata < 3; strata++) {
            String suffix = strata == 0 ? "" : "_strata_" + strata;
            titaniumIcons[strata] = register.registerIcon("nightmare:nmTitaniumOre" + suffix);
            tungstenIcons[strata] = register.registerIcon("nightmare:nmTungstenOre" + suffix);
        }
    }

    @Environment(EnvType.CLIENT)
    @Override public Icon getIcon(int side, int metadata) {
        int strata = Math.min(2, metadata >> 1 & 3);
        return getOreType(metadata) == TUNGSTEN ? tungstenIcons[strata] : titaniumIcons[strata];
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void getSubBlocks(int id, CreativeTabs tab, List list) {
        list.add(new ItemStack(id, 1, TITANIUM));
        list.add(new ItemStack(id, 1, TUNGSTEN));
    }

    public static int getOreType(int metadata) {
        return metadata & TYPE_MASK;
    }

    public static int withHostStrata(int oreType, int hostMetadata) {
        return oreType & TYPE_MASK | (hostMetadata & 3) << 1;
    }
}
