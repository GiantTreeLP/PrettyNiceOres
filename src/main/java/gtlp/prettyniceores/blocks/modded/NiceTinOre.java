package gtlp.prettyniceores.blocks.modded;

import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by Marv1 on 09.06.2016.
 */
public class NiceTinOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock {

    public static final String NAME = "nice_tin_ore";
    public static final String OREDICTTYPE = "oreTin";

    public NiceTinOre() {
        super(NAME);
        setHarvestLevel("pickaxe", Item.ToolMaterial.IRON.getHarvestLevel());
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return OreDictionary.getOres("ingotTin").get(0);
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
