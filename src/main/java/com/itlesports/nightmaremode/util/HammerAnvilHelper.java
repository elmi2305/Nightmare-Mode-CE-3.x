package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.block.tileEntities.TileEntityHammerAnvil;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.crafting.recipe.types.HammerRecipe;
import com.itlesports.nightmaremode.item.NMItems;
import btw.item.BTWItems;
import btw.BTWMod;
import com.itlesports.nightmaremode.item.items.ItemAnvilUpgrade;
import com.itlesports.nightmaremode.nmgui.ContainerAutoAnvil;
import com.itlesports.nightmaremode.nmgui.ContainerPhaseAnvil;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class HammerAnvilHelper {
    public static int getHitsRequired(int anvilID, HammerRecipe recipe) {
        int base = recipe.getHitsRequired();
        ItemStack input = recipe.getInput();
        if (input == null || input.getItemDamage() != 0) return base;
        int itemID = input.itemID;
        int hits = base;
        if (NMBlocks.stoneAnvil != null && anvilID == NMBlocks.stoneAnvil.blockID) {
            if (itemID == NMItems.roughStoneBrick.itemID) hits = 2;
        } else if (NMBlocks.lithiumAnvil != null && anvilID == NMBlocks.lithiumAnvil.blockID) {
            if (itemID == NMItems.lithiumRaw.itemID || itemID == NMItems.lithiumHammered.itemID) hits = 1;
        } else if (NMBlocks.ironAnvil != null && anvilID == NMBlocks.ironAnvil.blockID) {
            if (itemID == NMItems.ironBloom.itemID) hits = 2;
            else if (itemID == NMItems.carburizedIronBloom.itemID
                    || itemID == NMItems.carbonIronIngot.itemID || itemID == NMItems.reinforcedIronIngot.itemID) hits = 1;
        } else if (NMBlocks.diamondAnvil != null && anvilID == NMBlocks.diamondAnvil.blockID) {
            if (itemID == NMItems.diamondBearingRock.itemID) hits = 2;
        } else if (NMBlocks.netherrackAnvil != null && anvilID == NMBlocks.netherrackAnvil.blockID) {
            if (itemID == NMItems.coresteelIngot.itemID) hits = 5;
            else if (itemID == NMItems.deadzoneAlloyIngot.itemID) hits = 12;
        } else if (anvilID == Block.anvil.blockID) {
            if (itemID == NMItems.deadzoneAlloyIngot.itemID) hits = 6;
            else if (itemID == NMItems.phaseSteelIngot.itemID) hits = 2;
        }
        return Math.max(1, Math.min(base, hits));
    }

    public static boolean isAnvil(int id) {
        return id == Block.anvil.blockID || NMBlocks.stoneAnvil != null && id == NMBlocks.stoneAnvil.blockID
                || NMBlocks.ironAnvil != null && id == NMBlocks.ironAnvil.blockID
                || NMBlocks.lithiumAnvil != null && id == NMBlocks.lithiumAnvil.blockID
                || NMBlocks.diamondAnvil != null && id == NMBlocks.diamondAnvil.blockID
                || NMBlocks.netherrackAnvil != null && id == NMBlocks.netherrackAnvil.blockID;
    }

    public static boolean isPristine(World world, int x, int y, int z) {
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        return !(tile instanceof TileEntityHammerAnvil anvil) || !anvil.isAutomatic() && anvil.isPristine();
    }

    public static void dropOnRemoval(World world, int x, int y, int z, int id) {
        if (world.isRemote) return;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        boolean pristine = !(tile instanceof TileEntityHammerAnvil anvil)
                || anvil.isPristine() && (!anvil.isAutomatic() || anvil.canRecoverPristineUpgrade());
        if (tile instanceof TileEntityHammerAnvil anvil && !anvil.claimRemoval()) return;
        if (tile instanceof TileEntityHammerAnvil anvil && anvil.isAutomatic()) {
            for (int slot = 0; slot < anvil.getSizeInventory(); ++slot) {
                ItemStack contents = anvil.getStackInSlotOnClosing(slot);
                if (contents != null) dropStack(world, x, y, z, contents);
            }
            for (int tier = 1; tier <= anvil.getUpgradeTier(); ++tier) {
                AutoAnvilUpgrade upgrade = AutoAnvilUpgrade.fromTier(tier);
                dropStack(world, x, y, z, new ItemStack(pristine ? upgrade.item() : upgrade.salvageMaterial(), pristine ? 1 : 2));
            }
        }
        ItemStack drop;
        if (pristine) {
            drop = new ItemStack(id, 1, 0);
        } else {
            Item material;
            int count;
            if (id == NMBlocks.stoneAnvil.blockID) material = BTWItems.stone;
            else if (id == NMBlocks.ironAnvil.blockID) material = BTWItems.ironNugget;
            else if (id == NMBlocks.lithiumAnvil.blockID) material = NMItems.lithiumRefined;
            else if (id == NMBlocks.netherrackAnvil.blockID) material = NMItems.netherrackChunk;
            else if (id == NMBlocks.diamondAnvil.blockID) material = BTWItems.diamondIngot;
            else material = BTWItems.soulforgedSteelIngot;
            if (id == NMBlocks.lithiumAnvil.blockID) count = 3 + world.rand.nextInt(3);
            else count = id == NMBlocks.diamondAnvil.blockID || id == Block.anvil.blockID
                    ? 5 + world.rand.nextInt(5) : 16 + world.rand.nextInt(9);
            drop = new ItemStack(material, count);
        }
        dropStack(world, x, y, z, drop);
    }

    private static void dropStack(World world, int x, int y, int z, ItemStack drop) {
        while (drop.stackSize > 0) {
            ItemStack stack = drop.splitStack(Math.min(drop.stackSize, drop.getMaxStackSize()));
            EntityItem entity = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, stack);
            entity.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entity);
        }
    }

    public static void collectPristineAnvil(World world, int x, int y, int z) {
        if (world.isRemote) return;
        TileEntity tile = world.getBlockTileEntity(x, y, z);
        if (!(tile instanceof TileEntityHammerAnvil)) {
            tile = new TileEntityHammerAnvil();
            world.setBlockTileEntity(x, y, z, tile);
        }
        ((TileEntityHammerAnvil)tile).claimRemoval();
    }

    public static boolean tryHammerHeldItem(World world, int x, int y, int z, EntityPlayer player, TileEntityHammerAnvil anvil) {
        if (anvil == null || world.isRemote) return false;
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemAnvilUpgrade item) {
            if (anvil.installUpgrade(item.upgrade)) {
                if (!player.capabilities.isCreativeMode && --held.stackSize <= 0) {
                    player.inventory.mainInventory[player.inventory.currentItem] = null;
                }
                player.inventory.onInventoryChanged();
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "random.anvil_use", 0.5F, 1.1F);
            }
            return true;
        }
        if (anvil.isAutomatic() && player instanceof EntityPlayerMP serverPlayer) {
            if (anvil.getUpgrade() == AutoAnvilUpgrade.PHASE) {
                BTWMod.serverOpenCustomInterface(serverPlayer, new ContainerPhaseAnvil(player.inventory, anvil), ContainerPhaseAnvil.ID);
            } else {
                BTWMod.serverOpenCustomInterface(serverPlayer, new ContainerAutoAnvil(player.inventory, anvil), ContainerAutoAnvil.ID);
            }
            return true;
        }
        return anvil.tryStartHammerOperation(player);
    }
}
