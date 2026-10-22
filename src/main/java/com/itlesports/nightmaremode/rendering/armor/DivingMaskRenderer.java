package com.itlesports.nightmaremode.rendering.armor;

import com.itlesports.nightmaremode.util.DivingMaskAttachment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public final class DivingMaskRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("nightmare:textures/armor/ifhyDivingMask_layer_1.png");
    // layer-one helmets inflate the head by 1 and their outer headwear by 1.5.
    private static final ModelBiped MODEL = new ModelBiped(1.55F);

    private DivingMaskRenderer() {}

    public static void render(EntityLivingBase entity, ModelBiped bipedModel, float scale) {
        ItemStack helmet = entity.getCurrentItemOrArmor(4);
        if (!DivingMaskAttachment.hasAttachment(helmet) || DivingMaskAttachment.getMask(helmet) == null
                || bipedModel.bipedHead.isHidden || !bipedModel.bipedHead.showModel) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try {
            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (bipedModel.isChild) {
                GL11.glScalef(0.75F, 0.75F, 0.75F);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            bipedModel.bipedHead.postRender(scale);
            MODEL.bipedHead.render(scale);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
