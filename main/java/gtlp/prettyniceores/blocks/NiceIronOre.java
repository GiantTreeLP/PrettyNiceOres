package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public class NiceIronOre extends NiceOreBase implements ISmeltable, IOreDictCompatible {

    public static final String NAME = "nice_iron_ore";
    private static final String OREDICTTYPE = "oreIron";

    public NiceIronOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public String getOreDictType() {
        return OREDICTTYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ItemStack getSmeltingResult() {
        return new ItemStack(Item.getByNameOrId("iron_ingot"));
    }

    @Override
    public float getSmeltingExp() {
        return 2f;
    }
}
