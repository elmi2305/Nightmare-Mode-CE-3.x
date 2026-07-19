package com.itlesports.nightmaremode.mixin.entity;

import api.inventory.InventoryUtils;
import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

import static com.itlesports.nightmaremode.util.NMFields.HARDMODE;


@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc {

    @Shadow public MerchantRecipeList buyingList;
    @Shadow public static Map<Integer, Class> professionMap;

    @Shadow public abstract int getCurrentTradeLevel();
    @Shadow public abstract int getProfession();
    @Shadow public abstract int getCurrentTradeMaxXP();
    @Shadow public abstract int getCurrentTradeXP();
    @Shadow public abstract EntityPlayer getCustomer();
    @Shadow public abstract void setProfession(int profession);

    @Unique private int nightmareMode$levelBeforeTrade;
    @Unique private EntityPlayer nightmareMode$tradingPlayer;


    @Shadow public abstract void setInLove(int iInLove);
    @Shadow public abstract int getInLove();

    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Inject(method = "useRecipe", at = @At("HEAD"))
    private void rememberTradeState(MerchantRecipe recipe, CallbackInfo ci) {
        this.nightmareMode$levelBeforeTrade = this.getCurrentTradeLevel();
        this.nightmareMode$tradingPlayer = this.getCustomer();
    }

    @Inject(method = "useRecipe", at = @At("TAIL"))
    private void applySkillTradeProgress(MerchantRecipe recipe, CallbackInfo ci) {
        EntityPlayer player = this.nightmareMode$tradingPlayer;
        SkillHandler.incrementTradesCompleted(player);
        if (player != null && this.getCurrentTradeLevel() > this.nightmareMode$levelBeforeTrade
                && this.rand.nextFloat() < SkillHandler.getPlayerData(player).villagerProfessionChangeChance) {
            int oldProfession = this.getProfession();
            int newProfession = this.rand.nextInt(4);
            if (newProfession >= oldProfession && oldProfession < 5) {
                newProfession++;
            }
            this.setProfession(newProfession);
            this.buyingList = null;
        }
        this.nightmareMode$tradingPlayer = null;
    }

    @Override
    public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
        int profession = this.getProfession();
        int level = this.getCurrentTradeLevel();
        if(this.getHealth() < 10 || this.isDead) return false; // if the villager is about to die, it cannot be vacuumed up. prevents exploits with grabbing them while they're dying / about to die

        int itemMeta = NMUtils.VillagerMetaCodec.packItemMeta(profession, level);

        ItemStack stack = new ItemStack(NMItems.villagerOrb, 1, itemMeta);

        this.setDead();
        InventoryUtils.addSingleItemToInventory(tileEntity, stack.itemID, stack.getItemDamage());

        return true;
    }


    @Inject(method = "updateAITick", at = @At("HEAD"))
    private void resetVillagerTrades(CallbackInfo ci) {
        if(this.ticksExisted % 20 == 0 && NMUtils.getIsBloodMoon()){
            this.heal(20f);
        }
    }

    @Inject(method = "isTemptingItem", at = @At("HEAD"),cancellable = true)
    private void addTemptingItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.itemID == NMItems.refinedDiamondIngot.itemID){
            cir.setReturnValue(true);
        }
    }
    @Redirect(method = "interact", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityVillager;customInteract(Lnet/minecraft/src/EntityPlayer;)Z"))
    private boolean breedWithRefinedDiamond(EntityVillager instance, EntityPlayer player){
        ItemStack heldStack = player.inventory.getCurrentItem();
        if (!(heldStack == null || heldStack.getItem().itemID != Item.diamond.itemID && heldStack.getItem().itemID != BTWItems.diamondIngot.itemID || this.getGrowingAge() != 0 || this.getInLove() != 0 || this.isPossessed())) {
            if (!player.capabilities.isCreativeMode) {
                --heldStack.stackSize;
                if (heldStack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
            this.worldObj.playSoundAtEntity(this, "mob.villager.hurt", 1.0f, this.getSoundPitch() * 2.0f);
            this.setInLove(1);
            this.entityToAttack = null;
            return true;
        }
        return false;
    }
    @Inject(method = "<clinit>", at = @At("TAIL"),remap = false)
    private static void addNightmareVillagerProfession(CallbackInfo ci){
        professionMap.put(5, NightmareVillager.class);
    }

    @Override
    public boolean isSecondaryTargetForSquid() {
        return false;
    }
}
