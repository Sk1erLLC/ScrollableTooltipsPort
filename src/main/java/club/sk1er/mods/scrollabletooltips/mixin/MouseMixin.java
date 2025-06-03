package club.sk1er.mods.scrollabletooltips.mixin;

import club.sk1er.mods.scrollabletooltips.TooltipScroller;
import gg.essential.universal.UMinecraft;
import gg.essential.universal.UScreen;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Unique
    private static final String scrollableTooltips$mouseScrolledTarget =
        //#if MC>=12006
        //#if FORGE || NEOFORGE
        //$$ "Lnet/minecraft/client/gui/screens/Screen;mouseScrolled(DDDD)Z";
        //#else
        //$$ "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDDD)Z";
        //#endif
        //#else
        "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z";
        //#endif

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = scrollableTooltips$mouseScrolledTarget), cancellable = true)
    private void scrollableTooltips$captureScroll(long handle, double xOffset, double yOffset, CallbackInfo ci) {
        Screen currentScreen = UScreen.getCurrentScreen();
        if (UMinecraft.getMinecraft().player == null) return;
        if (currentScreen instanceof HandledScreen<?>) {
            AccessorHandledScreen containerScreen = (AccessorHandledScreen) currentScreen;
            if (containerScreen.getFocusedSlot() != null && containerScreen.getFocusedSlot().hasStack()) {
                if (TooltipScroller.scroll(yOffset)) ci.cancel();
            }
        }
    }
}