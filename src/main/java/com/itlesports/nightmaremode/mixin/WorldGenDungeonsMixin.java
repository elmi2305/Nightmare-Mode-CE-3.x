package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.IInventory;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.WorldGenDungeons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenDungeons.class)
public class WorldGenDungeonsMixin {
    @Unique private boolean journalPlaced;
    @Unique private boolean witherJournalPlaced;
    @Unique private boolean bloodWitherJournalPlaced;

    @Inject(method = "generate", at = @At("HEAD"))
    private void resetJournalPlacement(World world, java.util.Random random, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        this.journalPlaced = false;
        this.witherJournalPlaced = false;
        this.bloodWitherJournalPlaced = false;
    }

    @Inject(method = "filterChestContentsForDepth", at = @At("TAIL"))
    private void addKnowledgeBookLoot(World world, int x, int y, int z, CallbackInfo ci) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof IInventory inventory) {
            if (y <= 24 && !this.journalPlaced) {
                this.journalPlaced = com.itlesports.nightmaremode.util.JourneyJournals.addToInventory(inventory, 2);
            }
            if (y <= 24 && !this.witherJournalPlaced && world.rand.nextInt(4) == 0) {
                this.witherJournalPlaced = com.itlesports.nightmaremode.util.JourneyJournals.addToInventory(inventory, 3);
            }
            if (y <= 24 && !this.bloodWitherJournalPlaced && world.rand.nextInt(6) == 0) {
                this.bloodWitherJournalPlaced = com.itlesports.nightmaremode.util.JourneyJournals.addToInventory(inventory, 4);
            }
            // Deep dungeons are deliberately the best source for these skill rewards.
            int chance = y <= 24 ? 2 : y <= 36 ? 3 : 5;
            KnowledgeBookLoot.addBookIfRolled(inventory, world.rand, NMFields.KNOWLEDGE_BOOKS_DUNGEON, chance);
        }
    }
}
