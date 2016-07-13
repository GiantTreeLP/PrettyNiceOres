package gtlp.prettyniceores.interfaces;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public interface ISmeltable {
    /**
     * @return an {@link net.minecraft.item.ItemStack) containing the result of the smelting procedure
     */
    @Nullable
    ItemStack getSmeltingResult();

    /**
     * @return amount of experience dropped after smelting the item/block
     */
    default float getSmeltingExp() {
        return 0f;
    }
}
