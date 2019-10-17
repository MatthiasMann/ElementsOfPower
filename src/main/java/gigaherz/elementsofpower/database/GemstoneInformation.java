package gigaherz.elementsofpower.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gigaherz.elementsofpower.ElementsOfPower;
import gigaherz.elementsofpower.gemstones.Gemstone;
import gigaherz.elementsofpower.gemstones.ItemGemstone;
import gigaherz.elementsofpower.gemstones.Quality;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class GemstoneInformation
{
    @SuppressWarnings("unchecked")
    static final List<Pair<ItemStack, String>> GEMS = Lists.newArrayList(
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Ruby), "gemRuby"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Sapphire), "gemSapphire"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Citrine), "gemCitrine"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Agate), "gemAgate"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Quartz), "gemQuartz"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Serendibite), "gemSerendibite"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Emerald), "gemEmerald"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Amethyst), "gemAmethyst"),
            Pair.of(ElementsOfPower.gemstone.getStack(Gemstone.Diamond), "gemDiamond")
    );

    static Random rand = new Random();

    public static ItemStack identifyQuality(ItemStack stack)
    {
        if (stack.getCount() <= 0)
            return ItemStack.EMPTY;

        Item item = stack.getItem();
        if (item instanceof ItemGemstone)
        {
            if (((ItemGemstone) item).getQuality(stack) != null)
                return stack;
        }

        int[] ids = OreDictionary.getOreIDs(stack);
        Set<String> names = Sets.newHashSet();
        for (int i : ids)
        { names.add(OreDictionary.getOreName(i)); }

        for (Pair<ItemStack, String> target : GEMS)
        {
            if (names.contains(target.getRight()))
            {
                return setRandomQualityVariant(target.getLeft().copy());
            }
        }

        return stack;
    }

    private static ItemStack setRandomQualityVariant(ItemStack target)
    {
        float rnd = rand.nextFloat();
        if (rnd > 0.3f)
            return ElementsOfPower.gemstone.setQuality(target, Quality.Rough);
        if (rnd > 0.1f)
            return ElementsOfPower.gemstone.setQuality(target, Quality.Common);
        if (rnd > 0.01f)
            return ElementsOfPower.gemstone.setQuality(target, Quality.Smooth);
        if (rnd > 0.001f)
            return ElementsOfPower.gemstone.setQuality(target, Quality.Flawless);

        return ElementsOfPower.gemstone.setQuality(target, Quality.Pure);
    }
}