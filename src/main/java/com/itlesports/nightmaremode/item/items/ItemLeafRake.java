package com.itlesports.nightmaremode.item.items;

import api.item.items.ToolItem;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.NoMeleeKnockback;
import net.minecraft.src.Block;
import net.minecraft.src.BlockLeaves;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

/** A light tool made solely for gathering leaves. */
public class ItemLeafRake extends ToolItem implements NoMeleeKnockback {
    private final float twigDropChance;

    public ItemLeafRake(int id, EnumToolMaterial material, int durability, float efficiency, float twigDropChance, int damageVsEntity) {
        super(id, 1, material);
        this.setMaxDamage(durability);
        this.efficiencyOnProperMaterial = efficiency;
        this.twigDropChance = twigDropChance;
        this.setDamageVsEntity(damageVsEntity);
    }

    public float getTwigDropChance() {
        return twigDropChance;
    }

    @Override
    public float getStrVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return isLeaf(block) ? efficiencyOnProperMaterial : 0.0F;
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return isLeaf(block);
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, Block block, int x, int y, int z) {
        return isLeaf(block);
    }

    @Override
    public boolean isToolTypeEfficientVsBlockType(Block block) {
        return isLeaf(block);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase user) {
        if (isLeaf(Block.blocksList[blockID])) {
            stack.damageItem(1, user);
        }
        return true;
    }

    @Override
    public boolean getCanBePlacedAsBlock() {
        return false;
    }

    @Override
    public String getModId() {
        return NMFields.modID;
    }

    private static boolean isLeaf(Block block) {
        return block instanceof BlockLeaves;
    }
}
