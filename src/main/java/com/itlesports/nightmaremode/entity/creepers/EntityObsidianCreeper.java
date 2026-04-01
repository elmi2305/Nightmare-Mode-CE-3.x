package com.itlesports.nightmaremode.entity.creepers;

import api.item.items.PickaxeItem;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.attribute.BTWAttributes;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMDifficultyParam;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

import static com.itlesports.nightmaremode.util.NMFields.PACKET_CREEPER_FIRE;

public class EntityObsidianCreeper extends EntityCreeperVariant{
    public EntityObsidianCreeper(World par1World) {
        super(par1World);
        this.variantType = NMFields.PACKET_CREEPER_OBSIDIAN;
        this.soundPitchModifier = -0.4f;
        this.fuseTime = 50;
        this.explosionMultiplier = 1.5f;
        this.canLunge = false;

    }

    @Override
    public boolean getCanSpawnHere() {
        return (this.posY < 24 || this.dimension == -1) && super.getCanSpawnHere();
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();

        int progress = NMUtils.getWorldProgress();
        double bloodMoonModifier = NMUtils.getIsBloodMoon() ? 1.25 : 1;
        int eclipseModifier = NMUtils.getIsMobEclipsed(this) ? 20 : 0;


        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(((24 + progress * 6) * bloodMoonModifier + eclipseModifier) * NMUtils.getNiteMultiplier());
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setAttribute((double)0.18f);
        this.getEntityAttribute(BTWAttributes.armor).setAttribute(6f + NMUtils.getWorldProgress() * 2);
    }
    @Override
    public boolean attackEntityFrom(DamageSource source, float dmg) {
        if (this.isEntityInvulnerable() || this.isBlacklistedDamage(source)) {
            return false;
        }
        return super.attackEntityFrom(source, dmg);
    }

    @Override
    protected void dropFewItems(boolean bKilledByPlayer, int iLootingModifier) {
        super.dropFewItems(bKilledByPlayer, iLootingModifier);

        if (this.getNeuteredState() == 0) {
            this.dropItem(Block.obsidian.blockID, this.rand.nextInt(2) + 1);
            this.dropItem(NMItems.obsidianShard.itemID, this.rand.nextInt(4) + 2);
        }
    }
    @Override
    public boolean interact(EntityPlayer player) {
        ItemStack playersCurrentItem = player.inventory.getCurrentItem();
        if (playersCurrentItem != null && playersCurrentItem.getItem() instanceof ItemShears) {
            if(NightmareMode.devMode){
                boolean isHostile = this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class);

                if (playersCurrentItem.getItem() instanceof ItemShears && this.getNeuteredState() == 0) {
                    if (!this.worldObj.isRemote && this.worldObj.getDifficultyParameter(NMDifficultyParam.ShouldMobsBeBuffed.class)) {
                        if (isHostile || playersCurrentItem.getItem().itemID == Item.shears.itemID) {
                            boolean mobGriefing = this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing");
                            if (this.getPowered()) {
                                if (NightmareMode.devMode) {
                                    ChatMessageComponent text2 = new ChatMessageComponent();
                                    text2.addText("[CHARGED] Calculated Explosion strength: " + getExplosionSize() * 2 * this.explosionMultiplier + " FOR: " + this.getEntityName());
                                    text2.setColor(EnumChatFormatting.BLUE);
                                    player.sendChatToPlayer(text2);
                                }

                                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, getExplosionSize() * 2 * this.explosionMultiplier, this.variantType == PACKET_CREEPER_FIRE, mobGriefing);
                            } else {
                                if (NightmareMode.devMode) {
                                    ChatMessageComponent text2 = new ChatMessageComponent();
                                    text2.addText("[NORMAL] Calculated Explosion strength: " + getExplosionSize() * this.explosionMultiplier + " FOR: " + this.getEntityName());
                                    text2.setColor(EnumChatFormatting.GREEN);
                                    player.sendChatToPlayer(text2);
                                }
                                this.worldObj.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, getExplosionSize() * this.explosionMultiplier, this.variantType == PACKET_CREEPER_FIRE, mobGriefing);
                            }
                            this.setDead();
                            return true;
                        }
                    }
                }
                return super.interact(player);
            }
            playersCurrentItem.attemptDamageItem(6, this.rand);
            this.playSound("random.break", 1.0f, 1.0f);
        }
        return false;
    }

    private boolean isBlacklistedDamage(DamageSource src) {
        if(src == DamageSource.fall
                || src == DamageSource.fallingBlock
                || src == DamageSource.lava
                || src == DamageSource.inFire
                || src == DamageSource.onFire
                || src == DamageSource.drown)
        {return true;}

        if(src.getEntity() instanceof EntityLivingBase p){
            if(p.getHeldItem() == null) return true;

            if(p.getHeldItem().getItem() instanceof PickaxeItem) return false;


            for (int i = 0; i < 2; i++) {
                double offsetY = this.rand.nextDouble() * this.height * 1.2D;
                this.worldObj.playAuxSFX(2278, (int) this.posX, (int) (this.posY + offsetY), (int) this.posZ, 0);
            }
            p.getHeldItem().attemptDamageItem(3, p.rand);
            return true;

        }

        if(src == DamageSource.magic){
            return false;
        }
        return true;
    }

}
