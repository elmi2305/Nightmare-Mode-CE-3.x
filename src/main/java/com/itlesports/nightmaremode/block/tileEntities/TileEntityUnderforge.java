package com.itlesports.nightmaremode.block.tileEntities;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipe;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipeManager;
import net.minecraft.src.*;

public class TileEntityUnderforge extends TileEntity implements IInventory {
    public static final int SLOT_BASE = 0;
    public static final int SLOT_METAL = 1;
    public static final int SLOT_FLUX = 2;
    public static final int SLOT_FUEL = 3;
    public static final int SLOT_OUTPUT = 4;
    private ItemStack[] contents = new ItemStack[5];
    private int progress;
    private boolean recipeWasActive;

    @Override
    public void updateEntity() {
        if (worldObj == null || worldObj.isRemote) return;
        UnderforgeRecipe recipe = UnderforgeRecipeManager.find(contents);
        if (recipe == null || !canAccept(recipe.createOutput(contents[SLOT_BASE]))) {
            if (NightmareMode.devMode && recipeWasActive) {
                System.out.println("[Underworld/Underforge] recipe interrupted at " + xCoord + "," + yCoord + "," + zCoord);
            }
            recipeWasActive = false;
            progress = 0;
            return;
        }
        if (NightmareMode.devMode && !recipeWasActive) {
            System.out.println("[Underworld/Underforge] started " + recipe.getOutputTemplate().getDisplayName()
                    + " at " + xCoord + "," + yCoord + "," + zCoord);
        }
        recipeWasActive = true;
        if (++progress >= 100) {
            ItemStack result = recipe.createOutput(contents[SLOT_BASE]);
            recipe.consume(contents);
            if (contents[SLOT_OUTPUT] == null) contents[SLOT_OUTPUT] = result;
            else contents[SLOT_OUTPUT].stackSize += result.stackSize;
            progress = 0;
            recipeWasActive = false;
            if (NightmareMode.devMode) {
                System.out.println("[Underworld/Underforge] completed " + result.getDisplayName()
                        + " at " + xCoord + "," + yCoord + "," + zCoord);
            }
            onInventoryChanged();
        }
    }

    private boolean canAccept(ItemStack output) {
        ItemStack current = contents[SLOT_OUTPUT];
        return current == null || current.isItemEqual(output) && ItemStack.areItemStackTagsEqual(current, output)
                && current.stackSize + output.stackSize <= Math.min(current.getMaxStackSize(), getInventoryStackLimit());
    }

    @Override public int getSizeInventory() { return contents.length; }
    @Override public ItemStack getStackInSlot(int slot) { return contents[slot]; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public String getInvName() { return "container.nmUnderforge"; }
    @Override public boolean isInvNameLocalized() { return false; }
    @Override public boolean isUseableByPlayer(EntityPlayer player) { return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + .5, yCoord + .5, zCoord + .5) <= 64.0; }
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return slot != SLOT_OUTPUT; }
    @Override public void openChest() {}
    @Override public void closeChest() {}

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = contents[slot];
        if (stack == null) return null;
        ItemStack result;
        if (stack.stackSize <= count) {
            result = stack;
            contents[slot] = null;
        } else {
            result = stack.splitStack(count);
            if (stack.stackSize == 0) contents[slot] = null;
        }
        onInventoryChanged();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack result = contents[slot];
        contents[slot] = null;
        return result;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        contents[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
        onInventoryChanged();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        contents = new ItemStack[5];
        progress = nbt.getInteger("Progress");
        NBTTagList list = nbt.getTagList("Items");
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound)list.tagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot < contents.length) contents[slot] = ItemStack.loadItemStackFromNBT(tag);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("Progress", progress);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < contents.length; i++) if (contents[i] != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByte("Slot", (byte)i);
            contents[i].writeToNBT(tag);
            list.appendTag(tag);
        }
        nbt.setTag("Items", list);
    }
}
