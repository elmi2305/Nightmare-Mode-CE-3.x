package com.itlesports.nightmaremode.block.tileEntities;

import net.minecraft.src.*;

import java.util.Random;

public class TileEntityDisenchantmentTable extends TileEntity implements IInventory {
    public int tickCount;
    public float pageFlip;
    public float pageFlipPrev;
    public float field_70373_d;
    public float field_70374_e;
    public float bookSpread;
    public float bookSpreadPrev;
    public float bookRotation2;
    public float bookRotationPrev;
    public float bookRotation;
    public int totalCost;
    private static Random rand = new Random();
    private String name;
    public boolean playerNear = false;
    private ItemStack[] inventory = new ItemStack[17];

    public TileEntityDisenchantmentTable() {
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        if (this.isNameValid()) {
            par1NBTTagCompound.setString("CustomName", this.name);
        }
        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < this.inventory.length; ++i) {
            if (this.inventory[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte)i);
                this.inventory[i].writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }
        par1NBTTagCompound.setTag("Items", itemList);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        if (par1NBTTagCompound.hasKey("CustomName")) {
            this.name = par1NBTTagCompound.getString("CustomName");
        }
        NBTTagList itemList = par1NBTTagCompound.getTagList("Items");
        for (int i = 0; i < itemList.tagCount(); ++i) {
            NBTTagCompound tag = (NBTTagCompound)itemList.tagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot >= 0 && slot < this.inventory.length) {
                this.inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void updateEntity() {
        float var7;
        super.updateEntity();
        this.bookSpreadPrev = this.bookSpread;
        this.bookRotationPrev = this.bookRotation2;
        EntityPlayer var1 = this.worldObj.getClosestPlayer((float)this.xCoord + 0.5f, (float)this.yCoord + 0.5f, (float)this.zCoord + 0.5f, 4.5);
        if (var1 != null) {
            if (!this.playerNear) {
                this.playerNear = true;
            }
            double var2 = var1.posX - (double)((float)this.xCoord + 0.5f);
            double var4 = var1.posZ - (double)((float)this.zCoord + 0.5f);
            this.bookRotation = (float)Math.atan2(var4, var2);
            this.bookSpread += 0.1f;
            if (this.bookSpread < 0.5f || rand.nextInt(40) == 0) {
                float var6 = this.field_70373_d;
                do {
                    this.field_70373_d += (float)(rand.nextInt(4) - rand.nextInt(4));
                } while (var6 == this.field_70373_d);
            }
        } else {
            this.bookRotation += 0.02f;
            this.bookSpread -= 0.1f;
            this.playerNear = false;
        }
        while (this.bookRotation2 >= (float)Math.PI) {
            this.bookRotation2 -= (float)Math.PI * 2;
        }
        while (this.bookRotation2 < (float)(-Math.PI)) {
            this.bookRotation2 += (float)Math.PI * 2;
        }
        while (this.bookRotation >= (float)Math.PI) {
            this.bookRotation -= (float)Math.PI * 2;
        }
        while (this.bookRotation < (float)(-Math.PI)) {
            this.bookRotation += (float)Math.PI * 2;
        }
        for (var7 = this.bookRotation - this.bookRotation2; var7 >= (float)Math.PI; var7 -= (float)Math.PI * 2) {
        }
        while (var7 < (float)(-Math.PI)) {
            var7 += (float)Math.PI * 2;
        }
        this.bookRotation2 += var7 * 0.4f;
        if (this.bookSpread < 0.0f) {
            this.bookSpread = 0.0f;
        }
        if (this.bookSpread > 1.0f) {
            this.bookSpread = 1.0f;
        }
        ++this.tickCount;
        this.pageFlipPrev = this.pageFlip;
        float var3 = (this.field_70373_d - this.pageFlip) * 0.4f;
        float var8 = 0.2f;
        if (var3 < -var8) {
            var3 = -var8;
        }
        if (var3 > var8) {
            var3 = var8;
        }
        this.field_70374_e += (var3 - this.field_70374_e) * 0.9f;
        this.pageFlip += this.field_70374_e;
    }

    public String func_94133_a() {
        return I18n.getString("gui.container_disenchantment");
    }

    public boolean isNameValid() {
        return this.name != null && this.name.length() > 0;
    }

    public void func_94134_a(String par1Str) {
        this.name = par1Str;
    }

    @Override
    public int getSizeInventory() {
        return 17;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        return this.inventory[i];
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (this.inventory[i] != null) {
            ItemStack itemstack;
            if (this.inventory[i].stackSize <= j) {
                itemstack = this.inventory[i];
                this.inventory[i] = null;
                return itemstack;
            } else {
                itemstack = this.inventory[i].splitStack(j);
                if (this.inventory[i].stackSize == 0) {
                    this.inventory[i] = null;
                }
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        if (this.inventory[i] != null) {
            ItemStack itemstack = this.inventory[i];
            this.inventory[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemStack) {
        this.inventory[i] = itemStack;
        if (itemStack != null && itemStack.stackSize > this.getInventoryStackLimit()) {
            itemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public String getInvName() {
        return this.func_94133_a();
    }

    @Override
    public boolean isInvNameLocalized() {
        return this.isNameValid();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return par1EntityPlayer.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemStack) {
        if (i == 0) {
            return itemStack != null && itemStack.isItemEnchanted();
        } else if (i == 1) {
            return itemStack != null && itemStack.itemID == Item.book.itemID;
        } else {
            return false;
        }
    }
}