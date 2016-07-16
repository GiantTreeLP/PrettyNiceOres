package gtlp.prettyniceores.blocks.modded;

import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.INiceOre;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.util.OreDictUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 09.06.2016.
 */
public class NiceTinNiceOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock, INiceOre {

    public static final String NAME = "nice_tin_ore";
    public static final String OREDICTTYPE = "oreTin";

    public NiceTinNiceOre() {
        super(NAME);
        setHarvestLevel("pickaxe", Item.ToolMaterial.IRON.getHarvestLevel());
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return OreDictUtils.getFirstOre("ingotTin");
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
