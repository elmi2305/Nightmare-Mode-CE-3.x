package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.EntitySilverfish;
import net.minecraft.src.SharedMonsterAttributes;
import net.minecraft.src.World;

public class EntityEnderSilverfish extends EntitySilverfish {
    public EntityEnderSilverfish(World world) { super(world); }

    @Override protected void dropFewItems(boolean killedByPlayer, int looting) {
        this.dropItem(NMItems.enderShell.itemID, 1 + this.rand.nextInt(2) + (looting > 0 ? this.rand.nextInt(looting + 1) : 0));
        super.dropFewItems(killedByPlayer, looting);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(42.0);
        this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setAttribute(2);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute(0.7f);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(18.0);
    }
}
