package gtlp.prettyniceores.util;

import gtlp.prettyniceores.PrettyNiceOres;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Created by Marv1 on 07.07.2016.
 */
public final class OreDictUtils {
    private OreDictUtils() {
    }

    public static ItemStack getFirstOre(String name) {
        List<ItemStack> oreList = OreDictionary.getOres(name);
        if (oreList.size() == 0) {
            PrettyNiceOres.LOGGER.warn("Can't find ore for name '" + name + "'");
            return null;
        }
        return oreList.get(0);
    }
}
