package gtlp.prettyniceores.blocks.modded;

import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOre;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public class NiceMercuryOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock, IOre {

    public static final String NAME = "nice_mercury_ore";
    public static final String OREDICTTYPE = "oreMercury";

    public NiceMercuryOre() {
        super(NAME);
        setHarvestLevel("pickaxe", Item.ToolMaterial.STONE.getHarvestLevel());
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return OreDictionary.getOres("ingotMercury").get(0);
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
