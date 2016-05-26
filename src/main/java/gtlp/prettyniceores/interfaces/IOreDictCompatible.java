package gtlp.prettyniceores.interfaces;

/**
 * Created by Marv1 on 23.05.2016 as part of forge-modding-1.9.
 */
public interface IOreDictCompatible {
    /**
     * @return a String defining the name/type used by {@link net.minecraftforge.oredict.OreDictionary}
     */
    String getOreDictType();
}
