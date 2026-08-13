package com.createtobacco.registry;

import com.createtobacco.CreateTobacco;
import java.util.List;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateTobacco.MOD_ID);

    public static final DeferredItem<Item> VIRGINIA_SEEDS = ITEMS.registerItem(
            "virginia_seeds", properties -> new ItemNameBlockItem(ModBlocks.VIRGINIA_TOBACCO.get(), properties));
    public static final DeferredItem<Item> BURLEY_SEEDS = ITEMS.registerItem(
            "burley_seeds", properties -> new ItemNameBlockItem(ModBlocks.BURLEY_TOBACCO.get(), properties));
    public static final DeferredItem<Item> HAVANA_SEEDS = ITEMS.registerItem(
            "havana_seeds", properties -> new ItemNameBlockItem(ModBlocks.HAVANA_TOBACCO.get(), properties));

    public static final DeferredItem<Item> FRESH_VIRGINIA_LEAF = ITEMS.registerSimpleItem("fresh_virginia_leaf");
    public static final DeferredItem<Item> FRESH_BURLEY_LEAF = ITEMS.registerSimpleItem("fresh_burley_leaf");
    public static final DeferredItem<Item> FRESH_HAVANA_LEAF = ITEMS.registerSimpleItem("fresh_havana_leaf");

    public static final DeferredItem<Item> CURED_VIRGINIA_LEAF = ITEMS.registerSimpleItem("cured_virginia_leaf");
    public static final DeferredItem<Item> CURED_BURLEY_LEAF = ITEMS.registerSimpleItem("cured_burley_leaf");
    public static final DeferredItem<Item> CURED_HAVANA_LEAF = ITEMS.registerSimpleItem("cured_havana_leaf");

    public static final DeferredItem<Item> CUT_VIRGINIA_TOBACCO = ITEMS.registerSimpleItem("cut_virginia_tobacco");
    public static final DeferredItem<Item> CUT_BURLEY_TOBACCO = ITEMS.registerSimpleItem("cut_burley_tobacco");
    public static final DeferredItem<Item> CUT_HAVANA_TOBACCO = ITEMS.registerSimpleItem("cut_havana_tobacco");

    public static final DeferredItem<Item> CIGARETTE_PAPER = ITEMS.registerSimpleItem("cigarette_paper");
    public static final DeferredItem<Item> CIGARETTE_FILTER = ITEMS.registerSimpleItem("cigarette_filter");

    public static final DeferredItem<Item> MALBOOM_RED_BLEND = ITEMS.registerSimpleItem("malboom_red_blend");
    public static final DeferredItem<Item> WINSTONE_BLUE_BLEND = ITEMS.registerSimpleItem("winstone_blue_blend");
    public static final DeferredItem<Item> GLOWLEAF_BLEND = ITEMS.registerSimpleItem("glowleaf_blend");
    public static final DeferredItem<Item> BLAZEBORO_GOLD_BLEND = ITEMS.registerSimpleItem("blazeboro_gold_blend");
    public static final DeferredItem<Item> CREEPERFIELD_CLASSIC_BLEND = ITEMS.registerSimpleItem("creeperfield_classic_blend");
    public static final DeferredItem<Item> CRAMEL_LIGHT_BLEND = ITEMS.registerSimpleItem("cramel_light_blend");
    public static final DeferredItem<Item> LUCKY_STONE_BLEND = ITEMS.registerSimpleItem("lucky_stone_blend");
    public static final DeferredItem<Item> PALL_MINE_DARK_BLEND = ITEMS.registerSimpleItem("pall_mine_dark_blend");

    public static final List<DeferredItem<Item>> CREATIVE_TAB_ITEMS = List.of(
            VIRGINIA_SEEDS,
            BURLEY_SEEDS,
            HAVANA_SEEDS,
            FRESH_VIRGINIA_LEAF,
            FRESH_BURLEY_LEAF,
            FRESH_HAVANA_LEAF,
            CURED_VIRGINIA_LEAF,
            CURED_BURLEY_LEAF,
            CURED_HAVANA_LEAF,
            CUT_VIRGINIA_TOBACCO,
            CUT_BURLEY_TOBACCO,
            CUT_HAVANA_TOBACCO,
            CIGARETTE_PAPER,
            CIGARETTE_FILTER,
            MALBOOM_RED_BLEND,
            WINSTONE_BLUE_BLEND,
            GLOWLEAF_BLEND,
            BLAZEBORO_GOLD_BLEND,
            CREEPERFIELD_CLASSIC_BLEND,
            CRAMEL_LIGHT_BLEND,
            LUCKY_STONE_BLEND,
            PALL_MINE_DARK_BLEND
    );

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
