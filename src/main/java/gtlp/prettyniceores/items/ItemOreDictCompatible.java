package gtlp.prettyniceores.items;

import gtlp.prettyniceores.Constants;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Marv1 on 24.05.2016.
 */
public class ItemOreDictCompatible extends Item implements IOreDictCompatible {

    private final String oreDictType;

    /**
     * @param oreDictName name used to register in {@link net.minecraftforge.oredict.OreDictionary}, {@link ItemOreDictCompatible#getOreDictType}
     */
    public ItemOreDictCompatible(String oreDictName) {
        super();
        this.oreDictType = oreDictName;
        setRegistryName(Constants.MOD_ID, oreDictType);
        setUnlocalizedName(oreDictType);
        setCreativeTab(CreativeTabs.MATERIALS);
    }

    /**
     * @return a String defining the name used by {@link net.minecraftforge.oredict.OreDictionary}
     */
    @Override
    public final String getOreDictType() {
        return oreDictType;
    }
}
