package com.itlesports.nightmaremode.nmgui;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityDisenchantmentTable;
import net.minecraft.src.*;

public class ContainerDisenchantment extends Container
{
    public static final int ID = 50;
    private TileEntityDisenchantmentTable tileEntity;
    private int lastTotalCost;

    public ContainerDisenchantment(IInventory inv, TileEntityDisenchantmentTable tileEntity)
    {
        this.tileEntity = tileEntity;

        // Input slots
        this.addSlotToContainer(new EnchantedItemSlot(tileEntity, 0, 17, 75)); // Bottom: enchanted item

        this.addSlotToContainer(new BookSlot(tileEntity, 1, 17, 37)); // Top: books


        int height = 84 + (210 - 166) + 1;
        for (int vertical0 = 0; vertical0 < 3; ++vertical0) {
            for (int horizontal0 = 0; horizontal0 < 5; ++horizontal0) {
                this.addSlotToContainer(new OutputSlot(tileEntity, 2 + horizontal0 + vertical0 * 5, height + horizontal0 * 18 - 67, 17 + vertical0 * 18 + 21));
            }
        }

        // Player inventory
        for (int vertical = 0; vertical < 3; ++vertical) {
            for (int horizontal = 0; horizontal < 9; ++horizontal) {
                this.addSlotToContainer(new Slot(inv, horizontal + vertical * 9 + 9, 8 + horizontal * 18, height + vertical * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; ++hotbar) {
            this.addSlotToContainer(new Slot(inv, hotbar, 8 + hotbar * 18, height + 58));
        }

        // Update outputs initially
        this.updateOutputSlots();
    }

    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        super.onCraftMatrixChanged(par1IInventory);
        if (par1IInventory == this.tileEntity)
        {
            this.updateOutputSlots();
        }
    }
    private void updateOutputSlots()
    {
        ItemStack item = tileEntity.getStackInSlot(0);
        ItemStack books = tileEntity.getStackInSlot(1);

        // Clear output slots
        for (int i = 2; i < 17; ++i)
        {
            tileEntity.setInventorySlotContents(i, null);
        }

        if (item == null || !item.isItemEnchanted() || books == null || books.itemID != Item.book.itemID || books.stackSize == 0)
        {
            tileEntity.totalCost = 0;
            return;
        }

        NBTTagList enchList = item.getEnchantmentTagList();
        if (enchList == null)
        {
            tileEntity.totalCost = 0;
            return;
        }

        int numEnch = enchList.tagCount();
        if (books.stackSize < numEnch)
        {
            tileEntity.totalCost = 0;
            return; // Not enough books
        }

        int outIdx = 2;
        int totalCost = 0;
        for (int i = 0; i < numEnch; ++i)
        {
            if (outIdx > 16) break;
            NBTTagCompound ench = (NBTTagCompound)enchList.tagAt(i);
            short id = ench.getShort("id");
            short lvl = ench.getShort("lvl");

            ItemStack scroll = new ItemStack(BTWItems.arcaneScroll, 1, id);
            tileEntity.setInventorySlotContents(outIdx, scroll);

            int costThis = Math.max(2, Math.min(6, (int)((lvl * 1.5f)) + numEnch));
            totalCost += costThis;

            outIdx++;
        }
        tileEntity.totalCost = totalCost;
    }

    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int var1 = 0; var1 < this.crafters.size(); ++var1)
        {
            ICrafting var2 = (ICrafting)this.crafters.get(var1);

            if (this.lastTotalCost != this.tileEntity.totalCost)
            {
                var2.sendProgressBarUpdate(this, 1, this.tileEntity.totalCost);
            }
        }

        this.lastTotalCost = this.tileEntity.totalCost;
        this.updateOutputSlots();
    }

    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 1)
        {
            this.tileEntity.totalCost = par2;
        }
    }

    public void onCraftGuiOpened(ICrafting par1ICrafting)
    {
        super.onCraftGuiOpened(par1ICrafting);
        par1ICrafting.sendProgressBarUpdate(this, 1, this.tileEntity.totalCost);
    }

    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 < 11) // Tile entity slots 0-10
            {
                if (!this.mergeItemStack(var5, 11, 47, true))
                {
                    return null;
                }
            }
            else // Player inventory to inputs
            {
                if (var5.isItemEnchanted())
                {
                    if (!this.mergeItemStack(var5, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (var5.itemID == Item.book.itemID)
                {
                    if (!this.mergeItemStack(var5, 1, 2, false))
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }

            if (var5.stackSize == 0)
            {
                var4.putStack((ItemStack)null);
            }
            else
            {
                var4.onSlotChanged();
            }

            if (var5.stackSize == var3.stackSize)
            {
                return null;
            }

            var4.onPickupFromSlot(par1EntityPlayer, var5);
        }

        return var3;
    }

    private class EnchantedItemSlot extends Slot
    {
        public EnchantedItemSlot(IInventory inv, int index, int x, int y)
        {
            super(inv, index, x, y);
        }

        public boolean isItemValid(ItemStack stack)
        {
            return stack != null && stack.isItemEnchanted();
        }
    }

    private class BookSlot extends Slot
    {
        public BookSlot(IInventory inv, int index, int x, int y)
        {
            super(inv, index, x, y);
        }

        public boolean isItemValid(ItemStack stack)
        {
            return stack != null && stack.itemID == Item.book.itemID;
        }
    }

    private class OutputSlot extends Slot
    {
        public OutputSlot(IInventory inv, int index, int x, int y)
        {
            super(inv, index, x, y);
        }

        public boolean isItemValid(ItemStack stack)
        {
            return false;
        }

        public boolean canTakeStack(EntityPlayer player)
        {
            if (!this.getHasStack()) {
                return false;
            }

            ItemStack stack = this.getStack();
            int id = stack.getItemDamage();

            ItemStack item = tileEntity.getStackInSlot(0);
            if (item == null || !item.isItemEnchanted()) {
                return false;
            }

            NBTTagList enchList = item.getEnchantmentTagList();
            if (enchList == null) {
                return false;
            }

            int numEnch = enchList.tagCount();
            int lvl = 0;
            for (int i = 0; i < enchList.tagCount(); ++i) {
                NBTTagCompound e = (NBTTagCompound)enchList.tagAt(i);
                if (e.getShort("id") == (short)id) {
                    lvl = e.getShort("lvl");
                    break;
                }
            }
            if (lvl == 0) {
                return false;
            }

            int cost = Math.max(2, Math.min(8, lvl + numEnch));

            ItemStack books = tileEntity.getStackInSlot(1);
            boolean hasBook = books != null && books.stackSize > 0 && books.itemID == Item.book.itemID;

            return hasBook && player.experienceLevel >= cost;
        }

        public void onPickupFromSlot(EntityPlayer player, ItemStack stack)
        {
            int id = stack.getItemDamage();

            ItemStack item = tileEntity.getStackInSlot(0);
            if (item != null && item.isItemEnchanted())
            {
                NBTTagList itemEnch = item.getEnchantmentTagList();
                if (itemEnch != null)
                {
                    int numEnch = itemEnch.tagCount();
                    int lvl = 0;
                    for (int i = 0; i < itemEnch.tagCount(); ++i) {
                        NBTTagCompound e = (NBTTagCompound)itemEnch.tagAt(i);
                        if (e.getShort("id") == (short)id) {
                            lvl = e.getShort("lvl");
                            break;
                        }
                    }
                    if (lvl > 0) {
                        int cost = Math.max(2, Math.min(8, lvl + numEnch));
                        player.addExperienceLevel(-cost);

                        NBTTagList newList = new NBTTagList();
                        boolean removed = false;
                        for (int i = 0; i < itemEnch.tagCount(); ++i)
                        {
                            NBTTagCompound e = (NBTTagCompound)itemEnch.tagAt(i);
                            if (e.getShort("id") == (short)id)
                            {
                                removed = true;
                                continue;
                            }
                            newList.appendTag(e);
                        }
                        if (removed)
                        {
                            if (newList.tagCount() == 0)
                            {
                                item.getTagCompound().removeTag("ench");
                            }
                            else
                            {
                                item.getTagCompound().setTag("ench", newList);
                            }
                        }
                    }
                }
            }

            // Consume one book
            ItemStack books = tileEntity.getStackInSlot(1);
            if (books != null && books.stackSize > 0)
            {
                --books.stackSize;
                if (books.stackSize <= 0)
                {
                    tileEntity.setInventorySlotContents(1, null);
                }
            }

            // Update slots
            updateOutputSlots();

            super.onPickupFromSlot(player, stack);
        }
    }
}