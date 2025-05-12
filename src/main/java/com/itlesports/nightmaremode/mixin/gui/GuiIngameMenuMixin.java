package com.itlesports.nightmaremode.mixin.gui;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiIngameMenu;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameMenu.class)
public class GuiIngameMenuMixin extends GuiScreen {
    @Inject(method = "updateScreen", at = @At("HEAD"))
    private void setWhetherCanLeave(CallbackInfo ci){
        ((GuiButton)this.buttonList.get(0)).enabled = NightmareMode.getInstance().getCanLeaveGame();
    }
    @Inject(method = "initGui", at = @At("TAIL"))
    private void setWhetherCanLeaveOnFirstTick(CallbackInfo ci){
        ((GuiButton)this.buttonList.get(0)).enabled = NightmareMode.getInstance().getCanLeaveGame();
    }
    @Redirect(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;", ordinal = 0))
    private String manageTextOnCannotEscapeClient(String string){
        if(!NightmareMode.getInstance().getCanLeaveGame()){
            return I18n.getString("menu.noEscape");
        }
        return I18n.getString(string);
    }

    //inject copied from gloomy hostile, resets things to not infect rest
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Minecraft;loadWorld(Lnet/minecraft/src/WorldClient;)V", ordinal = 0))
    public void onLeaveWorld(GuiButton par1GuiButton, CallbackInfo ci) {
        NightmareMode.worldState = 0;
        NightmareMode.setBloodmoon(false);
        NightmareMode.setEclipse(false);
        NightmareUtils.shushMusic();
    }

//    @Redirect(method = "initGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/I18n;getString(Ljava/lang/String;)Ljava/lang/String;", ordinal = 1))
//    private String manageTextOnCannotEscapeServer(String string){
//        if(!NightmareMode.getInstance().getCanLeaveGame()){
//            return "No Escape";
//        }
//        return I18n.getString(string);
//    }
}
