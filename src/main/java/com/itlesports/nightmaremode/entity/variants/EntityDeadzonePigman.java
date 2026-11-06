package com.itlesports.nightmaremode.entity.variants;

import com.itlesports.nightmaremode.world.BalanceProfile;

import net.minecraft.src.World;
import net.minecraft.src.EntityPlayer;

public class EntityDeadzonePigman extends EntityNetherPigZombieVariant {
    public EntityDeadzonePigman(World world) {
        super(world);
    }

    @Override protected int getMinimumNetherTier() { return 3; }
    @Override protected int getFireSecondsOnHit() { return 6; }
    @Override protected double getVariantHealth() { return BalanceProfile.isCombatEasy() ? 40.0D : 110.0D; }
    @Override protected double getVariantDamage() { return BalanceProfile.isCombatEasy() ? 5.0D : 16.0D; }

    @Override
    public void onUpdate() {
        if (this instanceof com.itlesports.nightmaremode.util.interfaces.CarcassAnimal carcass && carcass.nm$isCarcass()) {
            carcass.nm$tickCarcass();
            return;
        }
        super.onUpdate();
        if (!BalanceProfile.isCombatEasy() && this.entityToAttack == null && (this.ticksExisted + this.entityId) % 20 == 0) {
            EntityPlayer player = this.worldObj.getClosestVulnerablePlayerToEntity(this, 32.0D);
            if (player != null) {
                this.entityToAttack = player;
            }
        }
    }
}
