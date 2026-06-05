package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;

import static com.itlesports.nightmaremode.util.NMFields.PACKET_CREEPER_GEL;

public class EntityGelCreeper extends EntityCreeperVariant {
    public EntityGelCreeper(World w) {
        super(w);
        this.variantType = PACKET_CREEPER_GEL;
    }
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(4);
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float amount) {
        if(src.getSourceOfDamage() instanceof EntityPlayer p) {
            if(p.getHeldItem() != null) {
                this.playSound("mob.slime.attack", 1.0f, this.getSoundPitch() * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f));

                p.dropOneItem(true);
                p.addPotionEffect(new PotionEffect(Potion.digSlowdown.getId(), 120, 0));
            }
        }
        return super.attackEntityFrom(src, amount);
    }

    @Override
    protected int shouldSpawnCharged() {
        return 0;
    }

    @Override
    public boolean getCanSpawnHere() {
        return NightmareMode.moreVariants && super.getCanSpawnHere();
    }
}
