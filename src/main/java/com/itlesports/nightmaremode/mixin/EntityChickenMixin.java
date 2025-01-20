package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.EntityAnimal;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityChicken.class)
public abstract class EntityChickenMixin extends EntityAnimal {
    public EntityChickenMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (NightmareUtils.getIsEclipse()) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            if (!this.worldObj.isRemote) {
                player.mountEntity(this);
            }
        }
        return super.interact(player);
    }
}
