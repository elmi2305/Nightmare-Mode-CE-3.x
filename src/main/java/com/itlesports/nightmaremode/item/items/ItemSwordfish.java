package com.itlesports.nightmaremode.item.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import net.minecraft.src.*;

public final class ItemSwordfish extends NMFoodItem {
    public ItemSwordfish(int id) { super(id, 1, 0.1F, false, "nmSwordfish", true); }

    @Override public Multimap getItemAttributeModifiers() {
        Multimap modifiers = HashMultimap.create();
        // the player's base attack is one, for five damage before other bonuses.
        modifiers.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(),
                new AttributeModifier(Item.field_111210_e, "Weapon modifier", 4.0D, 0));
        return modifiers;
    }

    @Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return true;
    }
}
