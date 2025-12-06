package com.itlesports.nightmaremode.mixin;

import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.community.nightmaremode.NightmareMode;
import btw.inventory.util.InventoryUtils;
import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.NightmareVillager;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(EntityVillager.class)
public abstract class EntityVillagerMixin extends EntityAgeable implements IMerchant, INpc {

    @Shadow protected MerchantRecipeList buyingList;
    @Shadow public static Map<Integer, Class> professionMap;

    @Shadow public abstract int getCurrentTradeLevel();

    @Shadow public abstract int getProfession();

    @Shadow public abstract int getCurrentMaxNumTrades();

    @Shadow public abstract int getCurrentTradeMaxXP();

    @Shadow public abstract int getCurrentTradeXP();

    @Shadow public static int[] xpPerLevel;

    public EntityVillagerMixin(World par1World) {
        super(par1World);
    }

    @Override
    public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntity) {
        int profession = this.getProfession();
        int level = this.getCurrentTradeLevel();

        int meta = NMUtils.VillagerMetaCodec.packMeta(profession, level);

        ItemStack stack = new ItemStack(NMBlocks.villagerBlock, 1, meta);

        this.setDead();
        InventoryUtils.addSingleItemToInventory(tileEntity, stack.itemID, stack.getItemDamage());
//        this.worldObj.playAuxSFX(2239, (int)this.posX, (int)this.posY, (int)this.posZ, 0);

        return true;
    }



    @Unique private static long lastCheckedTime = -1;
    @Unique private static boolean shouldResetTrades = false;

    @Inject(method = "updateAITick", at = @At("HEAD"))
    private void resetVillagerTrades(CallbackInfo ci) {
        int switchingConstant = 48000;
        if(NMUtils.getWorldProgress() > 1){
            switchingConstant = 24000;
        }
        if(NightmareMode.fastVillagers){
            switchingConstant = 6000;
        }

        long time = this.worldObj.getTotalWorldTime();

        if (time != lastCheckedTime) {
            shouldResetTrades = (time % switchingConstant == 0);
            lastCheckedTime = time;
        }

        if (shouldResetTrades) {
            this.buyingList = null;
        }

        if(this.ticksExisted % 20 == 0 && NMUtils.getIsBloodMoon()){
            this.heal(20f);
        }
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
