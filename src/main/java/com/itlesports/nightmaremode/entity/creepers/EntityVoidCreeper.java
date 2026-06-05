package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.underworld.EntityBlackHole;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.World;

public class EntityVoidCreeper extends EntityCreeperVariant{
    public EntityVoidCreeper(World w) {
        super(w);
        this.variantType = NMFields.PACKET_CREEPER_VOID;
        this.soundPitchModifier = -0.5f;
        this.fuseTime = 50;
        this.canLunge = false;
        this.explosionRadius = 1;
        this.explosionMultiplier = 1f;
    }

    @Override
    protected void onDeathEffect() {
        this.worldObj.spawnEntityInWorld(new EntityBlackHole(this.worldObj, this.posX, this.posY, this.posZ, 2));
        super.onDeathEffect();
    }
    @Override
    public boolean getCanSpawnHere() {
        return NightmareMode.moreVariants && super.getCanSpawnHere();
    }
}
