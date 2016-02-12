package gigaherz.elementsofpower.items;

import gigaherz.elementsofpower.ElementsOfPower;
import gigaherz.elementsofpower.database.MagicAmounts;
import gigaherz.elementsofpower.gemstones.Element;
import gigaherz.elementsofpower.gemstones.Gemstone;
import gigaherz.elementsofpower.gemstones.Quality;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants;

import java.util.List;

public class ItemGemstone extends ItemMagicContainer
{
    public static final MagicAmounts[] capacities = {
            new MagicAmounts().all(10),
            new MagicAmounts().all(50),
            new MagicAmounts().all(100),
            new MagicAmounts().all(250),
            new MagicAmounts().all(500),
    };

    public ItemGemstone()
    {
        setMaxStackSize(64);
        setHasSubtypes(true);
        setUnlocalizedName(ElementsOfPower.MODID + ".gemstone");

        // FIXME: Change to a gemstones tab
        setCreativeTab(ElementsOfPower.tabMagic);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int sub = stack.getItemDamage();

        if(sub >= Gemstone.values.length)
            return getUnlocalizedName();

        return "item." + ElementsOfPower.MODID + Gemstone.values[sub].getUnlocalizedName();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        Quality q = getQuality(stack);

        String qName = ElementsOfPower.MODID + ".gemstone.quality";
        if(q != null)
            qName += q.getUnlocalizedName();
        else
            qName += ".unknown";

        String qualityPart = StatCollector.translateToLocal(qName);
        String gemPart = StatCollector.translateToLocal(getUnlocalizedName(stack) + ".name");

        return (qualityPart + " " + gemPart).trim();
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
    {
        for (int meta = 0; meta < Gemstone.values.length; meta++)
        {
            for(Quality q : Quality.values)
            {
                subItems.add(setQuality(new ItemStack(itemIn, 1, meta), q));
            }
        }
    }

    @Override
    public MagicAmounts getCapacity(ItemStack stack)
    {
        Gemstone g = getGemstone(stack);
        Quality q = getQuality(stack);
        if(q == null)
            return null;

        MagicAmounts magic = capacities[q.ordinal()].copy();

        Element e = g.getElement();
        if(e == null)
            magic.all(magic.amounts[0] * 0.1f);
        else
            magic.element(g.getElement(), magic.amount(g.getElement()) * 0.25f);

        return magic;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        Quality q = getQuality(stack);
        if(q == null)
            return EnumRarity.COMMON;
        return q.getRarity();
    }

    public Gemstone getGemstone(ItemStack stack)
    {
        int meta = stack.getMetadata();

        if(meta >= Gemstone.values.length)
            return null;

        return Gemstone.values[meta];
    }

    public Quality getQuality(ItemStack stack)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            return null;

        if(!tag.hasKey("quality", Constants.NBT.TAG_INT))
            return null;

        int q = tag.getInteger("quality");
        if (q < 0 || q > Quality.values.length)
            return null;

        return Quality.values[q];
    }

    public ItemStack setQuality(ItemStack stack, Quality q)
    {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
        {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }

        tag.setInteger("quality", q.ordinal());

        return stack;
    }
}
