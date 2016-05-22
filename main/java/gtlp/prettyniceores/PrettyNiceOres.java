package gtlp.prettyniceores;

import gtlp.prettyniceores.blocks.NiceGoldOre;
import gtlp.prettyniceores.blocks.NiceIronOre;
import gtlp.prettyniceores.blocks.NiceOreBase;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
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
    public static final String VERSION = "v0.1";

    public static final Map<String, Block> blockList = new HashMap<>();
    public static final Map<String, Item> itemList = new HashMap<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        blockList.put(NiceIronOre.NAME, new NiceIronOre());
        blockList.put(NiceGoldOre.NAME, new NiceGoldOre());
        blockList.forEach((name, block) -> {
            GameRegistry.register(block);
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            itemList.put(itemBlock.getRegistryName().toString(), itemBlock);
            registerItemRenderer(itemBlock);
        });
        itemList.forEach((name, item) -> {
            GameRegistry.register(item);
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof NiceOreBase) {
                OreDictionary.registerOre(((NiceOreBase) ((ItemBlock) item).getBlock()).getOreDictType(), item);
                if (((NiceOreBase) ((ItemBlock) item).getBlock()).isSmeltable()) {
                    GameRegistry.addSmelting(item, ((NiceOreBase) ((ItemBlock) item).getBlock()).getSmeltingResult(), 1f);
                }
            }
        });
    }

    public static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), 1);
    }

}
