package com.itlesports.nightmaremode.skill;

import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import btw.item.BTWItems;
import net.minecraft.src.*;

public final class SkillMiningRewards {
    private SkillMiningRewards() {}

    /** Called once after a successful conversion or ordinary harvest, using its actual miner. */
    public static void award(EntityPlayer player, Block block, ItemStack tool, World world,
                             int x, int y, int z, int side) {
        if (world.isRemote || player == null || player.capabilities.isCreativeMode || tool == null
                || !(tool.getItem() instanceof PickaxeItem pickaxe)
                || pickaxe.toolMaterial.getHarvestLevel() < 1
                || block != Block.oreIron && block != Block.oreCoal) return;
        SkillTreeData data = SkillHandler.getPlayerData(player);
        int bonus = block == Block.oreIron ? data.ironDustDropBonus : data.coalDustDropBonus;
        Item drop = block == Block.oreIron ? BTWItems.ironOrePile : BTWItems.coalDust;
        for (int i = 0; i < bonus; ++i) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(drop), side);
        }
    }
}
