package gigaherz.elementsofpower.gemstones;

import net.minecraft.item.Rarity;

public enum Quality
{
    Rough(".rough", Rarity.COMMON, 0.9f),
    Common(".common", Rarity.COMMON, 1.0f),
    Smooth(".smooth", Rarity.UNCOMMON, 1.25f),
    Flawless(".flawless", Rarity.RARE, 1.5f),
    Pure(".pure", Rarity.EPIC, 2.0f);

    public static final Quality[] values = values();

    private final String unlocalizedName;
    private final Rarity rarity;
    private final float transferSpeed;

    Quality(String unlocalizedName, Rarity rarity, float transferSpeed)
    {
        this.unlocalizedName = unlocalizedName;
        this.rarity = rarity;
        this.transferSpeed = transferSpeed;
    }

    public String getUnlocalizedName()
    {
        return unlocalizedName;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public float getTransferSpeed()
    {
        return transferSpeed;
    }
}
