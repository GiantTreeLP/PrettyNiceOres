package gtlp.prettyniceores;

import gtlp.prettyniceores.blocks.*;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.recipes.ShapelessOreDictRecipe;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
@Mod(modid = PrettyNiceOres.MOD_ID, version = PrettyNiceOres.VERSION, canBeDeactivated = true, dependencies = "after:neotech@[1.9-3.0.6,);after:tconstruct@[1.9-2.3.1,)", acceptedMinecraftVersions = "[1.9,)")
public class PrettyNiceOres {
    public static final String MOD_ID = "prettyniceores";
    public static final String VERSION = "1.9-0.3.4";
    public static final Map<String, Block> blockList = new HashMap<>();
    public static final Map<String, Item> itemList = new HashMap<>();
    public static final Map<String, ItemBlock> itemBlockList = new HashMap<>();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    final static Block[] modBlocks = {new NiceCopperOre()};
    public List<IRecipe> recipeList = new ArrayList<>();

    /**
     * Preinitialization of the mod.
     *
     * @param event the preinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        RecipeSorter.register(MOD_ID + ":shapelessoredict", ShapelessOreDictRecipe.class, SHAPELESS, "after:minecraft:shapeless");

        addVanillaOres();
        addModOres();

        addModItems();

        blockList.forEach((name, block) -> {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            itemBlockList.put(block instanceof INamedBlock ? ((INamedBlock) block).getName() : block.getRegistryName().getResourcePath(), itemBlock);
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

    /**
     * Adds all replacements for the basic vanilla ores
     */
    private void addVanillaOres() {
        blockList.put(NiceIronOre.NAME, new NiceIronOre());
        blockList.put(NiceGoldOre.NAME, new NiceGoldOre());
        blockList.put(NiceCoalOre.NAME, new NiceCoalOre());
        blockList.put(NiceRedstoneOre.NAME, new NiceRedstoneOre());
        blockList.put(NiceLapisOre.NAME, new NiceLapisOre());
        blockList.put(NiceDiamondOre.NAME, new NiceDiamondOre());
        blockList.put(NiceEmeraldOre.NAME, new NiceEmeraldOre());
        blockList.put(NiceNetherQuartzOre.NAME, new NiceNetherQuartzOre());
    }

    /**
     * Adds all replacements for mod ores, if they have been created by any other mod.
     */
    private void addModOres() {
        for (Block block : modBlocks) {
            if (block instanceof IOreDictCompatible && block instanceof INamedBlock) {
                if (OreDictionary.doesOreNameExist(((IOreDictCompatible) block).getOreDictType())) {
                    blockList.put(((INamedBlock) block).getName(), block);
                }
            }
        }
    }

    private void addModItems() {
    }

    /**
     * Initialization of the mod.
     *
     * @param event the init event sent by Forge, used to determine whether or not to register renderers
     */
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

    /**
     * Registers a default item renderer for the inventory
     *
     * @param item to register the item renderer for
     */
    @SideOnly(Side.CLIENT)
    public static void registerItemRenderer(Item item) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    /**
     * Postinitialization of the mod.
     *
     * @param event the postinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), Integer.MAX_VALUE);
        LOGGER.info("PostInit done.");
    }

}
