//#if FABRIC
package club.sk1er.mods.scrollabletooltips;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> Config.INSTANCE.gui();
    }
}
//#endif