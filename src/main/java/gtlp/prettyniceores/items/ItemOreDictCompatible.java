package gtlp.prettyniceores.items;

import gtlp.prettyniceores.PrettyNiceOres;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Marv1 on 24.05.2016.
 */
public class ItemOreDictCompatible extends Item implements IOreDictCompatible {

    private String oreDictType;

    public ItemOreDictCompatible(String oreDictType) {
        super();
        setRegistryName(PrettyNiceOres.MOD_ID, oreDictType);
        setUnlocalizedName(oreDictType);
        setCreativeTab(CreativeTabs.tabMaterials);
        this.oreDictType = oreDictType;
    }

    @Override
    public String getOreDictType() {
        return oreDictType;
    }
}
