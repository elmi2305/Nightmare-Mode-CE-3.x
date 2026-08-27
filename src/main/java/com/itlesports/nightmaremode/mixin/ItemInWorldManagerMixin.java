package com.itlesports.nightmaremode.mixin;

import api.achievement.AchievementEventDispatcher;
import api.block.blocks.OreBlockStaged;
import api.item.items.PickaxeItem;
import btw.community.nightmaremode.NightmareMode;
import btw.item.items.ChiselItem;
import btw.item.BTWItems;
import api.item.util.ItemUtils;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.elements.LogSettings;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.achievements.NMAchievementEvents;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockOreNode;
import com.itlesports.nightmaremode.block.blocks.CrystalPocketBlock;
import com.itlesports.nightmaremode.item.items.ItemMechanicalWrench;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.itlesports.nightmaremode.util.NMFields.POSTWITHER;

@Mixin(ItemInWorldManager.class)
public class ItemInWorldManagerMixin {
    @Shadow
    public World theWorld;

    @Shadow
    public EntityPlayerMP thisPlayerMP;

    @Inject(method = "activateBlockOrUseItem", at = @At("HEAD"), cancellable = true)
    private void inspectMechanicalPowerBeforeBlockGui(EntityPlayer player, World world, ItemStack stack,
                                                       int x, int y, int z, int side,
                                                       float clickX, float clickY, float clickZ,
                                                       CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.getItem() instanceof ItemMechanicalWrench
                && ItemMechanicalWrench.inspect(player, world, x, y, z)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "survivalTryHarvestBlock", at = @At("HEAD"), cancellable = true)
    private void minePersistentOreNode(int x, int y, int z, int fromSide, CallbackInfoReturnable<Boolean> cir) {
        if (this.theWorld.getBlockId(x, y, z) == Block.whiteStone.blockID
                && this.theWorld.getBlockMetadata(x, y, z) == 1) {
            ItemStack tool = this.thisPlayerMP.getCurrentEquippedItem();
            if (tool == null || tool.getItem() != NMItems.enderPickaxe) {
                cir.setReturnValue(false);
                return;
            }
        }
        Block block = Block.blocksList[this.theWorld.getBlockId(x, y, z)];
        if (this.isSkillLockedOre(block, x, y, z)) {
            ItemStack held = this.thisPlayerMP.getCurrentEquippedItem();
            block.convertBlock(held, this.theWorld, x, y, z, fromSide);
            if (held != null) {
                held.onBlockDestroyed(this.theWorld, block.blockID, x, y, z, this.thisPlayerMP);
                if (held.stackSize <= 0) {
                    this.thisPlayerMP.destroyCurrentEquippedItem();
                }
            }
            cir.setReturnValue(true);
            return;
        }
        if (!(block instanceof BlockOreNode oreNode)) {
            if (!(block instanceof CrystalPocketBlock crystalPocket)) {
                return;
            }

            ItemStack held = this.thisPlayerMP.getCurrentEquippedItem();
            int metadata = this.theWorld.getBlockMetadata(x, y, z);
            if (!crystalPocket.isValidMiningTool(held, this.theWorld, x, y, z)
                    || !crystalPocket.minePocket(this.theWorld, this.thisPlayerMP, x, y, z, fromSide)) {
                cir.setReturnValue(false);
                return;
            }

            this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, x, y, z,
                    block.blockID + (metadata << 12));
            held.onBlockDestroyed(this.theWorld, block.blockID, x, y, z, this.thisPlayerMP);
            if (held.stackSize <= 0) {
                this.thisPlayerMP.destroyCurrentEquippedItem();
            }
            cir.setReturnValue(true);
            return;
        }

        ItemStack held = this.thisPlayerMP.getCurrentEquippedItem();
        if (!oreNode.isValidMiningTool(held, this.theWorld, x, y, z)) {
            cir.setReturnValue(false);
            return;
        }

        this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, x, y, z,
                block.blockID + (this.theWorld.getBlockMetadata(x, y, z) << 12));
        oreNode.mineNode(this.theWorld, this.thisPlayerMP, x, y, z);
        held.onBlockDestroyed(this.theWorld, block.blockID, x, y, z, this.thisPlayerMP);
        if (held.stackSize <= 0) {
            this.thisPlayerMP.destroyCurrentEquippedItem();
        }
        cir.setReturnValue(true);
    }

    @Unique
    private boolean isSkillLockedOre(Block block, int x, int y, int z) {
        if (!(block instanceof OreBlockStaged ore)) {
            return false;
        }

        if (ore.getStrata(this.theWorld, x, y, z) == 2
                && !SkillHandler.getPlayerData(this.thisPlayerMP).canMineStrataThreeOre) {
            return true;
        }

        return block.blockID == Block.oreDiamond.blockID
                && !SkillHandler.canHarvestDiamondOre(this.thisPlayerMP);
    }

    @Inject(method = "survivalTryHarvestBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;playAuxSFXAtEntity(Lnet/minecraft/src/EntityPlayer;IIIII)V"))
    private void sendNightmareAchievementData(int i, int j, int k, int iFromSide, CallbackInfoReturnable<Boolean> cir) {
        int id = this.theWorld.getBlockId(i, j, k);
        if(this.shouldActivate(id, i, j, k)){
            AchievementEventDispatcher.triggerEvent(NMAchievementEvents.LustEvent.class, this.thisPlayerMP);
        }
        if(NightmareMode.getInstance().isGriefLogging()){
            LogSettings ls = NightmareMode.getInstance().getLogSettings();

            TileEntity te = this.theWorld.getBlockTileEntity(i,j,k);
            if(te == null) return;

            String text = this.thisPlayerMP.username + " broke [" + Block.blocksList[id].getLocalizedName() + "] at " + i + " " + j + " " + k;

            if(ls.logAllTileEntities){
                NightmareMode.appendLogLine(text);
                return;
            }
            if(ls.logContainers && Block.blocksList[id] instanceof BlockContainer){
                NightmareMode.appendLogLine(text);
                return;
            }
            if(ls.logChests && te instanceof TileEntityChest){
                NightmareMode.appendLogLine(text);
                return;
            }
        }
    }

    @Unique
    private boolean shouldActivate(int id, int i, int j, int k) {
        ItemStack held = this.thisPlayerMP.getHeldItem();
        boolean isDiamond = id == Block.oreDiamond.blockID;
        boolean isSteel   = id == NMBlocks.steelOre.blockID;

        if (!isDiamond && !isSteel) {
            return false;
        }

        if (isSteel && NMUtils.getWorldProgress() < POSTWITHER) {
            return true;
        }

        Item item = held != null ? held.getItem() : null;
        if (item instanceof PickaxeItem p) {
            float strength = p.getStrVsBlock(held, this.theWorld, Block.oreDiamond, i, j, k);
            return strength < 3.9f;
        }

        return !(item instanceof ChiselItem);
    }

    @Redirect(method = "survivalTryHarvestBlock", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Block;convertBlock(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/World;IIII)Z"))
    private boolean applySkillHempSeedChance(Block block, ItemStack stack, World world, int x, int y, int z, int side) {
        boolean converted = block.convertBlock(stack, world, x, y, z, side);
        if (converted && block.blockID == Block.grass.blockID && !world.isRemote
                && world.rand.nextFloat() < SkillHandler.getPlayerData(this.thisPlayerMP).hempSeedChanceBonus) {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z, new ItemStack(BTWItems.hempSeeds), side);
        }
        return converted;
    }

}
