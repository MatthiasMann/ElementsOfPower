package gigaherz.elementsofpower.essentializer.gui;

import gigaherz.elementsofpower.ElementsOfPower;
import gigaherz.elementsofpower.capabilities.CapabilityMagicContainer;
import gigaherz.elementsofpower.database.EssenceConversions;
import gigaherz.elementsofpower.database.MagicAmounts;
import gigaherz.elementsofpower.essentializer.TileEssentializer;
import gigaherz.elementsofpower.network.EssentializerAmountsUpdate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.Arrays;
import java.util.regex.Pattern;

public class ContainerEssentializer
        extends Container
{
    protected TileEssentializer tile;
    private MagicAmounts prevContained = MagicAmounts.EMPTY;
    private MagicAmounts prevRemaining = MagicAmounts.EMPTY;

    public ContainerEssentializer(TileEssentializer tileEntity, InventoryPlayer playerInventory)
    {
        this.tile = tileEntity;

        IItemHandler inv = tileEntity.getInventory();

        addSlotToContainer(new SlotSource(inv, 0, 80, 44));
        addSlotToContainer(new SlotContainerIn(inv, 1, 8, 56));
        addSlotToContainer(new SlotContainerOut(inv, 2, 152, 56));

        bindPlayerInventory(playerInventory);
    }

    protected void bindPlayerInventory(InventoryPlayer playerInventory)
    {
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                addSlotToContainer(new Slot(playerInventory,
                        j + i * 9 + 9,
                        8 + j * 18, 94 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 152));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!prevContained.equals(tile.containedMagic)
                || !prevRemaining.equals(tile.remainingToConvert))
        {
            for (IContainerListener watcher : this.listeners)
            {
                if (watcher instanceof EntityPlayerMP)
                {
                    ElementsOfPower.channel.sendTo(new EssentializerAmountsUpdate(windowId, tile), (EntityPlayerMP) watcher);
                }
            }

            prevContained = tile.containedMagic;
            prevRemaining = tile.remainingToConvert;
        }
    }

    public void updateAmounts(MagicAmounts contained, MagicAmounts remaining)
    {
        tile.containedMagic = contained;
        tile.remainingToConvert = remaining;

        Pattern p = Pattern.compile("");
        p.matcher("");
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex)
    {
        Slot slot = this.inventorySlots.get(slotIndex);
        if (slot == null || !slot.getHasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getStack();
        assert stack.getCount() > 0;
        ItemStack stackCopy = stack.copy();

        int startIndex;
        int endIndex;

        if (slotIndex >= 3)
        {
            boolean itemIsContainer = CapabilityMagicContainer.hasContainer(stack);
            boolean itemHasEssence = EssenceConversions.itemHasEssence(stack);

            if (itemIsContainer)
            {
                startIndex = 1;
                endIndex = startIndex + 2;
            }
            else if (itemHasEssence)
            {
                startIndex = 0;
                endIndex = startIndex + 1;
            }
            else if (slotIndex < (27 + 3))
            {
                startIndex = 27 + 3;
                endIndex = startIndex + 9;
            }
            else if (slotIndex >= (27 + 3))
            {
                startIndex = 3;
                endIndex = startIndex + 27;
            }
            else
            {
                return ItemStack.EMPTY;
            }
        }
        else
        {
            startIndex = 3;
            endIndex = startIndex + 9 * 4;
        }

        if (!this.mergeItemStack(stack, startIndex, endIndex, false))
        {
            return ItemStack.EMPTY;
        }

        if (stack.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }

        if (stack.getCount() == stackCopy.getCount())
        {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return stackCopy;
    }
}