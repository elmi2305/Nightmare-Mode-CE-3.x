package com.itlesports.nightmaremode.mixin.blocks;

import api.util.color.Color;
import btw.block.blocks.FarmlandBlockBase;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.item.items.ItemChunkFertilizer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlockBase.class)
public class FarmlandBlockBaseMixin {
    @Inject(method = "onEntityCollidedWithBlock", at = @At("HEAD"), cancellable = true)
    private void applyDroppedFertilizer(
            World world,
            int x,
            int y,
            int z,
            Entity entity,
            CallbackInfo ci
    ) {
        if (world.isRemote || !(entity instanceof EntityItem entityItem) || !entity.isEntityAlive()) {
            return;
        }

        ItemStack stack = entityItem.getEntityItem();
        ChunkAttribute attribute = null;
        if (stack.itemID == Item.dyePowder.itemID && stack.getItemDamage() == Color.WHITE.colorID) {
            attribute = ChunkAttribute.NITROGEN;
        } else if (stack.getItem() instanceof ItemChunkFertilizer fertilizer) {
            attribute = fertilizer.getAttribute();
        }
        if (attribute == null || !ChunkAttributeManager.applyFertilizer(world, x, y, z, attribute)) {
            return;
        }

        --stack.stackSize;
        if (stack.stackSize <= 0) {
            entityItem.setDead();
        }
        world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                "random.pop",
                0.25F,
                ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F
        );
        ci.cancel();
    }
}
