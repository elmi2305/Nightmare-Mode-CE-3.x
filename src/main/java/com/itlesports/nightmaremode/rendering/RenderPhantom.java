package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.entity.EntityPhantomZombie;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

public class RenderPhantom extends RenderBiped{


    public RenderPhantom() {
        super(new ModelZombie(), 0.5f, 1.0f);
    }
    private static final ResourceLocation zombieTextures = new ResourceLocation("nightmare:textures/entity/nmPhantom.png");
    private ModelBiped field_82434_o = this.modelBipedMain;
    private ModelZombieVillager zombieVillagerModel = new ModelZombieVillager();
    protected ModelBiped field_82437_k;
    protected ModelBiped field_82435_l;
    protected ModelBiped field_82436_m;
    protected ModelBiped field_82433_n;
    private int field_82431_q = 1;

    @Override
    protected void func_82421_b() {
        this.field_82423_g = new ModelZombie(1.0f, true);
        this.field_82425_h = new ModelZombie(0.5f, true);
        this.field_82437_k = this.field_82423_g;
        this.field_82435_l = this.field_82425_h;
        this.field_82436_m = new ModelZombieVillager(1.0f, 0.0f, true);
        this.field_82433_n = new ModelZombieVillager(0.5f, 0.0f, true);
    }

    protected int func_82429_a(EntityPhantomZombie par1EntityZombie, int par2, float par3) {
        this.func_82427_a(par1EntityZombie);
        return super.func_130006_a(par1EntityZombie, par2, par3);
    }

    public void func_82426_a(EntityPhantomZombie par1EntityZombie, double par2, double par4, double par6, float par8, float par9) {
        this.func_82427_a(par1EntityZombie);
        super.doRenderLiving(par1EntityZombie, par2, par4, par6, par8, par9);
    }

    protected ResourceLocation func_110863_a(EntityZombie par1EntityZombie) {
        return zombieTextures;
    }

    protected void func_82428_a(EntityPhantomZombie par1EntityZombie, float par2) {
        this.func_82427_a(par1EntityZombie);
        super.func_130005_c(par1EntityZombie, par2);
    }

    private void func_82427_a(EntityPhantomZombie par1EntityZombie) {
        this.mainModel = this.field_82434_o;
        this.field_82423_g = this.field_82437_k;
        this.field_82425_h = this.field_82435_l;
        this.modelBipedMain = (ModelBiped)this.mainModel;
    }

    protected void func_82430_a(EntityPhantomZombie par1EntityZombie, float par2, float par3, float par4) {
        if (par1EntityZombie.isConverting()) {
            par3 += (float)(Math.cos((double)par1EntityZombie.ticksExisted * 3.25) * Math.PI * 0.25);
        }
        super.rotateCorpse(par1EntityZombie, par2, par3, par4);
    }

    @Override
    protected void func_130005_c(EntityLiving par1EntityLiving, float par2) {
        this.func_82428_a((EntityPhantomZombie)par1EntityLiving, par2);
    }

    @Override
    protected ResourceLocation func_110856_a(EntityLiving par1EntityLiving) {
        return this.func_110863_a((EntityPhantomZombie)par1EntityLiving);
    }

    @Override
    public void doRenderLiving(EntityLiving par1EntityLiving, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityPhantomZombie)par1EntityLiving, par2, par4, par6, par8, par9);
    }

    @Override
    protected int func_130006_a(EntityLiving par1EntityLiving, int par2, float par3) {
        return this.func_82429_a((EntityPhantomZombie)par1EntityLiving, par2, par3);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3) {
        return this.func_82429_a((EntityPhantomZombie)par1EntityLivingBase, par2, par3);
    }

    @Override
    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2) {
        this.func_82428_a((EntityPhantomZombie)par1EntityLivingBase, par2);
    }

    @Override
    protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4) {
        this.func_82430_a((EntityPhantomZombie)par1EntityLivingBase, par2, par3, par4);
    }

    @Override
    public void renderPlayer(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityPhantomZombie)par1EntityLivingBase, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity) {
        return this.func_110863_a((EntityPhantomZombie)par1Entity);
    }


    @Override
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9) {
        this.func_82426_a((EntityPhantomZombie) par1Entity, par2, par4, par6, par8, par9);
    }
}
