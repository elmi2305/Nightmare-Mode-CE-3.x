package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.item.items.ItemMechanicalWrench;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class PlayerControllerMPMixin {
    @Redirect(method = "onPlayerRightClick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Block;onBlockActivated(Lnet/minecraft/src/World;IIILnet/minecraft/src/EntityPlayer;IFFF)Z"))
    private boolean letMechanicalWrenchInspectBeforeOpeningGui(Block block, World world,
                                                                int x, int y, int z,
                                                                EntityPlayer player, int side,
                                                                float clickX, float clickY, float clickZ) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held != null && held.getItem() instanceof ItemMechanicalWrench
                && ItemMechanicalWrench.inspect(player, world, x, y, z)) {
            // The item-use path still sends the normal placement packet. The
            // server-side manager intercepts it and sends the stress reading.
            return false;
        }
        return block.onBlockActivated(world, x, y, z, player, side, clickX, clickY, clickZ);
    }

    @Inject(method = "onPlayerRightClick", at = @At("RETURN"))
    private void logSignPlacing(EntityPlayer p, World w, ItemStack stack, int x, int y, int z, int par7, Vec3 par8Vec3, CallbackInfoReturnable<Boolean> cir) {
        // again, does not work for some reason
//        System.out.println("client: " + w.isRemote);
//        if (NightmareMode.getInstance().isGriefLogging() && !w.isRemote) {
//            System.out.println("was grief logging");
//            if(stack == null) return;
//            System.out.println("stack was correct");
//
//            if(stack.itemID == Item.sign.itemID){
//                System.out.println("stack was id sign - wrote everything to log");
//
//                String text = "Player " + p.getEntityName() + " placed Sign at " + x + " " + y + " " + z;
//                NightmareMode.appendLogLine(text);
//            }
//        }
    }
}
