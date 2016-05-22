package gtlp.prettyniceores.blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceGoldOre extends NiceOreBase {

    public static final String NAME = "nice_gold_ore";
    private static final String OREDICTTYPE = "oreGold";

    public NiceGoldOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public ItemStack getSmeltingResult() {
        return new ItemStack(Item.getByNameOrId("gold_ingot"));
    }

    @Override
    public String getOreDictType() {
        return OREDICTTYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
