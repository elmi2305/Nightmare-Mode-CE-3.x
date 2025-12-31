package com.itlesports.nightmaremode.mixin.entity;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityChicken.class)
public abstract class EntityChickenMixin extends EntityAnimal {
    @Shadow protected abstract String getLivingSound();
    @Shadow protected abstract String getHurtSound();
    @Shadow public abstract void onBecomeFamished();

    public EntityChickenMixin(World par1World) {
        super(par1World);
    }

    @Unique
    private int flightTimer;
    @Inject(method = "<init>", at = @At("TAIL"))
    private void manageEclipseChance(World world, CallbackInfo ci){
        NMUtils.manageEclipseChance(this,32);
    }

    @Inject(method = "updateHungerState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityChicken;dropItem(II)Lnet/minecraft/src/EntityItem;"))
    private void chanceToLayMultipleEggs(CallbackInfo ci){
        if(this.rand.nextInt(3) == 0){
            this.dropItem(Item.egg.itemID, 1);
            if(this.rand.nextInt(3) == 0){
                this.dropItem(Item.egg.itemID, 1);
            }
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2) {
        Entity attacker = par1DamageSource.getSourceOfDamage();
        if (attacker != null && NMUtils.getIsMobEclipsed(this)) {
            Entity lightningbolt = new EntityLightningBolt(this.worldObj, attacker.posX, attacker.posY, attacker.posZ);
            this.worldObj.addWeatherEffect(lightningbolt);
        }
        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    public boolean interact(EntityPlayer player) {
//        ItemStack stack = player.inventory.getCurrentItem();
//        System.out.println(this.getHungerLevel());
//        if (stack != null && stack.getItem() instanceof ItemShears && this.isFullyFed() && !this.isChild()) {
//            if (!this.worldObj.isRemote) {
//                this.onBecomeFamished();
//                int lootingModifier = this.getAmbientLootingModifier();
//                int playerLootingModifier = EnchantmentHelper.getLootingModifier(player);
//                if (playerLootingModifier > lootingModifier) {
//                    lootingModifier = playerLootingModifier;
//                }
//                int iNumItems = 1 + this.rand.nextInt(1 + lootingModifier);
//                for (int iTempCount = 0; iTempCount < iNumItems; ++iTempCount) {
//                    EntityItem tempItem = this.entityDropItem(new ItemStack(Item.feather.itemID, 1, 0), 1.0f);
//                    tempItem.motionY += (double)(this.rand.nextFloat() * 0.05f);
//                    tempItem.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f);
//                    tempItem.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f);
//                }
//            }
//            stack.damageItem(1, player);
//            this.playSound("mob.sheep.shear", 1.0f, 1.0f);
//            this.attackEntityFrom(DamageSource.generic, 0.0f);
//            if (stack.stackSize <= 0) {
//                player.inventory.mainInventory[player.inventory.currentItem] = null;
//            }
//        }
//        else

        if (NMUtils.getIsMobEclipsed(this)) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            if (!this.worldObj.isRemote) {
                player.mountEntity(this);
            }
        }
        return super.interact(player);
    }
    @Unique public boolean isTired(){
        return this.isPotionActive(Potion.damageBoost);
        // mirror of isChickenTired in EntityPlayerMPMixin
    }

    @Inject(method = "onLivingUpdate", at = @At("TAIL"))
    private void ensureMaxHealthAndManageFlightTimer(CallbackInfo ci){
        if (this.riddenByEntity instanceof EntityPlayer) {
            if (!this.isTired()) {
                this.flightTimer += 2;
            }

            if(this.flightTimer > 1600){
                this.addPotionEffect(new PotionEffect(Potion.damageBoost.id, 1600, 0));
                this.playSound(this.getHurtSound(), 1.0f, 0.5f);
                this.flightTimer = 0;
                // this is simply used to communicate with the player mixin that
            }
        }

        if(this.ticksExisted % 120 != 0) return;
        int originalHealth = 4 + NMUtils.getWorldProgress() * 2;
        double eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 4 : 1;
        if(this.getMaxHealth() != originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier){
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(originalHealth * NMUtils.getNiteMultiplier() * eclipseModifier);
        }
    }


    @Inject(method = "dropFewItems", at = @At("HEAD"), cancellable = true)
    private void manageEclipseShardDrops(boolean bKilledByPlayer, int lootingLevel, CallbackInfo ci){
        if (bKilledByPlayer && NMUtils.getIsMobEclipsed(this)) {
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
        if(!this.isFullyFed()){
            ci.cancel();
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((4.0d + NMUtils.getWorldProgress() * 2) * NMUtils.getNiteMultiplier());
    }
}
