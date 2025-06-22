package com.itlesports.nightmaremode.block.tileEntities;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import btw.util.sounds.BTWSoundManager;
import com.itlesports.nightmaremode.block.blocks.BlockBloodChest;
import net.minecraft.src.*;

public class TileEntityBloodChest extends TileEntity implements IInventory {
    private ItemStack[] chestContents = new ItemStack[54];
    public boolean adjacentChestChecked;
    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    private int cachedChestType;
    private String customName;

    public TileEntityBloodChest() {
        this.cachedChestType = -1;
    }

    public TileEntityBloodChest(int par1) {
        this.cachedChestType = par1;
    }

    public int getSizeInventory() {
        return 54;
    }

    public ItemStack getStackInSlot(int par1) {
        return this.chestContents[par1];
    }

    public ItemStack decrStackSize(int par1, int par2) {
        if (this.chestContents[par1] != null) {
            if (this.chestContents[par1].stackSize <= par2) {
                ItemStack var3 = this.chestContents[par1];
                this.chestContents[par1] = null;
                this.onInventoryChanged();
                return var3;
            } else {
                ItemStack var3 = this.chestContents[par1].splitStack(par2);
                if (this.chestContents[par1].stackSize == 0) {
                    this.chestContents[par1] = null;
                }

                this.onInventoryChanged();
                return var3;
            }
        } else {
            return null;
        }
    }

    public ItemStack getStackInSlotOnClosing(int par1) {
        if (this.chestContents[par1] != null) {
            ItemStack var2 = this.chestContents[par1];
            this.chestContents[par1] = null;
            return var2;
        } else {
            return null;
        }
    }

    public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
        this.chestContents[par1] = par2ItemStack;
        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit()) {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }

        this.onInventoryChanged();
    }

    public String getInvName() {
        return this.isInvNameLocalized() ? this.customName : "container.chest";
    }

    public boolean isInvNameLocalized() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setChestGuiName(String par1Str) {
        this.customName = par1Str;
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
        this.chestContents = new ItemStack[this.getSizeInventory()];
        if (par1NBTTagCompound.hasKey("CustomName")) {
            this.customName = par1NBTTagCompound.getString("CustomName");
        }

        for(int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            int var5 = var4.getByte("Slot") & 255;
            if (var5 >= 0 && var5 < this.chestContents.length) {
                this.chestContents[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }

    }

    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList var2 = new NBTTagList();

        for(int var3 = 0; var3 < this.chestContents.length; ++var3) {
            if (this.chestContents[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.chestContents[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }

        par1NBTTagCompound.setTag("Items", var2);
        if (this.isInvNameLocalized()) {
            par1NBTTagCompound.setString("CustomName", this.customName);
        }

    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double)this.xCoord + (double)0.5F, (double)this.yCoord + (double)0.5F, (double)this.zCoord + (double)0.5F) <= (double)64.0F;
    }

    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        this.adjacentChestChecked = false;
    }


    private boolean func_94044_a(int par1, int par2, int par3) {
        Block var4 = Block.blocksList[this.worldObj.getBlockId(par1, par2, par3)];
        return var4 != null && var4 instanceof BlockChest ? ((BlockChest)var4).chestType == this.getChestType() : false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        ++this.ticksSinceSync;

        // Periodically recalculate how many players are using this chest
        if (!this.worldObj.isRemote && this.numUsingPlayers != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0) {
            this.numUsingPlayers = 0;
            float range = 5.0F;

            for (Object player : this.worldObj.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getAABBPool().getAABB(
                            (double)this.xCoord - range, (double)this.yCoord - range, (double)this.zCoord - range,
                            (double)(this.xCoord + 1) + range, (double)(this.yCoord + 1) + range, (double)(this.zCoord + 1) + range
                    ))) {
                if (((EntityPlayer)player).openContainer instanceof ContainerChest) {
                    IInventory inv = ((ContainerChest)((EntityPlayer)player).openContainer).getLowerChestInventory();
                    if (inv == this) {
                        ++this.numUsingPlayers;
                    }
                }
            }
        }

        this.prevLidAngle = this.lidAngle;
        float lidSpeed = 0.1F;

        // Play open sound when lid starts opening
        if (this.numUsingPlayers > 0 && this.lidAngle == 0.0F) {
            this.worldObj.playSoundEffect(
                    this.xCoord + 0.5D,
                    this.yCoord + 0.5D,
                    this.zCoord + 0.5D,
                    BTWSoundManager.CHEST_OPEN.sound(),
                    0.5F,
                    this.worldObj.rand.nextFloat() * 0.1F + 0.9F
            );
        }

        // Handle lid angle changes and play close sound if needed
        if (this.numUsingPlayers == 0 && this.lidAngle > 0.0F || this.numUsingPlayers > 0 && this.lidAngle < 1.0F) {
            float prevAngle = this.lidAngle;

            if (this.numUsingPlayers > 0) {
                this.lidAngle += lidSpeed;
            } else {
                this.lidAngle -= lidSpeed;
            }

            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }

            if (this.lidAngle < 0.5F && prevAngle >= 0.5F) {
                this.worldObj.playSoundEffect(
                        this.xCoord + 0.5D,
                        this.yCoord + 0.5D,
                        this.zCoord + 0.5D,
                        BTWSoundManager.CHEST_CLOSE.sound(),
                        0.5F,
                        this.worldObj.rand.nextFloat() * 0.1F + 0.9F
                );
            }

            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }


    public boolean receiveClientEvent(int par1, int par2) {
        if (par1 == 1) {
            this.numUsingPlayers = par2;
            return true;
        } else {
            return super.receiveClientEvent(par1, par2);
        }
    }

    public void openChest() {
        if (this.numUsingPlayers < 0) {
            this.numUsingPlayers = 0;
        }

        ++this.numUsingPlayers;
        this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
    }

    public void closeChest() {
        if (this.getBlockType() != null && this.getBlockType() instanceof BlockBloodChest) {
            --this.numUsingPlayers;
            this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID, 1, this.numUsingPlayers);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType().blockID);
            this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType().blockID);
        }
    }

    public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack) {
        return true;
    }

    public void invalidate() {
        super.invalidate();
        this.updateContainingBlockInfo();
    }

    public int getChestType() {
        if (this.cachedChestType == -1) {
            if (this.worldObj == null || !(this.getBlockType() instanceof BlockChest)) {
                return 0;
            }

            this.cachedChestType = ((BlockChest)this.getBlockType()).chestType;
        }

        return this.cachedChestType;
    }


    public void clearContents() {
        for(int i = 0; i < this.getSizeInventory(); ++i) {
            if (this.getStackInSlot(i) != null) {
                this.setInventorySlotContents(i, (ItemStack)null);
            }
        }
    }
}
