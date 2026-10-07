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
    // Tooltip padding constants (border + internal padding)
    private static final int TOOLTIP_PADDING_HORIZONTAL = 4; // Left/right padding
    private static final int TOOLTIP_PADDING_VERTICAL = 1;   // Top/bottom padding
    private static final int SCREEN_MARGIN = 6; // Screen edge margin

    public static boolean needsReset;
    public static boolean allowScrolling;
    public static boolean tooltipNeedsScrolling; // Track if tooltip is larger than screen
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
            int tooltipX, int tooltipY, int tooltipWidth, int tooltipHeight) {
        Screen screen = UScreen.getCurrentScreen();
        assert screen != null;

        int scaledTooltipHeight = (int) (tooltipHeight * zoomFactor);
        int scaledTooltipWidth = (int) (tooltipWidth * zoomFactor);

        if (needsReset) {
            scrollX = 0;
            // Always allow scrolling when master toggle is enabled
            allowScrolling = true;

            // Determine if the tooltip is larger than the screen or goes off-screen
            // Account for padding on both top and bottom
            int totalPadding = 2 * (TOOLTIP_PADDING_VERTICAL + SCREEN_MARGIN);
            tooltipNeedsScrolling =
                    (Math.max(scaledTooltipHeight, tooltipHeight) + totalPadding) > screen.height ||
                    Math.abs(tooltipY) + Math.max(scaledTooltipHeight, tooltipHeight) > screen.height;

            // Only apply startAtTop positioning if the tooltip needs scrolling
            if (Config.startAtTop && tooltipNeedsScrolling) {
                scrollY = SCREEN_MARGIN - tooltipY;
            } else {
                scrollY = 0;
            }
            zoomFactor = 1.0f;
            needsReset = false;
        }

        if (!Config.masterToggle) return;

        if (allowScrolling) {
            // Vertical bounds checking - use screen margin only
            int maxY = SCREEN_MARGIN - tooltipY;
            int minY = screen.height - tooltipY - scaledTooltipHeight - SCREEN_MARGIN;

            if (minY <= maxY) {
                // Tooltip is larger than screen - clamp to keep some part visible
                if (scrollY > maxY) {
                    scrollY = maxY;
                } else if (scrollY < minY) {
                    scrollY = minY;
                }
            } else {
                // maxY is the minimum scrollY, minY is the maximum scrollY
                if (scrollY < maxY) {
                    scrollY = maxY;
                }
                if (scrollY > minY) {
                    scrollY = minY;
                }
            }

            // Horizontal bounds checking - account for tooltip padding
            int maxX = TOOLTIP_PADDING_HORIZONTAL - tooltipX;
            int minX = screen.width - tooltipX - scaledTooltipWidth - TOOLTIP_PADDING_HORIZONTAL;

            if (minX <= maxX) {
                // Tooltip is wider than screen - clamp to keep some part visible
                if (scrollX > maxX) {
                    scrollX = maxX;
                } else if (scrollX < minX) {
                    scrollX = minX;
                }
            } else {
                // Tooltip is narrower than screen - bounds are inverted
                // maxX is the minimum scrollX, minX is the maximum scrollX
                if (scrollX < maxX) {
                    scrollX = maxX;
                }
                if (scrollX > minX) {
                    scrollX = minX;
                }
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
        // Zoom always works regardless of tooltip size
        if (UKeyboard.isCtrlKeyDown() && Config.zoom) {
            zoomFactor *= (float) (1.0 + 0.1 * Math.signum(delta));
            return true;
        }

        if (allowScrolling) {
            // For small tooltips, check if scrolling is enabled and Alt is pressed
            if (!tooltipNeedsScrolling) {
                // Config disabled: don't allow scrolling small tooltips at all
                if (!Config.enableScrollingSmallTooltips) {
                    return false;
                }
                // Config enabled: require Alt key to scroll small tooltips
                if (!UKeyboard.isAltKeyDown()) {
                    return false;
                }
            }

            // Perform horizontal or vertical scrolling
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
