package club.sk1er.mods.scrollabletooltips.mixin;

import club.sk1er.mods.scrollabletooltips.TooltipScroller;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 1.20+ moves tooltip rendering into DrawContext, so this is separated so we can still reset the tooltip
// when the screen is closed since DrawContext lacks a close method.
@Mixin(Screen.class)
public class ScreenMixin_ResetTooltipScroll {
    @Inject(method = "close", at = @At("HEAD"))
    private void scrollableTooltips$resetTooltipScroll(CallbackInfo ci) {
        TooltipScroller.resetScroll();
    }
}