/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.EntityZombieImposter;
import com.itlesports.nightmaremode.entity.underworld.FlowerZombie;
import com.itlesports.nightmaremode.entity.variants.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.variants.EntityShadowZombie;
import com.itlesports.nightmaremode.entity.variants.EntityStoneZombie;
import com.itlesports.nightmaremode.entity.zombies.EntityZombieVariant;
import com.itlesports.nightmaremode.util.NMUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelZombie;
import net.minecraft.src.RenderBiped;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

@Environment(value=EnvType.CLIENT)
public class RenderEntityZombieVariant
        extends RenderBiped {
    protected ModelBiped field_82437_k;
    protected ModelBiped field_82435_l;


    private static final ResourceLocation bz0 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame00.png");
    private static final ResourceLocation bz1 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame11.png");
    private static final ResourceLocation bz2 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame22.png");
    private static final ResourceLocation bz3 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame33.png");
    private static final ResourceLocation bz4 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame44.png");

    private static final ResourceLocation IMPOSTER = new ResourceLocation("nightmare:textures/entity/zombieImposter.png");
    private static final ResourceLocation FLOWER = new ResourceLocation("nightmare:textures/entity/flowerZombie.png");
    private static final ResourceLocation STONE = new ResourceLocation("nightmare:textures/entity/zombieStone.png");
    private static final ResourceLocation SHADOW = new ResourceLocation("nightmare:textures/entity/shadowzombie.png");
    private static final ResourceLocation SHADOW_ECLIPSE = new ResourceLocation("nightmare:textures/entity/shadowzombieEclipse.png");


    public RenderEntityZombieVariant() {
        super(new ModelZombie(), 0.5f, 1.0f);
    }

    @Override
    protected void func_82421_b() {
        this.field_82423_g = new ModelZombie(1.0f, true);
        this.field_82425_h = new ModelZombie(0.5f, true);
        this.field_82437_k = this.field_82423_g;
        this.field_82435_l = this.field_82425_h;
    }

    protected int func_82429_a(EntityZombieVariant par1EntityZombie, int par2, float par3) {
        return super.func_130006_a(par1EntityZombie, par2, par3);
    }

    public void func_82426_a(EntityZombieVariant par1EntityZombie, double par2, double par4, double par6, float par8, float par9) {
        super.doRenderLiving(par1EntityZombie, par2, par4, par6, par8, par9);
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


    protected ResourceLocation func_110863_a(EntityZombieVariant zombie) {
        if(zombie instanceof EntityBloodZombie){
            return this.getTextureForTicksExisted(zombie.ticksExisted);
        }
        if(zombie instanceof EntityShadowZombie){
            return NMUtils.getIsMobEclipsed(zombie) ? SHADOW_ECLIPSE : SHADOW;
        }
        if(zombie instanceof EntityStoneZombie) {
            return STONE;
        }
        if(zombie instanceof EntityZombieImposter){
            return IMPOSTER;
        }
        if(zombie instanceof FlowerZombie){
            return FLOWER;
        }
        return null;
    }

    protected void func_82428_a(EntityZombieVariant par1EntityZombie, float par2) {
        super.func_130005_c(par1EntityZombie, par2);
    }


    protected void func_82430_a(EntityZombieVariant par1EntityZombie, float par2, float par3, float par4) {
        if (par1EntityZombie.isConverting()) {
            par3 += (float)(Math.cos((double)par1EntityZombie.ticksExisted * 3.25) * Math.PI * 0.25);
        }
        super.rotateCorpse(par1EntityZombie, par2, par3, par4);
    }

    @Override
    protected void func_130005_c(EntityLiving par1EntityLiving, float par2) {
        this.func_82428_a((EntityZombieVariant)par1EntityLiving, par2);
    }

    @Override
    protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving) {
        return this.func_110863_a((EntityZombieVariant)par1EntityLiving);
    }

    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityZombieVariant)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    protected int func_130006_a(EntityLiving par1EntityLiving, int par2, float par3) {
        return this.func_82429_a((EntityZombieVariant)par1EntityLiving, par2, par3);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.func_82429_a((EntityZombieVariant)par1EntityLivingBase, par2, par3);
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        this.func_82428_a((EntityZombieVariant)par1EntityLivingBase, par2);
    }

    @Override
    protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
        this.func_82430_a((EntityZombieVariant)par1EntityLivingBase, par2, par3, par4);
    }

    @Override
    public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityZombieVariant)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.func_110863_a((EntityZombieVariant)par1Entity);
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityZombieVariant)par1Entity, par2, par4, par6, par8, par9);
    }
}

