package com.itlesports.nightmaremode.entity;

import btw.entity.BroadheadArrowEntity;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class EntityMagicArrow extends EntityArrow {

    public EntityMagicArrow(World world, EntityLivingBase entityLiving, float f) {
        super(world, entityLiving, f);
    }

    public Item getCorrespondingItem() {
        return NMItems.magicArrow;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.isDead && this.inGround) {
            for (int i = 0; i < 32; ++i) {
                this.worldObj.spawnParticle("iconcrack_266", this.posX, this.posY, this.posZ, (float)(Math.random() * 2.0 - 1.0) * 0.4f, (float)(Math.random() * 2.0 - 1.0) * 0.4f, (float)(Math.random() * 2.0 - 1.0) * 0.4f);
            }
            this.setDead();
        }
    }
}
