package com.itlesports.nightmaremode.mixin.biomegen;

import com.itlesports.nightmaremode.entity.variants.EntityRainSpider;
import com.itlesports.nightmaremode.util.elements.NMEvents;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BiomeGenBase.class)
public class BiomeGenBaseMixin {
    @Shadow protected List spawnableMonsterList;
    @Shadow protected List spawnableWaterCreatureList;


    @Inject(method = "getSpawnableList", at = @At("HEAD"), cancellable = true)
    private void spiders(EnumCreatureType type, CallbackInfoReturnable<List> cir){
        if(type == EnumCreatureType.monster){
            if(NMEvents.SimpleEvent.SPIDER_RAIN.isActive()){
                List temp = this.spawnableMonsterList;
                temp.add(new SpawnListEntry(EntityRainSpider.class, 100, 1, 4));
                cir.setReturnValue(temp);
                return;
            }

            if(NMEvents.SimpleEvent.HELL.isActive()){
                List temp = this.spawnableMonsterList;
                temp.add(new SpawnListEntry(EntityPigZombie.class, 3, 1, 1));
                temp.add(new SpawnListEntry(EntityGhast.class, 1, 1, 1));
                temp.add(new SpawnListEntry(EntityMagmaCube.class, 2, 1, 1));
                cir.setReturnValue(temp);
                return;
            }
        }
    }
}
