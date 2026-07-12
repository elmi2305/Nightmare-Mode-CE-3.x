package com.itlesports.nightmaremode.mixin.entity;

import btw.entity.item.FloatingItemEntity;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.blocks.templates.NMPlaceAsBlockItem;
import com.itlesports.nightmaremode.item.NMPostItems;
import com.itlesports.nightmaremode.item.items.template.NMItem;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    @Unique private int ticksInDesiredFluid;
    @Shadow public abstract ItemStack getEntityItem();

    public EntityItemMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void doWaterCheck(CallbackInfo ci){
        if(this.getEntityItem() != null){
            if (this.getEntityItem().getItem() instanceof NMPlaceAsBlockItem item && item.getWashResult() != -1) {
                int id = item.itemID;
                if(id == NMPostItems.stompedCrushedIronStoneMix.itemID && this.isInsideOfMaterial(Material.water)){
                    this.ticksInDesiredFluid++;
                }


                if(this.ticksInDesiredFluid >= 480){
                    summonEntity(item);
                    this.worldObj.playAuxSFX(2222, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);

                    this.setDead();
                }
            }
            else if (this.getEntityItem().getItem() instanceof NMItem item && item.getWashResult() != -1){
                int id = item.itemID;
                if(id == NMPostItems.stompedCrushedIronStoneMix.itemID && this.isInsideOfMaterial(Material.water)){
                    this.ticksInDesiredFluid++;
                }


                if(this.ticksInDesiredFluid >= 4800){
                    summonEntity(item);
                    this.worldObj.playAuxSFX(2222, MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 0);

                    this.setDead();
                }
            }
        }
    }
    @Unique
    private void summonEntity(Item item){
        int meta = 0;
        this.worldObj.spawnEntityInWorld(new FloatingItemEntity(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(item, 1, meta)));
    }
    @Unique
    private void summonEntity(Item item, int m){
        int meta = m;
        this.worldObj.spawnEntityInWorld(new FloatingItemEntity(this.worldObj, this.posX, this.posY, this.posZ, new ItemStack(item, 1, meta)));
    }
}
