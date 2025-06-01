package club.sk1er.mods.scrollabletooltips.mixin;


import club.sk1er.mods.scrollabletooltips.GuiUtilsOverride;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import gg.essential.universal.UMatrixStack;
import gg.essential.universal.UMinecraft;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public class TooltipMixin {
    // Mixin Constants

    @Unique
    private static final String scrollableTooltips$mixinTarget =
        //#if FORGE
        //$$ "renderTooltipInternal";
        //#else
        "renderTooltipFromComponents";
        //#endif
    @Unique
    private static final int scrollableTooltips$tooltipYOrdinal =
        4;

    @Unique
    private static final int scrollableTooltips$tooltipHeightOrdinal =
        //#if FABRIC
        //$$ 6;
        //#else
        5;
        //#endif

    @Unique
    private static final int scrollableTooltips$tooltipXOrdinal =
        //#if FABRIC
        //$$ 3;
        //#else
        3;
        //#endif

    @Unique
    private static final int scrollableTooltips$tooltipWidthOrdinal =
        2;

    // Internal Values
    @Unique
    private final UMatrixStack scrollableTooltips$matrixStack = new UMatrixStack();
    @Unique
    private Slot scrollableTooltips$currentSlot = null;

    @Inject(method = scrollableTooltips$mixinTarget, at = @At("HEAD"))
    private void scrollableTooltips$detectItemChange(CallbackInfo ci) {
        Screen currentScreen = UMinecraft.getMinecraft().currentScreen;
        if (currentScreen instanceof HandledScreen<?>) {
            Slot hoveredSlot = (((AccessorAbstractContainerScreen) currentScreen)).getFocusedSlot();
            if (scrollableTooltips$currentSlot != hoveredSlot) {
                scrollableTooltips$currentSlot = hoveredSlot;
                GuiUtilsOverride.resetScroll();
            }
        }
    }

    @Inject(
        method = scrollableTooltips$mixinTarget,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
            shift = At.Shift.AFTER
        )
    )
    private void scrollableTooltips$pushMatrixAndTranslate(
        MatrixStack arg, List<TooltipComponent> list, int m, int n, CallbackInfo ci,
        @Local(ordinal = scrollableTooltips$tooltipXOrdinal) LocalIntRef tooltipXRef,
        @Local(ordinal = scrollableTooltips$tooltipWidthOrdinal) int tooltipWidthRef,
        @Local(ordinal = scrollableTooltips$tooltipYOrdinal) LocalIntRef tooltipYRef,
        @Local(ordinal = scrollableTooltips$tooltipHeightOrdinal) int tooltipHeightRef
    ) {
        scrollableTooltips$matrixStack.push();
        int tooltipX = tooltipXRef.get();
        tooltipXRef.set(0);
        int tooltipY = tooltipYRef.get();
        tooltipYRef.set(0);

        // Replicate original behavior
        Screen screen = UScreen.getCurrentScreen();
        if (tooltipX + tooltipWidthRef > screen.width) {
            tooltipX -= 28 + tooltipWidthRef;
        }

        if (tooltipY + tooltipHeightRef + 6 > screen.height) {
            tooltipY = screen.height - tooltipHeightRef - 6;
        }

        scrollableTooltips$matrixStack.translate(tooltipX, tooltipY, 0.0);
        GuiUtilsOverride.drawHoveringText(scrollableTooltips$matrixStack, tooltipY, tooltipHeightRef);
        scrollableTooltips$matrixStack.applyToGlobalState();
    }

    @Inject(
        method = scrollableTooltips$mixinTarget,
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V"
        )
    )
    private void scrollableTooltips$popMatrix(MatrixStack arg, List<TooltipComponent> list, int m, int n, CallbackInfo ci) {
        scrollableTooltips$matrixStack.pop();
    }
}
