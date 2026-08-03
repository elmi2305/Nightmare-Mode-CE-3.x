package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.src.DamageSource;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipe;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public abstract class EntityNetherPostVillager extends EntityVillager {
    private String postGroup = "";
    private int postSlot = -1;
    private boolean professionCompleted;

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
    protected boolean isMovementBlocked() {
        return true;
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
        super.onLivingUpdate();
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
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
        if (this.getPostTier() == 3 && this.getCurrentTradeLevel() >= 4) {
            WorldSkillData data = SkillHandler.getWorldData(this.worldObj);
            if (!data.netherVillagerTier1Complete || !data.netherVillagerTier2Complete) {
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
        WorldSkillData data = SkillHandler.getWorldData(this.worldObj);
        int completionMask = data.markNetherPostVillagerComplete(this.getPostTier(), this.postGroup, this.postSlot);
        this.worldObj.setData(btw.community.nightmaremode.NightmareMode.WORLD_SKILL_TREE, data);
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
        if (newlyCompleted && customer != null) {
            customer.sendChatToPlayer(ChatMessageComponent.createFromText(
                    "Nether villager post Tier " + this.getPostTier() + " complete: 4/4 villagers."));
        }
        this.worldObj.setData(btw.community.nightmaremode.NightmareMode.WORLD_SKILL_TREE, data);
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
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.postGroup = tag.getString("NmNetherPostGroup");
        this.postSlot = tag.hasKey("NmNetherPostSlot") ? tag.getInteger("NmNetherPostSlot") : -1;
        this.professionCompleted = tag.getBoolean("NmPostProfessionComplete") || this.getCurrentTradeLevel() >= 5;
    }
}
