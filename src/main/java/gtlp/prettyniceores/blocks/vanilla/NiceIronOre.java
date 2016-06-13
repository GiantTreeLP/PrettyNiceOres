package gtlp.prettyniceores.blocks.vanilla;

import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
public class NiceIronOre extends NiceOreBase implements ISmeltable, IOreDictCompatible, INamedBlock {

    public static final String NAME = "nice_iron_ore";
    public static final String OREDICTTYPE = "oreIron";

    public NiceIronOre() {
        super(NAME);
        setHarvestLevel("pickaxe", Item.ToolMaterial.STONE.getHarvestLevel());
    }

    @Override
    public final String getOreDictType() {
        return OREDICTTYPE;
    }

    @Override
    public final String getName() {
        return NAME;
    }

    @Override
    public final ItemStack getSmeltingResult() {
        return new ItemStack(Items.IRON_INGOT);
    }

    @Override
    public final float getSmeltingExp() {
        return 2f;
    }
}
