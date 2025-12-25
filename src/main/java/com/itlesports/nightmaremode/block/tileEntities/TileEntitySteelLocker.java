package com.itlesports.nightmaremode.block.tileEntities;

import btw.util.BTWSounds;
import com.itlesports.nightmaremode.block.blocks.BlockSteelLocker;
import com.itlesports.nightmaremode.rendering.ContainerSteelLocker;
import net.minecraft.src.*;

public class TileEntitySteelLocker extends TileEntity implements IInventory {

    public static final int SLOT_COLS = 19;
    public static final int SLOT_ROWS = 7;
    public static final int SLOT_TOTAL = SLOT_COLS * SLOT_ROWS;

    public float lidAngle;
    public float prevLidAngle;
    public int numUsingPlayers;
    private int ticksSinceSync;
    private int cachedChestType = -1;
    private String customName;
    private ItemStack[] chestContents = new ItemStack[SLOT_TOTAL];

    @Override public int getSizeInventory() { return SLOT_TOTAL; }
    @Override public ItemStack getStackInSlot(int i) { return chestContents[i]; }

    @Override
    public ItemStack decrStackSize(int i, int count) {
        if (chestContents[i] != null) {
            ItemStack ret;
            if (chestContents[i].stackSize <= count) {
                ret = chestContents[i];
                chestContents[i] = null;
            } else {
                ret = chestContents[i].splitStack(count);
                if (chestContents[i].stackSize == 0) chestContents[i] = null;
            }
            onInventoryChanged();
            return ret;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        if (chestContents[i] != null) {
            ItemStack s = chestContents[i];
            chestContents[i] = null;
            return s;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        chestContents[i] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
        onInventoryChanged();
    }

    @Override
    public String getInvName() {
        return isInvNameLocalized() ? customName : "container.steelLocker";
    }

    @Override
    public boolean isInvNameLocalized() {
        return customName != null && !customName.isEmpty();
    }

    public void setChestGuiName(String name) {
        this.customName = name;
    }

    @Override
    public int getInventoryStackLimit() { return 64; }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getBlockTileEntity(xCoord,yCoord,zCoord) == this &&
                player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64D;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) { return true; }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        chestContents = new ItemStack[SLOT_TOTAL];
        if (nbt.hasKey("CustomName"))
            customName = nbt.getString("CustomName");

        NBTTagList list = nbt.getTagList("Items");
        for (int i=0;i<list.tagCount();i++) {
            NBTTagCompound tag = (NBTTagCompound)list.tagAt(i);
            int slot = tag.getByte("Slot") & 255;
            if (slot < chestContents.length) {
                chestContents[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        for (int i=0;i<chestContents.length;i++) {
            if (chestContents[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot",(byte)i);
                chestContents[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        nbt.setTag("Items", list);
        if (isInvNameLocalized()) nbt.setString("CustomName", customName);
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();
        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        ++ticksSinceSync;

        if (!worldObj.isRemote && numUsingPlayers != 0 &&
                (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
            numUsingPlayers = 0;
            float range = 5F;
            for (Object o : worldObj.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getAABBPool().getAABB(
                            xCoord - range, yCoord - range, zCoord - range,
                            xCoord + 1 + range, yCoord + 1 + range, zCoord + 1 + range))) {
                EntityPlayer p = (EntityPlayer)o;
                if (p.openContainer instanceof ContainerSteelLocker cont) {
                    if (cont.getChestInventory() == this) ++numUsingPlayers;
                }
            }
        }

        prevLidAngle = lidAngle;
        float speed = 0.1F;

        if (numUsingPlayers > 0 && lidAngle == 0F) {
            worldObj.playSoundEffect(
                    xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
                    BTWSounds.CHEST_OPEN.sound(),
                    0.5F,
                    worldObj.rand.nextFloat()*0.1F + 0.9F);
        }

        if ((numUsingPlayers == 0 && lidAngle > 0F) || (numUsingPlayers > 0 && lidAngle < 1F)) {
            float prev = lidAngle;
            if (numUsingPlayers > 0) lidAngle += speed;
            else lidAngle -= speed;

            if (lidAngle > 1F) lidAngle = 1F;
            if (lidAngle < 0.5F && prev >= 0.5F) {
                worldObj.playSoundEffect(
                        xCoord + 0.5, yCoord + 0.5, zCoord + 0.5,
                        BTWSounds.CHEST_CLOSE.sound(),
                        0.5F,
                        worldObj.rand.nextFloat()*0.1F + 0.9F);
            }
            if (lidAngle < 0F) lidAngle = 0F;
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            numUsingPlayers = value;
            return true;
        }
        return super.receiveClientEvent(id,value);
    }

    public void openChest() {
        if (numUsingPlayers < 0) numUsingPlayers = 0;
        ++numUsingPlayers;
        worldObj.addBlockEvent(xCoord,yCoord,zCoord,getBlockType().blockID,1,numUsingPlayers);
        worldObj.notifyBlocksOfNeighborChange(xCoord,yCoord,zCoord,getBlockType().blockID);
        worldObj.notifyBlocksOfNeighborChange(xCoord,yCoord-1,zCoord,getBlockType().blockID);
    }

    public void closeChest() {
        if (getBlockType() instanceof BlockSteelLocker) {
            --numUsingPlayers;
            worldObj.addBlockEvent(xCoord,yCoord,zCoord,getBlockType().blockID,1,numUsingPlayers);
            worldObj.notifyBlocksOfNeighborChange(xCoord,yCoord,zCoord,getBlockType().blockID);
            worldObj.notifyBlocksOfNeighborChange(xCoord,yCoord-1,zCoord,getBlockType().blockID);
        }
    }

    public int getChestType() {
        if (cachedChestType == -1) {
            if (getBlockType() instanceof BlockSteelLocker b) cachedChestType = b.chestType;
            else cachedChestType = 0;
        }
        return cachedChestType;
    }

    public void clearContents() {
        for (int i=0;i<getSizeInventory();i++) chestContents[i] = null;
        onInventoryChanged();
    }
}