package com.itlesports.nightmaremode.mixin;

import api.AddonHandler;
import btw.BTWMod;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NightmareKeyBindings;
import com.itlesports.nightmaremode.util.interfaces.ZoomStateAccessor;
import com.itlesports.nightmaremode.client.CarcassHarvestClient;
import com.itlesports.nightmaremode.client.EnderArmorClient;
import com.itlesports.nightmaremode.integration.emi.RecipeIndexExporter;
import net.minecraft.src.*;
import api.item.items.PlaceAsBlockItem;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow public GameSettings gameSettings;
    @Shadow public GuiScreen currentScreen;
    @Shadow public EntityRenderer entityRenderer;
    @Shadow public EntityClientPlayerMP thePlayer;
    @Shadow public WorldClient theWorld;
    @Shadow public MovingObjectPosition objectMouseOver;
    @Shadow public PlayerControllerMP playerController;

    @Unique private boolean wasZooming = false;
    @Unique private float originalFov = 0.0f;
    @Unique private boolean nightmareMode$railExtensionActive;
    @Unique private boolean nightmareMode$automatedRailClick;
    @Unique private int nightmareMode$borrowedRailSlot = -1;

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I", remap = false))
    private int nmBlockHotbarScrollWhenZoom() {
        if (entityRenderer instanceof ZoomStateAccessor accessor) {
            if (accessor.nightmareMode$isToggleZoomActive() && accessor.nightmareMode$isToggleZoomKeyHeld()) {
                return 0;
            }
        }
        return Mouse.getEventDWheel();
    }

    @ModifyArg(method = "startGame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
    private String changeWindowText(String newTitle){
        return newTitle + " | Better Than Wolves CE " + AddonHandler.getModByID("btw").getVersionString() + " | Journey Mode v"+ AddonHandler.getModByID(NMFields.modID).getVersionString();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void startAutomatedRecipeExport(CallbackInfo ci) {
        RecipeIndexExporter.startAutomatedExport((Minecraft)(Object)this);
    }

    @Inject(method = "screenshotListener", at = @At(value = "HEAD"))
    private void manageKeybinds(CallbackInfo ci) {
        if (Keyboard.isKeyDown(NightmareKeyBindings.nmZoomHold.keyCode) && this.currentScreen == null) {
            if (!this.wasZooming) {
                this.originalFov = this.gameSettings.fovSetting;
                this.wasZooming = true;
            }
            this.gameSettings.fovSetting = -1.2f;
        } else if (this.wasZooming) {
            this.gameSettings.fovSetting = originalFov;
            this.wasZooming = false;
        }
    }

    @Inject(method = "clickMouse", at = @At("HEAD"), cancellable = true)
    private void startOrBlockCarcassHarvest(int mouseButton, CallbackInfo ci) {
        if (mouseButton == 1 && !this.nightmareMode$automatedRailClick && GuiScreen.isCtrlKeyDown()
                && this.nightmareMode$isRailTarget()) {
            this.nightmareMode$railExtensionActive = !this.nightmareMode$railExtensionActive;
            if (!this.nightmareMode$railExtensionActive) {
                this.nightmareMode$restoreBorrowedRailSlot();
            }
        }
        if (mouseButton == 1 && EnderArmorClient.consumeEmptyHandUse((Minecraft)(Object)this)) {
            ci.cancel();
            return;
        }
        if (mouseButton == 1 && CarcassHarvestClient.consumeCarcassRightClick((Minecraft)(Object)this)) {
            ci.cancel();
        }
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void tickCarcassHarvest(CallbackInfo ci) {
        CarcassHarvestClient.tick((Minecraft)(Object)this);
        this.nightmareMode$tickAutomaticRailExtension();
        if (RecipeIndexExporter.consumeDevelopmentStopRequest()
                || RecipeIndexExporter.consumeAutomatedStopRequest()) {
            ((Minecraft)(Object)this).shutdown();
        }
    }

    @Unique
    private void nightmareMode$tickAutomaticRailExtension() {
        if (!this.nightmareMode$railExtensionActive) {
            return;
        }
        if (this.currentScreen != null || this.thePlayer == null || this.theWorld == null
                || !(this.thePlayer.ridingEntity instanceof EntityMinecartFurnace)
                || !this.nightmareMode$isRailTarget() || !this.nightmareMode$ensureRailInHand()) {
            this.nightmareMode$railExtensionActive = false;
            this.nightmareMode$restoreBorrowedRailSlot();
            return;
        }

        ItemStack held = this.thePlayer.inventory.getCurrentItem();
        if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, held,
                this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ,
                this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
            this.thePlayer.swingItem();
        }
        if (held.stackSize == 0) {
            this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
        }
    }

    @Unique
    private boolean nightmareMode$isRailTarget() {
        return this.objectMouseOver != null && this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE
                && BlockRailBase.isRailBlock(this.theWorld.getBlockId(this.objectMouseOver.blockX,
                this.objectMouseOver.blockY, this.objectMouseOver.blockZ));
    }

    @Unique
    private boolean nightmareMode$ensureRailInHand() {
        ItemStack held = this.thePlayer.inventory.getCurrentItem();
        if (this.nightmareMode$isRailStack(held)) {
            return true;
        }
        if (this.nightmareMode$borrowedRailSlot >= 0) {
            return false;
        }
        for (int slot = 0; slot < this.thePlayer.inventory.mainInventory.length; ++slot) {
            if (!this.nightmareMode$isRailStack(this.thePlayer.inventory.mainInventory[slot])) {
                continue;
            }
            int activeSlot = this.thePlayer.inventory.currentItem;
            ItemStack activeStack = this.thePlayer.inventory.mainInventory[activeSlot];
            this.thePlayer.inventory.mainInventory[activeSlot] = this.thePlayer.inventory.mainInventory[slot];
            this.thePlayer.inventory.mainInventory[slot] = activeStack;
            this.nightmareMode$borrowedRailSlot = slot;
            return true;
        }
        return false;
    }

    @Unique
    private boolean nightmareMode$isRailStack(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof PlaceAsBlockItem railItem)) {
            return false;
        }
        int railId = railItem.getBlockIDToPlace(this.theWorld, stack.getItemDamage(), 1, 0.5F, 0.5F, 0.5F);
        return railId > 0 && Block.blocksList[railId] instanceof BlockRailBase;
    }

    @Unique
    private void nightmareMode$restoreBorrowedRailSlot() {
        if (this.nightmareMode$borrowedRailSlot < 0 || this.thePlayer == null) {
            return;
        }
        int activeSlot = this.thePlayer.inventory.currentItem;
        ItemStack activeStack = this.thePlayer.inventory.mainInventory[activeSlot];
        this.thePlayer.inventory.mainInventory[activeSlot] = this.thePlayer.inventory.mainInventory[this.nightmareMode$borrowedRailSlot];
        this.thePlayer.inventory.mainInventory[this.nightmareMode$borrowedRailSlot] = activeStack;
        this.nightmareMode$borrowedRailSlot = -1;
    }
}
