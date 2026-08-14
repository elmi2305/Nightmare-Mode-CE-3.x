package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.block.blocks.BlockEnderAssembler;
import com.itlesports.nightmaremode.crafting.manager.EnderAssemblerRecipeManager;
import com.itlesports.nightmaremode.crafting.recipe.types.EnderAssemblerRecipe;
import net.minecraft.src.*;

public class EnderAssemblerTileEntity extends TileEntity implements IInventory {
    private final ItemStack[] inventory = new ItemStack[7];
    private int processTicks;
    private int processTotal = 400;

    @Override public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) return;
        Block block = Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
        if (!(block instanceof BlockEnderAssembler assembler) || !assembler.isInputtingMechanicalPower(this.worldObj, this.xCoord, this.yCoord, this.zCoord)) {
            this.processTicks = 0;
            this.setActive(false);
            return;
        }
        EnderAssemblerRecipe recipe = EnderAssemblerRecipeManager.instance.find(this);
        if (recipe == null || !this.canAccept(recipe.getOutput())) {
            this.processTicks = 0;
            this.setActive(false);
            return;
        }
        this.processTotal = recipe.getDuration();
        this.setActive(true);
        if (++this.processTicks < this.processTotal) return;
        this.processTicks = 0;
        recipe.consume(this);
        ItemStack result = recipe.getOutput();
        if (this.inventory[6] == null) this.inventory[6] = result;
        else this.inventory[6].stackSize += result.stackSize;
        this.onInventoryChanged();
    }

    private boolean canAccept(ItemStack result) {
        ItemStack output = this.inventory[6];
        return output == null || output.isItemEqual(result)
                && output.stackSize + result.stackSize <= Math.min(output.getMaxStackSize(), this.getInventoryStackLimit());
    }

    private void setActive(boolean active) {
        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int updated = active ? meta | 1 : meta & ~1;
        if (updated != meta) this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, updated, 3);
    }

    public int getProcessTicks() { return this.processTicks; }
    public int getProcessTotal() { return this.processTotal; }
    public void setProcessTicks(int ticks) { this.processTicks = ticks; }
    public void setProcessTotal(int total) { this.processTotal = total; }

    @Override public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("ProcessTicks", this.processTicks);
        tag.setInteger("ProcessTotal", this.processTotal);
        NBTTagList list = new NBTTagList();
        for (int slot = 0; slot < this.inventory.length; ++slot) {
            if (this.inventory[slot] == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte)slot);
            this.inventory[slot].writeToNBT(item);
            list.appendTag(item);
        }
        tag.setTag("Items", list);
    }

    @Override public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.processTicks = tag.getInteger("ProcessTicks");
        this.processTotal = tag.hasKey("ProcessTotal") ? Math.max(1, tag.getInteger("ProcessTotal")) : 400;
        NBTTagList list = tag.getTagList("Items");
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagCompound item = (NBTTagCompound)list.tagAt(i);
            int slot = item.getByte("Slot") & 255;
            if (slot < this.inventory.length) this.inventory[slot] = ItemStack.loadItemStackFromNBT(item);
        }
    }

    @Override public int getSizeInventory() { return this.inventory.length; }
    @Override public ItemStack getStackInSlot(int slot) { return this.inventory[slot]; }
    @Override public ItemStack decrStackSize(int slot, int count) {
        ItemStack stack = this.inventory[slot];
        if (stack == null) return null;
        if (stack.stackSize <= count) { this.inventory[slot] = null; return stack; }
        ItemStack result = stack.splitStack(count);
        if (stack.stackSize <= 0) this.inventory[slot] = null;
        return result;
    }
    @Override public ItemStack getStackInSlotOnClosing(int slot) { ItemStack stack = this.inventory[slot]; this.inventory[slot] = null; return stack; }
    @Override public void setInventorySlotContents(int slot, ItemStack stack) {
        this.inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) stack.stackSize = this.getInventoryStackLimit();
        this.onInventoryChanged();
    }
    @Override public String getInvName() { return "container.ifhyEnderAssembler"; }
    @Override public boolean isInvNameLocalized() { return true; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj == null || this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
    @Override public void openChest() {}
    @Override public void closeChest() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) { return slot < 6; }
}
