package com.itlesports.nightmaremode.util;

import api.block.blocks.CropsBlock;
import api.item.items.PickaxeItem;
import btw.item.items.ChiselItem;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemScythe;
import net.minecraft.src.*;

public final class NMBlockBreakingRules {
    public static final float HAND_BREAKABLE_HARDNESS = 0.6F;
    private static final ThreadLocal<Boolean> suppressPlantDrops = new ThreadLocal<>();

    private NMBlockBreakingRules() {}

    public static boolean requiresBlackstoneAuthority(Block block) {
        return block != null && block != Block.oreDiamond && block != Block.oreRedstone && block != Block.oreRedstoneGlowing
                && block != com.itlesports.nightmaremode.block.NMBlocks.nickelOre
                && block != com.itlesports.nightmaremode.block.NMBlocks.lithiumOre;
    }

    public static boolean isSoftBlock(Block block, World world, int x, int y, int z) {
        float hardness = block.getBlockHardness(world, x, y, z);
        if (hardness < 0.0F) return false;
        Material material = block.blockMaterial;
        return hardness <= HAND_BREAKABLE_HARDNESS || material == Material.plants
                || material == Material.vine || material == Material.leaves || material == Material.web
                || material == Material.cloth || material == Material.snow
                || material == Material.craftedSnow || material == Material.sponge;
    }

    public static boolean canAttemptBreak(EntityPlayer player, Block block, World world, int x, int y, int z) {
        if (block == null || player.capabilities.isCreativeMode) return true;
        if (block == Block.anvil) return isSteelMiningTool(player.getCurrentEquippedItem());
        if (block == Block.oreDiamond) return isValidDiamondMiningTool(player.getCurrentEquippedItem());
        if (isSoftBlock(block, world, x, y, z)) return true;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null) return false;
        Item item = held.getItem();
        // conversion tools must still be able to strip bark and perform staged progression actions.
        return item.isEfficientVsBlock(held, world, block, x, y, z)
                || item instanceof ItemTool && ((ItemTool)item).getStrVsBlock(held, block) > 1.0F
                || (item instanceof ChiselItem || item == NMItems.sharpTwig || item == NMItems.sharpBarkTwig)
                && block.canConvertBlock(held, world, x, y, z);
    }

    public static boolean isValidDiamondMiningTool(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        int requiredLevel = EnumToolMaterial.IRON.getHarvestLevel();
        if (item instanceof PickaxeItem pick) return pick.toolMaterial.getHarvestLevel() >= requiredLevel;
        if (item instanceof ChiselItem chisel) return chisel.toolMaterial.getHarvestLevel() >= requiredLevel;
        return item instanceof ItemPickaxe pick && pick.getToolMaterial().getHarvestLevel() >= requiredLevel;
    }

    public static boolean isSteelMiningTool(ItemStack stack) {
        if (stack == null) return false;
        if (stack.getItem() == NMItems.coresteelPickaxe || stack.getItem() == NMItems.quicksilverPickaxe) return true;
        int requiredLevel = EnumToolMaterial.SOULFORGED_STEEL.getHarvestLevel();
        if (stack.getItem() instanceof PickaxeItem pick) return pick.toolMaterial.getHarvestLevel() >= requiredLevel;
        return stack.getItem() instanceof ItemPickaxe pick && pick.getToolMaterial().getHarvestLevel() >= requiredLevel;
    }

    public static float getBreakingSpeed(Block block, EntityPlayer player, World world, int x, int y, int z) {
        if (!canAttemptBreak(player, block, world, x, y, z)) return 0.0F;
        float speed = block.getPlayerRelativeBlockHardness(player, world, x, y, z);
        // a tool with zero strength (such as an unrelated hammer) can still clear soft blocks.
        if (!(speed > 0.0F) && isSoftBlock(block, world, x, y, z)) {
            float hardness = block.getBlockHardness(world, x, y, z);
            return hardness == 0.0F ? 1.0F : (com.itlesports.nightmaremode.world.BalanceProfile.isEasy() ? 2.0F : 1.0F) / (hardness * 200.0F);
        }
        return speed;
    }

    public static boolean canHarvestPlant(EntityPlayer player, Block block) {
        if (!(block instanceof CropsBlock || block instanceof BlockCrops)) return true;
        ItemStack held = player.getCurrentEquippedItem();
        return held != null && held.getItem() instanceof ItemScythe;
    }

    public static boolean arePlantDropsSuppressed() {
        return Boolean.TRUE.equals(suppressPlantDrops.get());
    }

    public static boolean removeBlock(World world, EntityPlayer player, Block block, int x, int y, int z) {
        boolean previous = arePlantDropsSuppressed();
        suppressPlantDrops.set(previous || !canHarvestPlant(player, block));
        try {
            // wheat's second half drops during removal, before harvestBlock is called.
            return world.setBlockToAir(x, y, z);
        } finally {
            if (previous) suppressPlantDrops.set(true);
            else suppressPlantDrops.remove();
        }
    }
}
