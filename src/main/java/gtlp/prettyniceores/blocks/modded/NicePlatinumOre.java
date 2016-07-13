package gtlp.prettyniceores.blocks.modded;

import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOre;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.util.OreDictUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 09.06.2016.
 */
public class NicePlatinumOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock, IOre {

    public static final String NAME = "nice_platinum_ore";
    public static final String OREDICTTYPE = "orePlatinum";

    public NicePlatinumOre() {
        super(NAME);
        setHarvestLevel("pickaxe", Item.ToolMaterial.IRON.getHarvestLevel());
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return OreDictUtils.getFirstOre("ingotPlatinum");
    }

    @Override
    public final float getSmeltingExp() {
        return 3f;
    }

    @Override
    public final String getOreDictType() {
        return OREDICTTYPE;
    }

    public final String getName() {
        return NAME;
    }
}
