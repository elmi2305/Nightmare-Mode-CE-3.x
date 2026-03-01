package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.FlowerZombie;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.EntityZombieImposter;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public class RenderZombieVariant extends RenderZombie {
    private static final ResourceLocation bz0 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame00.png");
    private static final ResourceLocation bz1 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame11.png");
    private static final ResourceLocation bz2 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame22.png");
    private static final ResourceLocation bz3 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame33.png");
    private static final ResourceLocation bz4 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame44.png");


    private static final ResourceLocation izTextures = new ResourceLocation("nightmare:textures/entity/zombieImposter.png");

    private static final ResourceLocation fzTextures = new ResourceLocation("nightmare:textures/entity/flowerZombie.png");



    private static final ResourceLocation szTextures = new ResourceLocation("nightmare:textures/entity/shadowzombie.png");
    private static final ResourceLocation szTexturesEclipse = new ResourceLocation("nightmare:textures/entity/shadowzombieEclipse.png");

    @Override protected ResourceLocation func_110863_a(EntityZombie zombie) {
        if(zombie instanceof EntityBloodZombie){
            return this.getTextureForTicksExisted(zombie.ticksExisted);
        }
        if(zombie instanceof EntityZombieImposter){
            return izTextures;
        }
        if(zombie instanceof FlowerZombie){
            return fzTextures;
        }
        return NMUtils.getIsMobEclipsed(zombie) ? szTexturesEclipse : szTextures;
    }

    @Unique
    private ResourceLocation getTextureForTicksExisted(int ticks){
        int ticksMod100 = ticks % 100;
        if(ticksMod100 < 88){
            return bz0;
        }
        else if(ticksMod100 < 91){
            return bz1;
        }
        else if(ticksMod100 < 94){
            return bz2;
        }
        else if(ticksMod100 < 97){
            return bz3;
        }
        else {
            return bz4;
        }
    }
}
