package com.itlesports.nightmaremode.mixin;

import api.world.WorldUtils;
import api.world.data.DataEntry;
import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.interfaces.WorldServerExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldServer.class)
public abstract class WorldServerMixin extends World implements WorldServerExt {
    @Shadow public abstract <T> void setData(DataEntry.WorldDataEntry<T> entry, T value);

    @Unique private boolean oldBlueMoon;
    @Unique private int subTick = 0;



    public WorldServerMixin(ISaveHandler par1ISaveHandler, String par2Str, WorldProvider par3WorldProvider, WorldSettings par4WorldSettings, Profiler par5Profiler, ILogAgent par6ILogAgent) {
        super(par1ISaveHandler, par2Str, par3WorldProvider, par4WorldSettings, par5Profiler, par6ILogAgent);
    }

//    @Inject(method = "initialize", at = @At("TAIL"))
//    private void resetBlueMoonStuff(WorldSettings par1WorldSettings, CallbackInfo ci){
//        oldBlueMoon = NightmareMode.isBlueMoon;
//        NightmareMode.isBlueMoon = false;
//    }

    @Unique private boolean isBlueMoonWorld;
    @Inject(method = "tick", at = @At("TAIL"))
    private void manageBlueMoons(CallbackInfo ci){
//        if(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION){
//            if(time % 100 != 1) return;
//            NightmareMode.setBlueMoon(
//                    this.getIsBlueMoon(this)
//            );
//            boolean shouldChangeBlueMoon   = NightmareMode.isBlueMoon   != oldBlueMoon;
//            if(shouldChangeBlueMoon){
//                System.out.println("sending packet");
//                NightmareMode.sendMoonAndSunEventsToAllPlayers();
//            }
//
//            oldBlueMoon   = NightmareMode.isBlueMoon;
//        }
        long time = this.getWorldTime();
        if(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION &&time % 20 == 1 && !this.isRemote) {
            // only server logic for now
            this.setBlueMoonWorld(this.calculateIsBlueMoon(this));
//            System.out.println("calculated blue moon: " + this.isBlueMoonWorld);
        } else{
            this.setBlueMoonWorld(false);
        }
    }
    @Unique private void setBlueMoonWorld(boolean b){
        this.isBlueMoonWorld = b;
    }


    @Unique private boolean calculateIsBlueMoon(World world){
//        if(NMUtils.getWorldProgress() <= 1){return false;}
        // TODO include the upper condition. it's off for debugging

//        System.out.println(this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION ? "UNDERWORLD" : "SOMETHING ELSE");
//        System.out.println("Night: " + getIsNightFromWorldTime(world));
//        System.out.println("Moon Phase: " + world.getMoonPhase());
//        System.out.println("OUTPUT: " + (this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION && this.getIsNightFromWorldTime(world) && world.getMoonPhase() == 0));
//        System.out.println(" ");
        // works as expected
        return this.worldInfo.dimension == NMFields.UNDERWORLD_DIMENSION && this.getIsNightFromWorldTime(world) && world.getMoonPhase() == 0;
    }
    @Unique private boolean getIsNightFromWorldTime(World world){
        return world.getWorldTime() % 24000 >= 12541 && world.getWorldTime() % 24000 <= 23459;
    }

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/WorldInfo;setWorldTime(J)V"))
    private long realTime(long worldTime){
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
                return worldTime - 1L;
            } else {
                this.subTick = 0;
                return worldTime;
            }
        }
        return worldTime;
    }

    @Override
    public boolean nightmareMode$getIsBlueMoon() {
        return this.isBlueMoonWorld;
    }

    @Override
    public void nightmareMode$setIsBlueMoon(boolean isBlueMoon) {
        this.setBlueMoonWorld(isBlueMoon);
        // probably won't be used - no need to call it externally
    }
}
