package gtlp.prettyniceores.gui;

import com.google.common.collect.Lists;
import gtlp.prettyniceores.Constants;
import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marv1 on 22.07.2016.
 */

public class NiceConfigGui extends GuiConfig {
    public NiceConfigGui(GuiScreen parent) {
        super(parent, getConfigElements(), Constants.MOD_ID, false, true, Constants.NAME);
    }

    private static List<IConfigElement> getConfigElements() {
        ArrayList<IConfigElement> configElements = Lists.newArrayList();

        configElements.add(new ConfigElement(PrettyNiceOres.getConfig().getCategory(Constants.CATEGORY_ENABLED_BLOCKS)));
        configElements.add(new ConfigElement(PrettyNiceOres.getConfig().getCategory(Constants.CATEGORY_DEBUG)));
        return configElements;
    }

    @Override
    public final void onGuiClosed() {
        super.onGuiClosed();
        PrettyNiceOres.getConfig().save();
    }
}
