package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.util.NetherPostProgress;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.src.DamageSource;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipe;
import net.minecraft.src.MerchantRecipeList;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import btw.item.BTWItems;

public abstract class EntityNetherPostVillager extends EntityVillager {
    private String postGroup = "";
    private int postSlot = -1;
    private boolean professionCompleted;
    private boolean completionReconciled;
    private boolean tradesUpdated4030 = true;

    protected EntityNetherPostVillager(World world, int profession) {
        super(world, profession);
        this.setTradeLevel(1);
        this.setPersistent(true);
        this.isImmuneToFire = true;
    }

    public abstract int getPostTier();

    public void setPostGroup(int centerX, int centerZ, int postSlot) {
        this.postGroup = centerX + ":" + centerZ;
        this.postSlot = postSlot;
    }

    public boolean hasCompletedProfession() {
        return this.professionCompleted;
    }

    public String getPostGroup() {
        return this.postGroup;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entity) {
    }

    @Override
    public void addVelocity(double x, double y, double z) {
    }

    @Override
    public void moveEntity(double x, double y, double z) {
    }

    @Override
    public void mountEntity(Entity entity) {
        if (!(entity instanceof EntityMinecart)) {
            super.mountEntity(entity);
        }
    }

    @Override
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && (this.buyingList == null || this.buyingList.isEmpty())) {
            this.buyingList = new MerchantRecipeList();
            this.tradesUpdated4030 = true;
            this.checkForNewTrades(this.getCurrentMaxNumTrades());
        }
        if (!this.worldObj.isRemote && this.buyingList != null && this.ticksExisted % 20 == 0 && !this.isTrading()) {
            for (Object entry : this.buyingList) {
                MerchantRecipe recipe = (MerchantRecipe)entry;
                ItemStack payment = recipe.getItemToBuy();
                ItemStack second = recipe.getSecondItemToBuy();
                if (payment != null && payment.getItem() == Item.bone) payment.stackSize = Math.min(16, payment.stackSize);
                if (second != null && second.getItem() == Item.bone) second.stackSize = Math.min(16, second.stackSize);
                if (!this.tradesUpdated4030 && payment != null && payment.getItem() == Item.emerald
                        && com.itlesports.nightmaremode.util.NMInitializer.isNetherInfrastructureTrade(recipe.getItemToSell().itemID)) {
                    payment.stackSize = (payment.stackSize + 1) / 2;
                }
            }
            this.tradesUpdated4030 = true;
            this.addGuaranteedTrades();
        }
        if (!this.worldObj.isRemote && !this.completionReconciled && this.getCurrentTradeLevel() >= 5) {
            this.professionCompleted = true;
            this.updatePostCompletion();
            this.completionReconciled = true;
        }
        super.onLivingUpdate();
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
    }

    public void addGuaranteedTrades() {
        if (this.worldObj.isRemote || this.buyingList == null) return;
        boolean hasIncome = false;
        boolean hasAquamarine = false;
        for (Object entry : this.buyingList) {
            MerchantRecipe recipe = (MerchantRecipe)entry;
            ItemStack payment = recipe.getItemToBuy();
            if (payment != null && payment.getItem() == BTWItems.groundNetherrack
                    && recipe.getItemToSell().getItem() == Item.emerald && recipe.getItemToSell().stackSize >= 3) hasIncome = true;
            if (recipe.getItemToSell().getItem() == com.itlesports.nightmaremode.item.NMItems.aquamarine) hasAquamarine = true;
        }
        if (!hasIncome) this.buyingList.addToListWithCheck(new MerchantRecipe(
                new ItemStack(BTWItems.groundNetherrack, 24), new ItemStack(Item.emerald, 3)));
        if (this.getPostTier() == 2 && !hasAquamarine) this.buyingList.addToListWithCheck(new MerchantRecipe(
                new ItemStack(Item.emerald, 1 + this.rand.nextInt(2)),
                new ItemStack(com.itlesports.nightmaremode.item.NMItems.aquamarine, 8 + this.rand.nextInt(9))));
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean getCanCreatureTypeBePossessed() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int looting) {
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        int previousLevel = this.getCurrentTradeLevel();
        super.useRecipe(recipe);
        if (!this.worldObj.isRemote && previousLevel < 5 && this.getCurrentTradeLevel() >= 5) {
            this.professionCompleted = true;
            this.updatePostCompletion();
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.isChild()) return false;
        if (com.itlesports.nightmaremode.util.VillagerTradeReroll.useApple(this, player)) return true;
        if (!this.worldObj.isRemote && this.getPostTier() == 3 && this.getCurrentTradeLevel() >= 4) {
            if ((NetherPostProgress.completedTiers(player) & 3) != 3) {
                if (!this.worldObj.isRemote) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText(
                            "This villager's final commission requires completed Tier 1 and Tier 2 posts."));
                }
                return false;
            }
        }
        return super.interact(player);
    }

    private void updatePostCompletion() {
        if (this.postSlot < 0) {
            this.postSlot = this.inferPostSlot();
        }
        WorldSkillData data = NetherPostProgress.worldData(this.worldObj);
        int completionMask = data.markNetherPostVillagerComplete(this.getPostTier(), this.postGroup, this.postSlot);
        NetherPostProgress.savedWorld(this.worldObj).setData(btw.community.nightmaremode.NightmareMode.WORLD_SKILL_TREE, data);
        if ((completionMask & 15) != 15) {
            return;
        }
        boolean newlyCompleted = false;
        if (this.getPostTier() == 1) {
            newlyCompleted = !data.netherVillagerTier1Complete;
            data.netherVillagerTier1Complete = true;
        } else if (this.getPostTier() == 2) {
            newlyCompleted = !data.netherVillagerTier2Complete;
            data.netherVillagerTier2Complete = true;
        } else {
            newlyCompleted = !data.netherVillagerTier3Complete;
            data.netherVillagerTier3Complete = true;
        }
        EntityPlayer customer = this.getCustomer();
        for (Object obj : this.worldObj.playerEntities) {
            if (obj instanceof EntityPlayer player) {
                NetherPostProgress.completedTiers(player);
            }
        }
        if (newlyCompleted && customer != null) {
            customer.sendChatToPlayer(ChatMessageComponent.createFromText(
                    "Nether villager post Tier " + this.getPostTier() + " complete: 4/4 villagers."));
        }
        NetherPostProgress.savedWorld(this.worldObj).setData(btw.community.nightmaremode.NightmareMode.WORLD_SKILL_TREE, data);
    }

    public void debugNotifyTradeLevelChanged(int previousLevel) {
        if (!this.worldObj.isRemote && previousLevel < 5 && this.getCurrentTradeLevel() >= 5
                && !this.professionCompleted) {
            this.professionCompleted = true;
            this.updatePostCompletion();
        }
    }

    private int inferPostSlot() {
        try {
            String[] center = this.postGroup.split(":");
            double centerX = Double.parseDouble(center[0]);
            double centerZ = Double.parseDouble(center[1]);
            boolean positiveX = this.posX >= centerX;
            boolean positiveZ = this.posZ >= centerZ;
            if (positiveX) return positiveZ ? 0 : 1;
            return positiveZ ? 2 : 3;
        } catch (RuntimeException ignored) {
            return -1;
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("NmNetherPostGroup", this.postGroup);
        tag.setInteger("NmNetherPostSlot", this.postSlot);
        tag.setBoolean("NmPostProfessionComplete", this.professionCompleted);
        tag.setBoolean("NmTradesUpdated4030", this.tradesUpdated4030);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.postGroup = tag.getString("NmNetherPostGroup");
        this.postSlot = tag.hasKey("NmNetherPostSlot") ? tag.getInteger("NmNetherPostSlot") : -1;
        this.professionCompleted = tag.getBoolean("NmPostProfessionComplete") || this.getCurrentTradeLevel() >= 5;
        this.tradesUpdated4030 = tag.getBoolean("NmTradesUpdated4030");
    }
}
