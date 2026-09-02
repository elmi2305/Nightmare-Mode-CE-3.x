package com.itlesports.nightmaremode.mixin;

import api.achievement.AchievementEventDispatcher;
import btw.achievement.BTWAchievementEvents;
import btw.entity.LocatorPileEntity;
import btw.item.BTWItems;
import btw.item.items.LocatorPileItem;
import com.itlesports.nightmaremode.structure.MapGenNetherDesertTemple;
import com.itlesports.nightmaremode.util.interfaces.LocatorPileEntityExt;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocatorPileItem.class)
public class LocatorPileItemMixin {
    private static final int DIAMOND_PILE_COLOR = 0x66FFFF;
    private static final double TEMPLE_CLOSE_RANGE = 512.0D;

    @Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
    private void ifhy$locateNetherTemple(ItemStack stack, World world, EntityPlayer player,
                                         CallbackInfoReturnable<ItemStack> cir) {
        if (stack.getItem() != BTWItems.diamondPile) {
            return;
        }
        if (world.provider.dimensionId != -1) {
            // Diamond dust is exclusively a Nether temple locator in IFHY.
            cir.setReturnValue(stack);
            return;
        }
        if (!world.isRemote) {
            ChunkPosition target = MapGenNetherDesertTemple.findNearestTemple(world,
                    (int) player.posX, (int) player.posZ);
            LocatorPileEntity particle = new LocatorPileEntity(world, player.posX,
                    player.posY + 1.7D - player.yOffset, player.posZ, DIAMOND_PILE_COLOR);
            if (target != null) {
                // The close-target path uses a distinct visual trail and whole-step movement.
                ((LocatorPileEntityExt) particle).nightmareMode$setDenominator(TEMPLE_CLOSE_RANGE);
                particle.moveTowards(target.x, target.z);
                AchievementEventDispatcher.triggerEvent(BTWAchievementEvents.UseLocatorPileEvent.class, player, stack);
                world.playAuxSFX(2286, (int) Math.round(player.posX),
                        (int) Math.round(player.posY + 1.7D - player.yOffset), (int) Math.round(player.posZ), 0);
            } else {
                particle.moveTowards(player.posX, player.posZ);
            }
            world.spawnEntityInWorld(particle);
            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
        }
        cir.setReturnValue(stack);
    }
}
