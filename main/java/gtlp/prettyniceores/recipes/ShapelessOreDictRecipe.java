package gtlp.prettyniceores.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/**
 * Created by Marv1 on 24.05.2016.
 */
public class ShapelessOreDictRecipe extends ShapelessOreRecipe {

    public ShapelessOreDictRecipe(String outputType, String inputType) {
        super(OreDictionary.getOres(outputType).get(0), inputType);
    }

    public ShapelessOreDictRecipe(String outputType, String inputType, int amount) {
        super(new ItemStack(OreDictionary.getOres(outputType).get(0).getItem(), amount), inputType);
    }
}
