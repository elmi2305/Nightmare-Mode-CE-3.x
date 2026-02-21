package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class EntityDungCreeper extends EntityCreeperVariant{
    public EntityDungCreeper(World par1World) {
        super(par1World);
        this.variantType = NMFields.CREEPER_DUNG;
    }

    @Override
    protected void onDeathEffect() {
        super.onDeathEffect();
        int amount = NightmareMode.isAprilFools ? 12 : 4;
        for(int i = 0; i < amount; i++){
            spawnItemExplosion(this.worldObj,this, new ItemStack(BTWItems.dung), amount / 4,this.rand);
        }
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);
        if (this.getNeuteredState() == 0 && this.rand.nextInt(100) == 0) {
            this.dropItem(NMItems.dungApple.itemID, 1);
        }
    }
}
