package gtlp.prettyniceores.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Marv1 on 24.05.2016.
 */
public class ShapelessOreDictRecipe extends ShapelessOreRecipe {

    /**
     * Simple ore dict-aware recipe with output amount of 1
     *
     * @param outputType ore dict name of the output item
     * @param inputType  ore dict name of the input item/itemblock/block
     */
    public ShapelessOreDictRecipe(String outputType, String inputType) {
        super(OreDictionary.getOres(outputType).get(0), inputType);
    }

    /**
     * Simple ore dict-aware recipe with variable output amount
     *
     * @param outputType ore dict name of the output item
     * @param inputType  ore dict name of the input item/itemblock/block
     * @param amount output amount
     */
    public ShapelessOreDictRecipe(String outputType, String inputType, int amount) {
        super(new ItemStack(OreDictionary.getOres(outputType).get(0).getItem(), amount), inputType);
    }
}
