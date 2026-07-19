package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlockOre;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;
import java.util.Random;

public class ProcessingOreBlock extends NMBlockOre {
    private final int dropItemID;
    private final int requiredToolLevel;

    public ProcessingOreBlock(int id, int dropItemID, int requiredToolLevel) {
        super(id);
        this.dropItemID = dropItemID;
        this.requiredToolLevel = requiredToolLevel;
        this.setHardness(3.0F);
        this.setResistance(5.0F);
        this.setPicksEffectiveOn();
    }

    @Override
    public boolean canBeMined(IBlockAccess world, int i, int j, int k) {
        return true;
    }

    @Override
    public int idDropped(int metadata, Random rand, int fortune) {
        return this.dropItemID;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
        if (world.isRemote) {
            return;
        }
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 8.0D);
        if (player == null) {
            return;
        }
        if (this == NMBlocks.lithiumOre && SkillHandler.getPlayerData(player).doubleLithiumDrops) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.dropItemID, 1, 0));
        } else if (this == NMBlocks.nickelOre
                && world.rand.nextFloat() < SkillHandler.getPlayerData(player).doubleNickelRockChance) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(this.dropItemID, 1, 0));
        }
    }

    @Override
    public int getRequiredToolLevelForOre(IBlockAccess blockAccess, int i, int j, int k) {
        return this.requiredToolLevel;
    }

    @Override
    public int idDroppedOnConversion(boolean dropPiles, int metadata) {
        return this.dropItemID;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
    }
}
