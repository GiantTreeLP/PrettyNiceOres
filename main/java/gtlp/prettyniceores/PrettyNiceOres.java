package gtlp.prettyniceores;

import gtlp.prettyniceores.blocks.NiceGoldOre;
import gtlp.prettyniceores.blocks.NiceIronOre;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
@Mod(modid = PrettyNiceOres.MODID, version = PrettyNiceOres.VERSION)
public class PrettyNiceOres {
    public static final String MODID = "prettyniceores";
    public static final String VERSION = "0.2";

    public static final Map<String, Block> blockList = new HashMap<>();
    public static final Map<String, Item> itemList = new HashMap<>();
    public static final Map<String, ItemBlock> itemBlockList = new HashMap<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        blockList.put(NiceIronOre.NAME, new NiceIronOre());
        blockList.put(NiceGoldOre.NAME, new NiceGoldOre());
        blockList.forEach((name, block) -> {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            itemBlockList.put(((INamedBlock) block).getName(), itemBlock);
            GameRegistry.register(block);
        });
        itemBlockList.forEach((name, item) -> {
            GameRegistry.register(item);
            Block block = item.getBlock();
            if (block instanceof IOreDictCompatible) {
                OreDictionary.registerOre(((IOreDictCompatible) block).getOreDictType(), item);
            }
            if (block instanceof ISmeltable) {
                GameRegistry.addSmelting(item, ((ISmeltable) block).getSmeltingResult(), (((ISmeltable) block).getSmeltingExp()));
            }
        });
        itemList.forEach((name, item) -> {
            GameRegistry.register(item);
            if (item instanceof IOreDictCompatible) {
                OreDictionary.registerOre(((IOreDictCompatible) item).getOreDictType(), item);
            }
            if (item instanceof ISmeltable) {
                GameRegistry.addSmelting(item, ((ISmeltable) item).getSmeltingResult(), ((ISmeltable) item).getSmeltingExp());
            }
        });

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        itemList.forEach((name, item) -> registerItemRenderer(item));
        itemBlockList.forEach((name, item) -> registerItemRenderer(item));
    }

    public static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), Integer.MAX_VALUE);
    }

}
