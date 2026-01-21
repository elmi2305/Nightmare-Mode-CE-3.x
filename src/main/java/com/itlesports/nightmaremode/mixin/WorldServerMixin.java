package com.itlesports.nightmaremode.mixin;

import api.world.WorldUtils;
import api.world.data.DataEntry;
import btw.community.nightmaremode.NightmareMode;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World {
    @Shadow public abstract <T> void setData(DataEntry.WorldDataEntry<T> entry, T value);

    @Unique private int subTick = 0;

    public WorldServerMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setWorldTime(J)V"))
    private long realTime(long par1){
        if(NightmareMode.realTime) {

            // each amount is adjusted by 200 ticks
            // morning of day 5 becomes endgame, morning of day 3 becomes hardmode
            if (this.getWorldTime() > 96000 && !WorldUtils.gameProgressHasWitherBeenSummonedServerOnly()) {
                WorldUtils.gameProgressSetWitherHasBeenSummonedServerOnly();
                ChatMessageComponent text1 = new ChatMessageComponent();
                text1.addKey("world.endgameBegin");
                text1.setColor(EnumChatFormatting.DARK_RED);
                for (Object player : this.playerEntities) {
                    if (!(player instanceof EntityPlayer p)) continue;
                    p.sendChatToPlayer(text1);
                    p.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    this.playSoundEffect(p.posX, p.posY, p.posZ, "mob.wither.death", 1f, 0.5f);
                }
            } else if (this.getWorldTime() > 47600 && !WorldUtils.gameProgressHasNetherBeenAccessedServerOnly()) {
                WorldUtils.gameProgressSetNetherBeenAccessedServerOnly();
                ChatMessageComponent text1 = new ChatMessageComponent();
                text1.addKey("world.hardmodeBegin");
                text1.setColor(EnumChatFormatting.DARK_RED);
                for (Object player : this.playerEntities) {
                    if (!(player instanceof EntityPlayer p)) continue;
                    p.sendChatToPlayer(text1);
                    p.addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 0));
                    this.playSoundEffect(p.posX, p.posY, p.posZ, "mob.wither.death", 1f, 0.5f);
                }
            }



            this.subTick++;
            if (this.subTick % 36 != 0) {
                return par1 - 1L;
            } else {
                this.subTick = 0;
                return par1;
            }



        }
        return par1;
    }
}
