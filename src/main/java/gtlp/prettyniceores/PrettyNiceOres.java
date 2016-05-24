package gtlp.prettyniceores;

import gtlp.prettyniceores.blocks.*;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.items.ItemOreDictCompatible;
import gtlp.prettyniceores.recipes.ShapelessOreDictRecipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
@Mod(modid = PrettyNiceOres.MOD_ID, version = PrettyNiceOres.VERSION, canBeDeactivated = true, dependencies = "after:neotech@[1.9-3.0.6,)")
public class PrettyNiceOres {
    public static final String MOD_ID = "prettyniceores";
    public static final String VERSION = "1.9-0.0.2.1";
    public static final Map<String, Block> blockList = new HashMap<>();
    public static final Map<String, Item> itemList = new HashMap<>();
    public static final Map<String, ItemBlock> itemBlockList = new HashMap<>();
    public static Logger LOGGER;
    public List<IRecipe> recipeList = new ArrayList<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();

        RecipeSorter.register(MOD_ID + ":shapelessoredict", ShapelessOreDictRecipe.class, SHAPELESS, "after:minecraft:shapeless");

        addVanillaOres();
        addNeoTechOres();

        addNeoTechItems();
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
        recipeList.forEach(GameRegistry::addRecipe);
        LOGGER.info("PreInit done.");
    }

    private void addVanillaOres() {
        blockList.put(NiceIronOre.NAME, new NiceIronOre());
        blockList.put(NiceGoldOre.NAME, new NiceGoldOre());
        blockList.put(NiceCoalOre.NAME, new NiceCoalOre());
        blockList.put(NiceRedstoneOre.NAME, new NiceRedstoneOre());
        blockList.put(NiceLapisOre.NAME, new NiceLapisOre());
        blockList.put(NiceDiamondOre.NAME, new NiceDiamondOre());
        blockList.put(NiceEmeraldOre.NAME, new NiceEmeraldOre());
    }

    private void addNeoTechOres() {
        if (Loader.isModLoaded("neotech")) {
            blockList.putIfAbsent(NiceCopperOre.NAME, new NiceCopperOre());
        }
    }

    private void addNeoTechItems() {
        if (Loader.isModLoaded("neotech")) {
            itemList.putIfAbsent("ingotCopper", new ItemOreDictCompatible("ingotCopper"));
            recipeList.add(new ShapelessOreDictRecipe("nuggetCopper", "ingotCopper", 9));
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        itemList.forEach((name, item) -> {
            if (event.getSide().isClient()) {
                registerItemRenderer(item);
            }
        });
        itemBlockList.forEach((name, item) -> {
            if (event.getSide().isClient()) {
                registerItemRenderer(item);
            }
        });
        blockList.entrySet().stream().filter(entry -> entry.getValue() instanceof IOreDictCompatible).forEach(entry -> {
            IOreDictCompatible block = (IOreDictCompatible) entry.getValue();
            GameRegistry.addRecipe(new ShapelessOreRecipe(OreDictionary.getOres(block.getOreDictType()).get(0), block));
        });
        LOGGER.info("Init done.");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), Integer.MAX_VALUE);
        LOGGER.info("PostInit done.");
    }

}
