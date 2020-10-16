package gigaherz.elementsofpower.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gigaherz.elementsofpower.gemstones.Gemstone;
import gigaherz.elementsofpower.magic.MagicAmounts;
import gigaherz.elementsofpower.spells.Element;
import net.minecraft.block.Blocks;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class StockConversions
{
    private static final Map<Item, ItemEssenceConversion> CONVERSIONS = Maps.newHashMap();

    public static void addStockConversions(MinecraftServer server)
    {
        CONVERSIONS.clear();

        for (Element e : Element.values)
        {
            essences(e.getOrb()).element(e, 8);
            essences(e.getItem()).element(e, 8).life(2);
        }

        for (Gemstone e : Gemstone.values)
        {
            ItemEssenceEntry a = essences(e.getOre()).earth(8);
            ItemEssenceEntry b = essences(e.getItem()).earth(1);
            ItemEssenceEntry c = essences(e.getBlock()).earth(19);
            if (e.getElement() != null)
            {
                a.element(e.getElement(), 1);
                b.element(e.getElement(), 1 / 8.0f);
                c.element(e.getElement(), 1);
            }
        }

        essences(Blocks.CACTUS).life(3);
        essences(Blocks.CHEST).earth(2).light(1);

        for (DyeColor color : DyeColor.values())
        {
            Item item = DyeItem.getItem(color);
            essences(item).earth(1).life(1);
        }
        essences(Items.INK_SAC).water(2).darkness(2);
        essences(Items.LAPIS_LAZULI).earth(8);
        essences(Items.BONE_MEAL).earth(1).death(1);

        essences(Blocks.CLAY).earth(4).water(4);
        essences(Items.BRICK).earth(1).fire(1);
        essences(Blocks.BRICKS).earth(4).fire(4);

        essences(Blocks.DIRT).earth(3).life(1);
        essences(Blocks.COARSE_DIRT).earth(3).life(1);
        essences(Blocks.GRAVEL).earth(3).air(1);
        essences(Blocks.SAND).earth(2).air(2);
        essences(Blocks.RED_SAND).earth(2).air(2);
        essences(Blocks.SANDSTONE).earth(8).air(8);
        essences(Blocks.RED_SANDSTONE).earth(8).air(8);
        essences(Blocks.SMOOTH_SANDSTONE).earth(8).air(8);
        essences(Blocks.SMOOTH_RED_SANDSTONE).earth(8).air(8);
        essences(Blocks.OBSIDIAN).earth(10).darkness(10);
        essences(Blocks.NETHERRACK).earth(1).fire(1);

        essences(Blocks.COBBLESTONE).earth(5);
        fromTag(server, "minecraft:stone_crafting_materials", items ->
                essences(items).earth(10)
        );

        essences(Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA,
                Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA,
                Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA,
                Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA,
                Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA,
                Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA,
                Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA,
                Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA).earth(5).fire(1);

        essences(Blocks.TALL_GRASS).earth(2).life(2);
        essences(Blocks.GRASS).earth(2).life(2);
        essences(Blocks.GRASS_BLOCK).earth(3).life(2);
        essences(Blocks.PODZOL).earth(3).life(1).death(1);

        fromTag(server, "minecraft:small_flowers", items ->
                essences(items).life(1)
        );
        // Overwrite WITHER_ROSE if it's in the tag.
        essences(Blocks.WITHER_ROSE).life(1).death(2);

        fromTag(server, "minecraft:tall_flowers", items ->
                essences(items).life(2)
        );

        fromTag(server, "minecraft:saplings", items ->
                essences(items).life(4)
        );

        fromTag(server, "minecraft:logs", items ->
                essences(items).life(16)
        );

        fromTag(server, "minecraft:leaves", items ->
                essences(items).life(4)
        );

        essences(Items.STICK).life(1);

        essences(Items.COAL, Items.CHARCOAL).fire(8);
        essences(Blocks.COAL_BLOCK).fire(72).earth(8);

        essences(Items.WHEAT).life(1);
        essences(Blocks.HAY_BLOCK).earth(1).life(9).air(1);

        essences(Blocks.RED_MUSHROOM).earth(2).life(2);
        essences(Blocks.BROWN_MUSHROOM).earth(2).life(2);
        essences(Blocks.PUMPKIN).earth(1).life(3);
        essences(Blocks.SPONGE).water(1).life(2);
        essences(Blocks.WET_SPONGE).water(4).life(2);
        essences(Blocks.VINE).life(2);

        essences(Items.NETHER_STAR).all(64);

        essences(Items.IRON_INGOT).earth(18);
        essences(Items.GOLD_INGOT).earth(18);
        essences(Items.GOLD_NUGGET).earth(2);

        essences(Items.BLAZE_ROD).fire(12).life(8);

        essences(
                Items.COD,
                Items.SALMON,
                Items.TROPICAL_FISH,
                Items.PUFFERFISH
        ).life(4).water(2);

        essences(Items.DIAMOND).earth(128);
        essences(Items.EMERALD).earth(100).life(50);
        essences(Items.QUARTZ).earth(100).light(50);

        essences(Gemstone.RUBY.getItem()).earth(100).fire(50);
        essences(Gemstone.SAPPHIRE.getItem()).earth(100).water(50);
        essences(Gemstone.CITRINE.getItem()).earth(100).air(50);
        essences(Gemstone.AGATE.getItem()).earth(100).earth(50);
        essences(Gemstone.QUARTZ.getItem()).earth(100).light(50);
        essences(Gemstone.SERENDIBITE.getItem()).earth(100).darkness(50);
        essences(Gemstone.EMERALD.getItem()).earth(100).life(50);
        essences(Gemstone.AMETHYST.getItem()).earth(100).death(50);
        essences(Gemstone.DIAMOND.getItem()).earth(128);

        essences(Items.CLAY_BALL).earth(8).water(2);

        essences(Items.FEATHER).air(4).life(4);

        essences(Items.FLINT).earth(1);

        essences(Items.STRING).earth(1).life(1).air(1);

        essences(Items.SNOWBALL).water(1).air(1);

        essences(Items.APPLE).life(2).earth(1).air(1);
        essences(Items.BEEF).life(8);
        essences(Items.PORKCHOP).life(8);
        essences(Items.MUTTON).life(8);
        essences(Items.RABBIT).life(4);
        essences(Items.CARROT).life(2).earth(2);
        essences(Items.MELON).life(1).earth(1);
        essences(Items.CHICKEN).life(4).air(2);
        essences(Items.EGG).life(2).air(2).light(2);
        essences(Items.POTATO).life(2).earth(2);
        essences(Items.POISONOUS_POTATO).death(2).earth(2);

        essences(Items.REDSTONE).earth(4).light(4);

        essences(Items.MILK_BUCKET).life(2).water(4).earth(54);

        essences(Items.BONE).death(4);
        essences(Items.GUNPOWDER).death(2).fire(2).earth(2);

        essences(Items.SUGAR_CANE).life(4).water(4).earth(2);

        //essences(Blocks.NETHER_BRICK);
        //essences(Blocks.QUARTZ_BLOCK);

        //essences(Items.ENDER_PEARL,0);
        //essences(Items.MAP,0);
        //essences(Items.MILK_BUCKET,0);
        //essences(Items.POTATO,0);
        //essences(Items.PRISMARINE_CRYSTALS,0);
        //essences(Items.PRISMARINE_SHARD,0);
        //essences(Items.RABBIT_HIDE,0);
        //essences(Items.SLIME_BALL,0);
        //essences(Items.SPIDER_EYE,0);
        //essences(Items.GLOWSTONE_DUST,0);


        CONVERSIONS.values().forEach(ItemEssenceConversion::apply);
    }

    private static ItemEssenceCollection collection(ItemEssenceConversion... entries)
    {
        ItemEssenceCollection collection = new ItemEssenceCollection();

        Collections.addAll(collection, entries);

        return collection;
    }

    @Nullable
    private static ItemEssenceCollection fromTag(MinecraftServer server, String id, Function<List<Item>, ItemEssenceCollection> conversion)
    {
        ITag<Item> tag = server.func_244266_aF().getItemTags().get(new ResourceLocation(id));

        if (tag == null) return essences();

        return conversion.apply(tag.getAllElements());
    }

    private static ItemEssenceCollection essences(IItemProvider... items)
    {
        ItemEssenceCollection collection = new ItemEssenceCollection();

        for (IItemProvider item : items)
        {
            ItemEssenceEntry ee = new ItemEssenceEntry(item, MagicAmounts.EMPTY);
            collection.add(ee);
            CONVERSIONS.put(item.asItem(), ee);
        }

        return collection;
    }

    private static ItemEssenceCollection essences(Iterable<? extends IItemProvider> items)
    {
        ItemEssenceCollection collection = new ItemEssenceCollection();

        for (IItemProvider item : items)
        {
            ItemEssenceEntry ee = new ItemEssenceEntry(item, MagicAmounts.EMPTY);
            collection.add(ee);
            CONVERSIONS.put(item.asItem(), ee);
        }

        return collection;
    }

    private static ItemEssenceEntry essences(IItemProvider item)
    {
        ItemEssenceEntry ee = new ItemEssenceEntry(item, MagicAmounts.EMPTY);
        CONVERSIONS.put(item.asItem(), ee);
        return ee;
    }

    public interface ItemEssenceConversion
    {
        ItemEssenceConversion all(float amount);

        ItemEssenceConversion fire(float amount);

        ItemEssenceConversion water(float amount);

        ItemEssenceConversion air(float amount);

        ItemEssenceConversion earth(float amount);

        ItemEssenceConversion light(float amount);

        ItemEssenceConversion darkness(float amount);

        ItemEssenceConversion life(float amount);

        ItemEssenceConversion death(float amount);

        ItemEssenceConversion element(Element l, float amount);

        void apply();
    }

    private static class ItemEssenceEntry implements ItemEssenceConversion
    {
        Item item;
        MagicAmounts amounts;

        public ItemEssenceEntry(IItemProvider item, MagicAmounts amounts)
        {
            this.item = item.asItem();
            this.amounts = amounts;
        }

        @Override
        public void apply()
        {
            EssenceConversions.SERVER.addConversion(item, amounts);
        }

        @Override
        public ItemEssenceEntry all(float amount)
        {
            amounts = amounts.all(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry fire(float amount)
        {
            amounts = amounts.fire(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry water(float amount)
        {
            amounts = amounts.water(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry air(float amount)
        {
            amounts = amounts.air(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry earth(float amount)
        {
            amounts = amounts.earth(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry light(float amount)
        {
            amounts = amounts.light(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry darkness(float amount)
        {
            amounts = amounts.darkness(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry life(float amount)
        {
            amounts = amounts.life(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry death(float amount)
        {
            amounts = amounts.death(amount);
            return this;
        }

        @Override
        public ItemEssenceEntry element(Element l, float amount)
        {
            amounts = amounts.add(l, amount);
            return this;
        }
    }

    private static class ItemEssenceCollection extends ArrayList<ItemEssenceConversion> implements ItemEssenceConversion
    {
        @Override
        public void apply()
        {
            this.forEach(ItemEssenceConversion::apply);
        }

        @Override
        public ItemEssenceCollection all(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.all(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection fire(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.fire(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection water(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.water(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection air(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.air(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection earth(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.earth(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection light(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.light(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection darkness(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.darkness(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection life(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.life(amount);
            }
            return this;
        }

        @Override
        public ItemEssenceCollection death(float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.death(amount);
            }
            return this;
        }


        @Override
        public ItemEssenceCollection element(Element l, float amount)
        {
            for (ItemEssenceConversion e : this)
            {
                e.element(l, amount);
            }
            return this;
        }
    }
}
