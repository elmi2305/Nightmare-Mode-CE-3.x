package com.itlesports.nightmaremode.mixin;

import btw.world.util.difficulty.Difficulties;
import com.itlesports.nightmaremode.NightmareUtils;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityPigZombie.class)
public class EntityPigZombieMixin extends EntityZombie {
    public EntityPigZombieMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void attackNearestPlayer(CallbackInfo ci){
        double range = this.worldObj.getDifficulty() == Difficulties.HOSTILE ? 3.0 : 2.0;
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(range, range, range));
        for (Object tempEntity : list) {
            if (!(tempEntity instanceof EntityPlayer player)) continue;
            if (this.isPlayerWearingGoldArmor(player)) continue;
            this.entityToAttack = player;
            break;
        }
    }
    @Inject(method = "applyEntityAttributes", at = @At("TAIL"))
    private void applyAdditionalAttributes(CallbackInfo ci){
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(16 + 4 * NightmareUtils.getWorldProgress(this.worldObj));
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setAttribute(3 + 2 * NightmareUtils.getWorldProgress(this.worldObj));
    }

    @Inject(method = "dropFewItems", at = @At("TAIL"))
    private void allowBloodOrbDrops(boolean bKilledByPlayer, int iLootingModifier, CallbackInfo ci){
        int bloodOrbID = NightmareUtils.getIsBloodMoon() ? NMItems.bloodOrb.itemID : 0;
        if (bloodOrbID > 0) {
            int var4 = this.rand.nextInt(2);
            // 0 - 1
            if (iLootingModifier > 0) {
                var4 += this.rand.nextInt(iLootingModifier + 1);
            }
            for (int var5 = 0; var5 < var4; ++var5) {
                this.dropItem(bloodOrbID, 1);
            }
        }
    }

    @Unique boolean isPlayerWearingGoldArmor(EntityPlayer player){
        return (player.getCurrentItemOrArmor(1) != null && player.getCurrentItemOrArmor(1).itemID == Item.bootsGold.itemID)
                || (player.getCurrentItemOrArmor(2) != null && player.getCurrentItemOrArmor(2).itemID == Item.legsGold.itemID)
                || (player.getCurrentItemOrArmor(3) != null && player.getCurrentItemOrArmor(3).itemID == Item.plateGold.itemID)
                || (player.getCurrentItemOrArmor(4) != null && player.getCurrentItemOrArmor(4).itemID == Item.helmetGold.itemID);
    }
}
