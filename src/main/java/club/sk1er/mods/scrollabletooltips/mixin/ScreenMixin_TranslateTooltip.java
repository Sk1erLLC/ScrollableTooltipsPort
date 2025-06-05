package club.sk1er.mods.scrollabletooltips.mixin;


import club.sk1er.mods.scrollabletooltips.TooltipScroller;
import club.sk1er.mods.scrollabletooltips.mixin.accessors.AccessorHandledScreen;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

//#if FORGE || NEOFORGE
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif

//#if MC>=12006
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Final;
//#if FABRIC
//$$ import net.minecraft.client.gui.DrawContext;
//$$ @Mixin(DrawContext.class)
//#else
//$$ import net.minecraft.client.gui.GuiGraphics;
//$$ @Mixin(GuiGraphics.class)
//#endif
//#else
@Mixin(Screen.class)
//#endif
public class ScreenMixin_TranslateTooltip {
    // Mixin Constants

    @Unique
    private static final String scrollableTooltips$mixinTarget =
        //#if FORGE || NEOFORGE
        //$$ "renderTooltipInternal";
        //#elseif MC==12006
        //$$ "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;)V";
        //#elseif MC>=12105
        //$$ "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;)V";
        //#else
        "renderTooltipFromComponents";
        //#endif
    @Unique
    private static final int scrollableTooltips$tooltipYOrdinal =
        //#if MC>=12006
        //$$ 7;
        //#else
        5;
        //#endif

    @Unique
    private static final int scrollableTooltips$tooltipHeightOrdinal = 3;

    @Unique
    private static final int scrollableTooltips$tooltipXOrdinal =
        //#if MC>=12006
        //$$ 6;
        //#else
        4;
        //#endif

    @Unique
    private static final int scrollableTooltips$tooltipWidthOrdinal = 2;

    //#if MC>=12006
    //#if FORGE || NEOFORGE
    //$$ @Shadow(aliases = "pose") @Final
    //$$ private PoseStack matrices;
    //#else
    //$$ @Shadow @Final
    //$$ private MatrixStack matrices;
    //#endif
    //#endif

    @Inject(method = scrollableTooltips$mixinTarget, at = @At("HEAD"))
    private void scrollableTooltips$detectItemChange(CallbackInfo ci) {
        Screen currentScreen = UScreen.getCurrentScreen();
        if (currentScreen instanceof HandledScreen<?>) {
            Slot hoveredSlot = ((AccessorHandledScreen) currentScreen).getFocusedSlot();
            if (TooltipScroller.currentSlot != hoveredSlot) {
                TooltipScroller.currentSlot = hoveredSlot;
                TooltipScroller.resetScroll();
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
        //#if MC==11902
        MatrixStack matrices,
        List<TooltipComponent> components, int x, int y,
        //#endif
        CallbackInfo ci,
        @Local(ordinal = scrollableTooltips$tooltipXOrdinal) LocalIntRef tooltipXRef,
        @Local(ordinal = scrollableTooltips$tooltipWidthOrdinal) int tooltipWidthRef,
        @Local(ordinal = scrollableTooltips$tooltipYOrdinal) LocalIntRef tooltipYRef,
        @Local(ordinal = scrollableTooltips$tooltipHeightOrdinal) int tooltipHeightRef
    ) {
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

        matrices.translate(tooltipX, tooltipY, 0.0);
        TooltipScroller.translateTooltip(matrices, tooltipY, tooltipHeightRef);
    }
}
