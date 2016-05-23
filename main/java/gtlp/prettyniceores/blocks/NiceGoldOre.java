package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceGoldOre extends NiceOreBase implements ISmeltable, IOreDictCompatible {

    public static final String NAME = "nice_gold_ore";
    private static final String OREDICTTYPE = "oreGold";

    public NiceGoldOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 2);
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return new ItemStack(Item.getByNameOrId("gold_ingot"));
    }

    @Override
    public final float getSmeltingExp() {
        return 2f;
    }

    @Override
    public final String getOreDictType() {
        return OREDICTTYPE;
    }

    @Override
    public final String getName() {
        return NAME;
    }
}
