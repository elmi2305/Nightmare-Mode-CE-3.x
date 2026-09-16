package com.itlesports.nightmaremode.block.blocks;

import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.block.blocks.templates.NMBlock;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class CrystalPocketBlock extends NMBlock {
    private static final int MAX_ATTEMPTS = 4;
    private static final int ATTEMPT_MASK = 3;
    private static final int STRATA_MASK = 12;
    @Environment(EnvType.CLIENT)
    private Icon[] iconByMetadataArray;

    public CrystalPocketBlock(int id) {
        super(id, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(8.0F);
        this.setResistance(8.0F);
        this.setPicksEffectiveOn();
        this.setChiselsEffectiveOn();
        this.setChiselsCanHarvest(true);
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess world, int x, int y, int z) {
        return 2;
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess world, int x, int y, int z) {
        return 2;
    }

    public boolean isValidMiningTool(ItemStack held, World world, int x, int y, int z) {
        if (held == null) {
            return false;
        }
        Item item = held.getItem();
        return (item instanceof PickaxeItem || item instanceof ChiselItem)
                && item.canHarvestBlock(held, world, this, x, y, z);
    }

    public boolean minePocket(World world, EntityPlayer player, int x, int y, int z, int fromSide) {
        if (!SkillHandler.getPlayerData(player).canMineCrystals) {
            SkillHandler.sendStatus(player, "Requires skill: Witch Hunter - Kill 4 witches.");
            return false;
        }

        float chance = Math.min(1.0F, 0.5F + SkillHandler.getPlayerData(player).crystalDropChanceBonus);
        if (world.rand.nextFloat() < chance) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                    new ItemStack(NMItems.crystalUncleanedShard), fromSide);
        }
        player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        player.addHarvestBlockExhaustion(this.blockID, x, y, z, 0);

        int metadata = world.getBlockMetadata(x, y, z);
        int attempts = metadata & ATTEMPT_MASK;
        if (attempts >= MAX_ATTEMPTS - 1) {
            world.setBlockToAir(x, y, z);
        } else {
            world.setBlockMetadataWithNotify(x, y, z,
                    (metadata & STRATA_MASK) | (attempts + 1), 3);
        }
        return true;
    }

    @Override
    public boolean hasStrata() {
        return true;
    }

    @Override
    public int getMetadataConversionForStrataLevel(int strataLevel, int metadata) {
        return (metadata & ATTEMPT_MASK) | (strataLevel << 2);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconByMetadataArray = new Icon[16];
        super.registerIcons(register);
        Icon strataOne = this.blockIcon;
        Icon strataTwo = register.registerIcon(this.getTextureName() + "_strata_2");
        Icon strataThree = register.registerIcon(this.getTextureName() + "_strata_3");
        for (int metadata = 0; metadata < 16; ++metadata) {
            int strata = (metadata & STRATA_MASK) >> 2;
            this.iconByMetadataArray[metadata] = switch (strata) {
                case 1 -> strataTwo;
                case 2 -> strataThree;
                default -> strataOne;
            };
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return this.iconByMetadataArray[metadata & 15];
    }

    @Override
    public int idDropped(int metadata, java.util.Random random, int fortune) {
        return 0;
    }

    @Override
    public int quantityDropped(java.util.Random random) {
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
    }
}
