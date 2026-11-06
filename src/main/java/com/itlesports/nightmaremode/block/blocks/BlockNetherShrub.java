package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;

import java.util.Random;

public class BlockNetherShrub extends BlockFlower {
    public static final int STICK_DROP_CHANCE = 4;

    public BlockNetherShrub(int id) {
        super(id, Material.vine);
        this.setHardness(0.5F);
        this.setStepSound(Block.soundGrassFootstep);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setUnlocalizedName("ifhyNetherShrub");
        this.setTextureName("nightmare:ifhyNetherShrub");
    }

    @Override
    protected boolean canGrowOnBlock(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == Block.netherrack.blockID;
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return random.nextInt(STICK_DROP_CHANCE) == 0 ? NMItems.netherStick.itemID : -1;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float chance, int fortune) {
        if (world.isRemote) return;
        super.dropBlockAsItemWithChance(world, x, y, z, metadata, chance, fortune);
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 8.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).twigDropChanceBonus;
        if (bonus > 0.0F && world.rand.nextFloat() < bonus * chance) {
            this.dropBlockAsItem_do(world, x, y, z, new ItemStack(NMItems.netherStick));
        }
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }
}
