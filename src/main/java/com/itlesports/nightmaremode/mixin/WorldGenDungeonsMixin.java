package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenDungeons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldGenDungeons.class)
public class WorldGenDungeonsMixin {
    @Inject(method = "filterChestContentsForDepth", at = @At("TAIL"))
    private void addKnowledgeBookLoot(World world, int x, int y, int z, CallbackInfo ci) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof IInventory inventory) {
            // Deep dungeons are deliberately the best source for these skill rewards.
            int chance = y <= 24 ? 2 : y <= 36 ? 3 : 5;
            KnowledgeBookLoot.addBookIfRolled(inventory, world.rand, NMFields.KNOWLEDGE_BOOKS_DUNGEON, chance);
        }
    }
}
