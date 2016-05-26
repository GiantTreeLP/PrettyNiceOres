package gtlp.prettyniceores.blocks;

import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceCopperOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock {

    public static final String NAME = "nice_copper_ore";
    public static final String OREDICTTYPE = "oreCopper";

    public NiceCopperOre() {
        super(NAME);
        setHarvestLevel("pickaxe", 1);
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return OreDictionary.getOres("ingotCopper").get(0);
    }

    @Override
    public final float getSmeltingExp() {
        return 2f;
    }

    @Override
    public final String getOreDictType() {
        return OREDICTTYPE;
    }

    public final String getName() {
        return NAME;
    }
}
