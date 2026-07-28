package com.itlesports.nightmaremode.item.items;

import api.achievement.AchievementEventDispatcher;
import api.item.items.SwordItem;
import api.util.MiscUtils;
import api.world.BlockPos;
import api.world.WorldUtils;
import btw.achievement.BTWAchievementEvents;
import btw.block.BTWBlocks;
import btw.block.tileentity.PlacedToolTileEntity;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.src.*;

public class ItemScythe extends SwordItem {
    private final float weaponDamage;

    public ItemScythe(int id, EnumToolMaterial material, float weaponDamage) {
        super(id, material);
        this.weaponDamage = weaponDamage;
    }

    @Override
    public boolean isEnchantmentApplicable(Enchantment enchantment) {
        return enchantment == Enchantment.sharpness
                || enchantment == Enchantment.smite
                || enchantment == Enchantment.looting
                || enchantment == Enchantment.unbreaking;
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        modifiers.put(
                SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", this.weaponDamage, 0)
        );
        return modifiers;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        return par1ItemStack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int i, int j, int k, int iFacing, float fClickX, float fClickY, float fClickZ) {
        return false;
    }

    protected boolean canToolStickInBlock(ItemStack stack, Block block, World world, int i, int j, int k) {
        return false;
    }

    protected boolean getCanBePlacedAsBlock() {
        return false;
    }

}
