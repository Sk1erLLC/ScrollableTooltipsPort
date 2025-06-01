package club.sk1er.mods.scrollabletooltips.mixin;

import club.sk1er.mods.scrollabletooltips.GuiUtilsOverride;
import gg.essential.universal.UMinecraft;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if FORGE
//$$ import net.minecraft.client.gui.screens.inventory.MenuAccess;
//#else
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
//#endif

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"), cancellable = true)
    private void scrollableTooltips$captureScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        Screen currentScreen = UMinecraft.getMinecraft().currentScreen;
        if (UMinecraft.getMinecraft().player == null) return;
        if (currentScreen instanceof InventoryScreen) {
            AccessorAbstractContainerScreen containerScreen = (AccessorAbstractContainerScreen) currentScreen;
            //#if FORGE
            //$$ ItemStack carriedItem = ((MenuAccess<?>) currentScreen).getMenu().getCarried();
            //#else
            ItemStack carriedItem = ((ScreenHandlerProvider<?>) currentScreen).getScreenHandler().getCursorStack();
            //#endif
            if (carriedItem != null &&
                    !carriedItem.isEmpty() &&
                    containerScreen.getFocusedSlot() != null &&
                    containerScreen.getFocusedSlot().hasStack()) {
                if (GuiUtilsOverride.scroll(yOffset)) ci.cancel();
            }
        }
    }
}