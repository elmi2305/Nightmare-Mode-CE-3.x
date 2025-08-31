package com.itlesports.nightmaremode.mixin;

import btw.achievement.event.AchievementEventDispatcher;
import btw.achievement.event.BTWAchievementEvents;
import btw.entity.mob.villager.LibrarianVillagerEntity;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LibrarianVillagerEntity.class)
public class LibrarianVillagerEntityMixin extends EntityVillager {
    @Unique private int conversionCountdown = 600;

    public LibrarianVillagerEntityMixin(World par1World) {
        super(par1World);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(this.worldObj != null && NMUtils.getIsEclipse()){
            if (this.conversionCountdown > 0) {
                this.conversionCountdown -= 1;
            } else if (!this.worldObj.isRemote){
                for (Object o : this.worldObj.playerEntities) {
                    EntityPlayer player = (EntityPlayer)o;
                    AchievementEventDispatcher.triggerEvent(NMAchievementEvents.NightmareMerchantEvent.class, player, BTWAchievementEvents.none());
                }
                NightmareVillager villager = new NightmareVillager(this.worldObj);
                villager.setPositionAndUpdate(this.posX,this.posY,this.posZ);
                this.worldObj.spawnEntityInWorld(villager);
                this.setDead();
            }
        }
    }
}
