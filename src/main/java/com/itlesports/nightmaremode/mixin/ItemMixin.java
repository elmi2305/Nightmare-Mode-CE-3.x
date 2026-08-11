package com.itlesports.nightmaremode.mixin;

import api.item.items.FireStarterItemPrimitive;
import api.item.items.SeedFoodItem;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import com.itlesports.nightmaremode.item.items.template.NMFoodItem;
import com.itlesports.nightmaremode.mixin.interfaces.ItemInvoker;
import com.itlesports.nightmaremode.util.NMFoodSpoilage;
import com.itlesports.nightmaremode.util.interfaces.INetherItem;
import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkAttributes;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.skill.NMSkillNodes;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {
    @Shadow public static Item horseArmorIron;
    @Shadow public static Item horseArmorGold;
    @Shadow public static Item horseArmorDiamond;
    @Shadow public static ItemArmor bootsDiamond;
    @Shadow public static ItemArmor helmetDiamond;
    @Shadow public static ItemArmor plateDiamond;
    @Shadow public static ItemArmor legsDiamond;

    @Shadow public static Item netherStar;
    @Shadow public static Item potato;
    @Shadow public static Item bakedPotato;
    @Shadow public static Item netherStalkSeeds;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void replaceItems(CallbackInfo ci){
        horseArmorIron    = new ItemAdvancedHorseArmor(161, ItemAdvancedHorseArmor.ArmorTier.IRON).setUnlocalizedName("horsearmormetal").setTextureName("iron_horse_armor");
        horseArmorGold    = new ItemAdvancedHorseArmor(162, ItemAdvancedHorseArmor.ArmorTier.GOLD).setUnlocalizedName("horsearmorgold").setTextureName("gold_horse_armor");
        horseArmorDiamond = new ItemAdvancedHorseArmor(163, ItemAdvancedHorseArmor.ArmorTier.DIAMOND).setUnlocalizedName("horsearmordiamond").setTextureName("diamond_horse_armor");


        bootsDiamond = (ItemArmor) bootsDiamond.setTextureName("nightmare:nmDiamondBoots");
        helmetDiamond = (ItemArmor) helmetDiamond.setTextureName("nightmare:nmDiamondHelmet");
        plateDiamond  = (ItemArmor) plateDiamond.setTextureName("nightmare:nmDiamondChestplate");
        legsDiamond = (ItemArmor) legsDiamond.setTextureName("nightmare:nmDiamondLeggings");

        potato = new SeedFoodItem(136, 1, 0.0f, Block.potato.blockID).setAsBasicPigFood().setUnlocalizedName("potato").setTextureName("potato");
        bakedPotato = new ItemFood(137, 1, 0.0f, false).setAsBasicPigFood().setUnlocalizedName("potatoBaked").setTextureName("potato_baked");
        netherStalkSeeds = new NMFoodItem(116, 1, 0.0F, false, "netherStalkSeeds", false)
                .setPotionEffect("+4")
                .setMaxStackSize(16)
                .setTextureName("nether_wart");

        netherStar = ((ItemInvoker)netherStar).invokeSetMaxDamage(4);
    }

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void updateFoodSpoilage(ItemStack stack, World world, EntityPlayer player, int inventorySlot, boolean isHeldItem, CallbackInfo ci) {
        NMFoodSpoilage.updateFoodSpoilage(stack, world, player, inventorySlot);
    }

    @Inject(method = "addInformation", at = @At("TAIL"))
    private void addNetherItemTooltip(ItemStack stack, EntityPlayer player, List tooltip,
                                      boolean advanced, CallbackInfo ci) {
        if ((Object) this instanceof INetherItem) {
            tooltip.add(EnumChatFormatting.DARK_AQUA + "Fireproof");
        }
    }

    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void collectSoilSample(ItemStack stack, EntityPlayer player, World world,
                                   int x, int y, int z, int side, float hitX, float hitY, float hitZ,
                                   CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this != Item.glassBottle || !SkillHandler.isUnlocked(player, NMSkillNodes.CRAFT_BOOK_64)) return;
        int blockId = world.getBlockId(x, y, z);
        if (blockId != Block.grass.blockID && blockId != Block.dirt.blockID && blockId != Block.gravel.blockID) return;
        if (!world.isRemote) {
            ChunkAttributes attributes = ChunkAttributeManager.get(world, x, z);
            ItemStack sample = new ItemStack(NMItems.soilSample);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setLong("Roll", attributes.getRollSeed());
            for (ChunkAttribute attribute : ChunkAttribute.values()) {
                tag.setFloat(attribute.name(), attributes.get(attribute));
            }
            sample.setTagCompound(tag);
            if (!player.capabilities.isCreativeMode && --stack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            player.dropPlayerItem(sample);
        }
        cir.setReturnValue(true);
    }
}
