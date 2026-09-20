package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemLimitedRecallPotion;
import com.itlesports.nightmaremode.util.interfaces.PhaseTransitEntity;
import net.minecraft.src.*;

public final class NetherRecall {
    private static final ThreadLocal<NBTTagCompound> TRANSFER_TARGET = new ThreadLocal<>();
    private NetherRecall() {}

    public static boolean isRecall(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemLimitedRecallPotion;
    }

    public static ItemStack create(EntityPlayerMP player) {
        ItemStack stack = new ItemStack(NMItems.limitedRecallPotion);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Owner", player.username);
        tag.setLong("Expires", System.currentTimeMillis() + 20000L);
        tag.setInteger("Dimension", player.dimension);
        tag.setDouble("X", player.posX);
        tag.setDouble("Y", player.posY);
        tag.setDouble("Z", player.posZ);
        tag.setFloat("Yaw", player.rotationYaw);
        tag.setFloat("Pitch", player.rotationPitch);
        stack.setTagCompound(tag);
        return stack;
    }

    public static boolean isValid(ItemStack stack, EntityPlayer player) {
        if (!isRecall(stack) || stack.stackSize <= 0 || !stack.hasTagCompound() || player == null) return false;
        NBTTagCompound tag = stack.getTagCompound();
        long remaining = tag.getLong("Expires") - System.currentTimeMillis();
        return remaining > 0 && remaining <= 20000L && tag.getString("Owner").equals(player.username)
                && tag.getInteger("Dimension") == 0;
    }

    public static void age(ItemStack stack, EntityPlayer player) {
        if (!isRecall(stack)) return;
        if (!isValid(stack, player)) {
            stack.stackSize = 0;
            return;
        }
        long remaining = stack.getTagCompound().getLong("Expires") - System.currentTimeMillis();
        stack.setItemDamage((int)Math.max(0, 20 - (remaining + 999) / 1000));
    }

    public static NBTTagCompound getTransferTarget() { return TRANSFER_TARGET.get(); }

    public static boolean recall(EntityPlayerMP player, ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        double x = tag.getDouble("X"), y = tag.getDouble("Y"), z = tag.getDouble("Z");
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)
                || Math.abs(x) > 29999900 || Math.abs(z) > 29999900 || y < 1 || y > 254) return false;
        WorldServer destination = player.mcServer.worldServerForDimension(0);
        if (destination == null || player.dimension != -1) return false;
        int chunkX = MathHelper.floor_double(x) >> 4, chunkZ = MathHelper.floor_double(z) >> 4;
        // load only the arrival area; never generate a portal, edit terrain, or move the player in the source world.
        for (int cx = chunkX - 1; cx <= chunkX + 1; cx++) for (int cz = chunkZ - 1; cz <= chunkZ + 1; cz++) {
            destination.theChunkProviderServer.loadChunk(cx, cz);
        }
        AxisAlignedBB arrival = AxisAlignedBB.getAABBPool().getAABB(x - 0.3, y, z - 0.3, x + 0.3, y + 1.8, z + 0.3);
        if (!destination.getCollidingBoundingBoxes(player, arrival).isEmpty() || destination.isAnyLiquid(arrival)) {
            player.sendChatToPlayer(ChatMessageComponent.createFromText("The return portal is obstructed."));
            return false;
        }
        NBTTagCompound previous = TRANSFER_TARGET.get();
        TRANSFER_TARGET.set(tag);
        try {
            player.mountEntity(null);
            player.mcServer.getConfigurationManager().transferPlayerToDimension(player, 0);
            player.fallDistance = 0;
            player.extinguish();
            player.timeUntilPortal = player.getPortalCooldown();
            ((PhaseTransitEntity)player).nm$setMustLeavePhasePortal(true);
            return player.dimension == 0;
        } finally {
            if (previous == null) TRANSFER_TARGET.remove();
            else TRANSFER_TARGET.set(previous);
        }
    }

    public static void tick(EntityPlayerMP player) {
        boolean changed = false;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!isRecall(stack)) continue;
            age(stack, player);
            if (stack.stackSize <= 0) player.inventory.setInventorySlotContents(i, null);
            changed = true;
        }
        ItemStack cursor = player.inventory.getItemStack();
        if (isRecall(cursor)) {
            age(cursor, player);
            if (cursor.stackSize <= 0) player.inventory.setItemStack(null);
            player.updateHeldItem();
            changed = true;
        }
        for (Object obj : player.openContainer.inventorySlots) {
            Slot slot = (Slot)obj;
            if (!(slot.inventory instanceof InventoryPlayer) && isRecall(slot.getStack())) {
                slot.putStack(null);
                changed = true;
            }
        }
        if (changed) {
            player.inventory.onInventoryChanged();
            player.openContainer.detectAndSendChanges();
        }
    }
}
