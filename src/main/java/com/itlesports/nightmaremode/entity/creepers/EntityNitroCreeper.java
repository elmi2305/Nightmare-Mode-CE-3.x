package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.World;

public class EntityNitroCreeper extends EntityCreeperVariant {
    public EntityNitroCreeper(World par1World) {
        super(par1World);
        this.variantType = NMFields.PACKET_CREEPER_SUPERCRITICAL;
        this.soundPitchModifier = 0.8f;
        this.fuseTime = 15;
        this.explosionMultiplier = 1.4f;
        this.explosionRadius = 6;
        this.patienceCounter = (byte)20;
        this.canLunge = false;
        this.isImmuneToFire = par1World != null && par1World.provider.dimensionId == -1;
    }
    @Override
    public boolean getCanSpawnHere() {
        return (NightmareMode.moreVariants || this.dimension == -1
                && com.itlesports.nightmaremode.worldgen.NetherTierHelper.getTier(this.worldObj, this.posX, this.posZ) >= 3)
                && super.getCanSpawnHere();
    }
}
