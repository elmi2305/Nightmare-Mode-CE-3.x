package com.itlesports.nightmaremode.mixin;

import api.AddonHandler;
import btw.BTWMod;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NightmareKeyBindings;
import com.itlesports.nightmaremode.nmgui.GuiJoiningWorld;
import com.itlesports.nightmaremode.util.interfaces.ZoomStateAccessor;
import com.itlesports.nightmaremode.client.CarcassHarvestClient;
import com.itlesports.nightmaremode.client.EnderArmorClient;
import com.itlesports.nightmaremode.integration.emi.RecipeIndexExporter;
import net.minecraft.server.MinecraftServer;
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
    @Shadow private IntegratedServer theIntegratedServer;

    @Unique private boolean wasZooming = false;
    @Unique private float originalFov = 0.0f;
    @Unique private boolean nightmareMode$railExtensionActive;
    @Unique private boolean nightmareMode$automatedRailClick;
    @Unique private int nightmareMode$borrowedRailSlot = -1;
    /**
     * Vanilla drops its reference to an integrated server before its thread has
     * necessarily finished. Keep this reference until that thread confirms
     * it has stopped so a subsequent world launch cannot overlap it.
     */
    @Unique private MinecraftServer nightmareMode$serverAwaitingShutdown;

    @Inject(method = "loadWorld(Lnet/minecraft/src/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void nightmareMode$stopIntegratedServerBeforeUnloading(WorldClient world, String message, CallbackInfo ci) {
        if (world != null) {
            return;
        }
        if (this.theIntegratedServer != null) {
            this.nightmareMode$serverAwaitingShutdown = this.theIntegratedServer;
        }
        this.nightmareMode$waitForIntegratedServerShutdown();
    }

    @Inject(method = "launchIntegratedServer", at = @At("HEAD"), cancellable = true)
    private void nightmareMode$preventOverlappingIntegratedServers(String folderName, String worldName,
                                                                     WorldSettings settings, CallbackInfo ci) {
        if (!this.nightmareMode$waitForIntegratedServerShutdown()) {
            // starting a second server after a failed shutdown corrupts the singleton used by login and packet handling.
            // returning to the menu is preferable to starting a server that will inevitably crash.
            // some call it code, I call it a cry for help
            ((Minecraft) (Object) this).displayGuiScreen(new GuiMainMenu());
            ci.cancel();
        }
    }

    /**
     * Vanilla closes the current screen as soon as the integrated server enters
     * its run loop. The client world does not exist until the login packet is
     * processed, so displayGuiScreen(null) immediately substitutes the main
     * menu for a few frames. Keep a non-interactive loading screen in place
     * until NetClientHandler replaces it with GuiDownloadTerrain.
     */
    @Redirect(method = "launchIntegratedServer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Minecraft;displayGuiScreen(Lnet/minecraft/src/GuiScreen;)V"))
    private void nightmareMode$keepLoadingScreenUntilLogin(Minecraft minecraft, GuiScreen ignored) {
        minecraft.displayGuiScreen(new GuiJoiningWorld());
    }

    @Unique
    private boolean nightmareMode$waitForIntegratedServerShutdown() {
        MinecraftServer server = this.nightmareMode$serverAwaitingShutdown;
        if (server == null && this.theIntegratedServer != null) {
            server = this.theIntegratedServer;
            this.nightmareMode$serverAwaitingShutdown = server;
        }
        if (server == null || server.isServerStopped()) {
            this.nightmareMode$serverAwaitingShutdown = null;
            return true;
        }

        server.initiateShutdown();
        long deadline = Minecraft.getSystemTime() + 10000L;
        while (!server.isServerStopped() && Minecraft.getSystemTime() < deadline) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (server.isServerStopped()) {
            this.nightmareMode$serverAwaitingShutdown = null;
            return true;
        }
        return false;
    }

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
