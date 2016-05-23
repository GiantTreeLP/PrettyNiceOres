package gtlp.prettyniceores.interfaces;

import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public interface ISmeltable {
    ItemStack getSmeltingResult();

    float getSmeltingExp();
}
