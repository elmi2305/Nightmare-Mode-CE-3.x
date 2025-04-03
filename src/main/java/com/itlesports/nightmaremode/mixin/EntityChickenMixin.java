package com.itlesports.nightmaremode.mixin;

import btw.entity.LightningBoltEntity;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityChicken.class)
public abstract class EntityChickenMixin extends EntityAnimal {
    public EntityChickenMixin(World par1World) {
        super(par1World);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NightmareUtils.manageEclipseChance(this,32);
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        Entity attacker = par1DamageSource.getSourceOfDamage();
        if (attacker != null && NightmareUtils.getIsMobEclipsed(this)) {
            Entity lightningbolt = new LightningBoltEntity(this.worldObj, attacker.posX, attacker.posY, attacker.posZ);
            this.worldObj.addWeatherEffect(lightningbolt);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (NightmareUtils.getIsMobEclipsed(this)) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            if (!this.worldObj.isRemote) {
                player.mountEntity(this);
            }
        }
        return super.interact(player);
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void manageJumpAttackAtPlayer(CallbackInfo ci){
        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 4;
        double eclipseModifier = NightmareUtils.getIsMobEclipsed(this) ? 4 : 1;
        if(this.getMaxHealth() != originalHealth * NightmareUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NightmareUtils.getNiteMultiplier() * eclipseModifier);
        }
    }


    @Inject(method = "dropFewItems", at = @At("HEAD"))
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NightmareUtils.getIsMobEclipsed(this)) {
            for(int i = 0; i < (lootingLevel * 2) + 1; i++) {
                if (this.rand.nextInt(8) == 0) {
                    this.dropItem(NMItems.darksunFragment.itemID, 1);
                    if (this.rand.nextBoolean()) {
                        break;
                    }
                }
            }

            int itemID = NMItems.magicFeather.itemID;

            int var4 = this.rand.nextInt(3);
            if (lootingLevel > 0) {
                var4 += this.rand.nextInt(lootingLevel + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                if(this.rand.nextInt(3) == 0) continue;
                this.dropItem(itemID, 1);
            }
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(4.0d * NightmareUtils.getNiteMultiplier());
    }
}
