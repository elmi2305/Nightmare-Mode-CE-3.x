package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.entity.underworld.EntityBlackHole;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.World;

public class EntityVoidCreeper extends EntityCreeperVariant{
    public EntityVoidCreeper(World w) {
        super(w);
        this.variantType = NMFields.PACKET_CREEPER_VOID;
        this.soundPitchModifier = -0.5f;
        this.fuseTime = 50;
        this.canLunge = false;
        this.explosionRadius = 0;
        this.explosionMultiplier = 0f;
    }

    @Override
    protected void onDeathEffect() {
        this.worldObj.spawnEntityInWorld(new EntityBlackHole(this.worldObj, this.posX, this.posY, this.posZ, 1 + ((double) NMUtils.getWorldProgress() / 3)));
        super.onDeathEffect();
    }
    @Override
    public boolean getCanSpawnHere() {
        return NightmareMode.moreVariants && super.getCanSpawnHere();
    }
}
