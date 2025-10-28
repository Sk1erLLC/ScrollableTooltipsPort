package club.sk1er.mods.scrollabletooltips;

import gg.essential.universal.UKeyboard;
import gg.essential.universal.UScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.slot.Slot;

//#if MC>=12106
//$$ import org.joml.Matrix3x2fStack;
//#else
import net.minecraft.client.util.math.MatrixStack;
//#endif


public class TooltipScroller {
    public static boolean needsReset;
    public static boolean allowScrolling;
    public static double scrollX = 0;
    public static double scrollY = 0;
    public static float zoomFactor = 1.0f;

    public static Slot currentSlot;

    public static void translateTooltip(
            //#if MC>=12106
            //$$ Matrix3x2fStack matrixStack,
            //#else
            MatrixStack matrixStack,
            //#endif
            int tooltipY, int tooltipHeight) {
        Screen screen = UScreen.getCurrentScreen();
        assert screen != null;

        if (needsReset) {
            scrollX = 0;
            allowScrolling =
                    // Check if the tooltip is larger than the screen
                    (tooltipHeight + 12) > screen.height ||
                    // Also check if the tooltip goes off-screen
                    Math.abs(tooltipY) + tooltipHeight > screen.height;
            if (allowScrolling && Config.startAtTop) {
                scrollY = 6 - tooltipY;
            } else {
                scrollY = 0;
            }
            zoomFactor = 1.0f;
            needsReset = false;
        }

        if (!Config.masterToggle) return;

        if (allowScrolling) {
            int max = 6 - tooltipY;
            int min = screen.height - tooltipY - tooltipHeight - 6;
            if (scrollY > max) {
                scrollY = max;
            } else if (scrollY < min) {
                scrollY = min;
            }
        }

        //#if MC>=12106
        //$$ matrixStack.translate((float) scrollX, (float) scrollY);
        //$$ matrixStack.scale(zoomFactor, zoomFactor);
        //#else
        matrixStack.translate(scrollX, scrollY, 0);
        matrixStack.scale(zoomFactor, zoomFactor, 1.0f);
        //#endif
    }

    public static void resetScroll() {
        needsReset = true;
        allowScrolling = false;
    }

    public static boolean scroll(double delta) {
        if (UKeyboard.isCtrlKeyDown() && Config.zoom) {
            zoomFactor *= (float) (1.0 + 0.1 * Math.signum(delta));
            return true;
        }
        if (allowScrolling) {
             if (UKeyboard.isShiftKeyDown() && Config.horizontalScrolling) {
                scrollX += 10 * Math.signum(delta);
                return true;
            } else if (Config.verticalScrolling) {
                scrollY += 10 * Math.signum(delta);
                return true;
            }
        }
        return false;
    }
}
