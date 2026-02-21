package com.itlesports.nightmaremode.entity.creepers;

import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.World;

public class EntityNitroCreeper extends EntityCreeperVariant {
    public EntityNitroCreeper(World par1World) {
        super(par1World);
        this.variantType = NMFields.CREEPER_SUPERCRITICAL;
        this.soundPitchModifier = 0.8f;
        this.fuseTime = 15;
        this.explosionMultiplier = 1.4f;
        this.explosionRadius = 6;
        this.patienceCounter = (byte)20;
        this.canLunge = false;
    }
}
