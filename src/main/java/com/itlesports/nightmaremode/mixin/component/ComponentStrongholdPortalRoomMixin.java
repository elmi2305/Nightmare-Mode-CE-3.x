package com.itlesports.nightmaremode.mixin.component;

import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComponentStrongholdPortalRoom.class)
public abstract class ComponentStrongholdPortalRoomMixin extends ComponentStronghold{
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ComponentStrongholdPortalRoom;placeBlockAtCurrentPosition(Lnet/minecraft/src/World;IIIIILnet/minecraft/src/StructureBoundingBox;)V"))
    private void manageBlackstoneBricks(ComponentStrongholdPortalRoom corridor, World world, int blockId, int metadata, int localX, int localY, int localZ, StructureBoundingBox boundingBox) {
        if (blockId == Block.stairsStoneBrick.blockID) {
            this.placeBlockAtCurrentPosition(world, 1134, metadata, localX, localY, localZ, boundingBox);
            // blackstone brick stairs
        } else{
            this.placeBlockAtCurrentPosition(world, blockId, metadata, localX, localY, localZ, boundingBox);
        }
    }
}
