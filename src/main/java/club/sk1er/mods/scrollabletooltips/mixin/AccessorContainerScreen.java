package club.sk1er.mods.scrollabletooltips.mixin;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface AccessorContainerScreen {
    @Accessor
    Slot getHoveredSlot();
}
