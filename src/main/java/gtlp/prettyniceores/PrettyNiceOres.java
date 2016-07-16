package gtlp.prettyniceores;

import gtlp.prettyniceores.blocks.modded.*;
import gtlp.prettyniceores.blocks.vanilla.*;
import gtlp.prettyniceores.common.CommonProxy;
import gtlp.prettyniceores.events.OnPlayerLoginEvent;
import gtlp.prettyniceores.generators.NiceOresGenerator;
import gtlp.prettyniceores.interfaces.INamedBlock;
import gtlp.prettyniceores.interfaces.IOreDictCompatible;
import gtlp.prettyniceores.interfaces.ISmeltable;
import gtlp.prettyniceores.items.DebugAndTestingItem;
import gtlp.prettyniceores.recipes.ShapelessOreDictRecipe;
import gtlp.prettyniceores.util.OreDictUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by Marv1 on 22.05.2016 as part of forge-modding-1.9.
 */
@Mod(modid = Constants.MOD_ID,
        version = Constants.VERSION,
        name = Constants.NAME,
        updateJSON = Constants.UPDATE_URL,
        dependencies = Constants.DEPENDENCIES,
        acceptedMinecraftVersions = Constants.MC_VERSION)

public class PrettyNiceOres {
    public static final Logger LOGGER = LogManager.getLogger(Constants.MOD_ID);

    public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(Constants.MOD_ID) {
        @SuppressWarnings("ConstantConditions")
        @SideOnly(Side.CLIENT)
        @Override
        @Nonnull
        public Item getTabIconItem() {
            return ItemBlock.getItemFromBlock(Blocks.DIAMOND_ORE);
        }
    };

    private static final Map<String, Block> blockList = new HashMap<>();
    private static final Map<String, Item> itemList = new HashMap<>();
    private static final Map<String, ItemBlock> itemBlockList = new HashMap<>();
    private static final List<IRecipe> recipeList = new ArrayList<>();

    @SidedProxy(clientSide = "gtlp.prettyniceores.client.ClientProxy", serverSide = "gtlp.prettyniceores.common.CommonProxy")
    public static CommonProxy proxy;


    public static Map<String, Block> getBlockList() {
        return blockList;
    }

    /**
     * Registers a default item renderer for the inventory
     *
     * @param item to register the item renderer for
     */
    @SideOnly(Side.CLIENT)
    private static void registerItemRenderer(Item item, int meta) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    /**
     * Adds a smelting recipe for the given {@link Item}, if the item is an {@link ISmeltable} (takes all items, though)
     *
     * @param item to register the smelting recipe for
     */
    private static void addSmeltingRecipe(Item item) {
        if (item instanceof ISmeltable) {
            ItemStack result = ((ISmeltable) item).getSmeltingResult();
            if (result != null) {
                GameRegistry.addSmelting(item, result, ((ISmeltable) item).getSmeltingExp());
            }
        }
    }

    /**
     * Adds a smelting recipe for the given {@link Block}, if the item is an {@link ISmeltable} (takes all blocks, though)
     *
     * @param block to register the smelting recipe for
     */
    private static void addSmeltingRecipe(Block block) {
        if (block instanceof ISmeltable) {
            ItemStack result = ((ISmeltable) block).getSmeltingResult();
            if (result != null) {
                GameRegistry.addSmelting(block, result, ((ISmeltable) block).getSmeltingExp());
            }
        }
    }

    /**
     * Preinitialization of the mod.
     *
     * @param event the preinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        RecipeSorter.register(Constants.MOD_ID + ":shapelessoredict", ShapelessOreDictRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        addVanillaOres();
        addModOres();

        addItems();

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
            addSmeltingRecipe(block);
        });
        itemList.forEach((name, item) -> {
            GameRegistry.register(item);
            if (item instanceof IOreDictCompatible) {
                OreDictionary.registerOre(((IOreDictCompatible) item).getOreDictType(), item);
            }
            addSmeltingRecipe(item);
        });
        MinecraftForge.EVENT_BUS.register(new OnPlayerLoginEvent());
        LOGGER.info("PreInit done.");
    }

    /**
     * Adds items.
     */
    private void addItems() {
        String debugString = System.getenv("pno_debug");
        if (debugString != null && debugString.equals("true")) {
            itemList.put(DebugAndTestingItem.NAME, new DebugAndTestingItem());
        }
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
        Block[] blockArray = {
                new NiceCopperOre(),
                new NiceTinOre(),
                new NiceSilverOre(),
                new NiceLeadOre(),
                new NiceNickelOre(),
                new NicePlatinumOre(),
                new NiceZincOre(),
                new NiceMercuryOre(),
        };

        Stream.of(blockArray).filter(block -> block instanceof IOreDictCompatible && block instanceof INamedBlock)
                .filter(block -> OreDictionary.doesOreNameExist(((IOreDictCompatible) block).getOreDictType()))
                .forEach(block -> {
                    blockList.put(((INamedBlock) block).getName(), block);
                });
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
            }
        });
        itemBlockList.forEach((name, item) -> {
            if (event.getSide().isClient()) {
                registerItemRenderer(item, 0);
            }
        });
        blockList.entrySet().stream().filter(entry -> entry.getValue() instanceof IOreDictCompatible).forEach(entry -> {
            IOreDictCompatible block = (IOreDictCompatible) entry.getValue();
            ItemStack result = OreDictUtils.getFirstOre(block.getOreDictType());
            if (result != null) {
                GameRegistry.addRecipe(new ShapelessOreRecipe(result, block));
            }
        });
        recipeList.forEach(GameRegistry::addRecipe);

        recipeList.forEach(GameRegistry::addRecipe);
        LOGGER.info("Init done.");
    }

    /**
     * Postinitialization of the mod.
     *
     * @param event the postinit event sent by Forge, unused.
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new NiceOresGenerator(), Short.MAX_VALUE);
        LOGGER.info("PostInit done.");
    }
}
