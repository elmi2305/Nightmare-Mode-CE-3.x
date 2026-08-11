package com.itlesports.nightmaremode.block.tileEntities;

import com.itlesports.nightmaremode.agriculture.ChunkAttribute;
import com.itlesports.nightmaremode.agriculture.ChunkAttributeManager;
import com.itlesports.nightmaremode.agriculture.ChunkAttributes;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

public class TerrainExtractorTileEntity extends TileEntity implements IInventory {
    public static final int PROCESS_TICKS = 600;
    private final ItemStack[] inventory = new ItemStack[3];
    private int fuelTicks;
    private int processTicks;
    private int fieldMilli;
    private int fieldType;

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) return;
        ChunkAttribute attribute = this.getAttribute();
        ChunkAttributes fields = ChunkAttributeManager.get(this.worldObj, this.xCoord, this.zCoord);
        this.fieldMilli = Math.round(fields.get(attribute) * 1000.0F);
        if (!this.hasValidSubstrate() || fields.get(attribute) < 0.01F || !this.canOutput(attribute)) {
            this.processTicks = 0;
            return;
        }
        if (this.fuelTicks <= 0 && !this.consumeFuel()) {
            this.processTicks = 0;
            return;
        }
        --this.fuelTicks;
        ++this.processTicks;
        if (this.processTicks % 20 == 0) {
            fields.consume(attribute, 0.01F);
            this.worldObj.getChunkFromChunkCoords(this.xCoord >> 4, this.zCoord >> 4).setChunkModified();
            this.fieldMilli = Math.round(fields.get(attribute) * 1000.0F);
        }
        if (this.processTicks >= PROCESS_TICKS) {
            this.processTicks = 0;
            this.produce(attribute);
        }
    }

    private boolean hasValidSubstrate() {
        for (int depth = 1; depth <= 3; ++depth) {
            int id = this.worldObj.getBlockId(this.xCoord, this.yCoord - depth, this.zCoord);
            if (id != Block.dirt.blockID && id != Block.grass.blockID && id != Block.sand.blockID
                    && id != Block.gravel.blockID) return false;
        }
        return true;
    }

    private ChunkAttribute getAttribute() {
        return switch (this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) % 5) {
            case 0 -> ChunkAttribute.POTASSIUM;
            case 1 -> ChunkAttribute.NITROGEN;
            case 2 -> ChunkAttribute.MOISTURE;
            case 3 -> ChunkAttribute.POROSITY;
            default -> ChunkAttribute.ACIDITY;
        };
    }

    private ItemStack getResult(ChunkAttribute attribute) {
        return switch (attribute) {
            case POTASSIUM -> new ItemStack(NMItems.potassiumCrystal);
            case NITROGEN -> new ItemStack(NMItems.nitrogenCrystal);
            case MOISTURE -> new ItemStack(Item.bucketWater);
            case POROSITY -> new ItemStack(NMItems.porosityAggregate);
            case ACIDITY -> new ItemStack(NMItems.acidCrystal);
        };
    }

    private boolean canOutput(ChunkAttribute attribute) {
        if (attribute == ChunkAttribute.MOISTURE
                && (this.inventory[1] == null || this.inventory[1].itemID != Item.bucketEmpty.itemID)) return false;
        ItemStack result = this.getResult(attribute);
        ItemStack output = this.inventory[2];
        return output == null || output.isItemEqual(result)
                && output.stackSize + result.stackSize <= Math.min(output.getMaxStackSize(), this.getInventoryStackLimit());
    }

    private void produce(ChunkAttribute attribute) {
        if (attribute == ChunkAttribute.MOISTURE && --this.inventory[1].stackSize <= 0) this.inventory[1] = null;
        ItemStack result = this.getResult(attribute);
        if (this.inventory[2] == null) this.inventory[2] = result;
        else this.inventory[2].stackSize += result.stackSize;
        this.onInventoryChanged();
    }

    private boolean consumeFuel() {
        ItemStack fuel = this.inventory[0];
        if (fuel == null || fuel.itemID != Item.coal.itemID) return false;
        if (--fuel.stackSize <= 0) this.inventory[0] = null;
        this.fuelTicks = 1600;
        this.onInventoryChanged();
        return true;
    }

    public int getFuelTicks() { return this.fuelTicks; }
    public int getProcessTicks() { return this.processTicks; }
    public int getFieldMilli() { return this.fieldMilli; }
    public void setFuelTicks(int value) { this.fuelTicks = value; }
    public void setProcessTicks(int value) { this.processTicks = value; }
    public void setFieldMilli(int value) { this.fieldMilli = value; }
    public int getFieldType() {
        return this.worldObj == null ? this.fieldType : this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) % 5;
    }
    public void setFieldType(int value) { this.fieldType = value; }
    public String getFieldName() {
        return com.itlesports.nightmaremode.block.blocks.BlockTerrainExtractor.TYPES[this.getFieldType()];
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("FuelTicks", this.fuelTicks);
        tag.setInteger("ProcessTicks", this.processTicks);
        NBTTagList items = new NBTTagList();
        for (int slot = 0; slot < this.inventory.length; ++slot) {
            if (this.inventory[slot] == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte)slot);
            this.inventory[slot].writeToNBT(item);
            items.appendTag(item);
        }
        tag.setTag("Items", items);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.fuelTicks = tag.getInteger("FuelTicks");
        this.processTicks = tag.getInteger("ProcessTicks");
        NBTTagList items = tag.getTagList("Items");
        for (int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
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
    @Override public String getInvName() { return "container.ifhyTerrainExtractor"; }
    @Override public boolean isInvNameLocalized() { return true; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj == null || this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
    @Override public void openChest() {}
    @Override public void closeChest() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0 && stack != null && stack.itemID == Item.coal.itemID
                || slot == 1 && stack != null && stack.itemID == Item.bucketEmpty.itemID;
    }
}
