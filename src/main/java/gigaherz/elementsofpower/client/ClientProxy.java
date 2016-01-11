package gigaherz.elementsofpower.client;

import gigaherz.elementsofpower.ElementsOfPower;
import gigaherz.elementsofpower.ISideProxy;
import gigaherz.elementsofpower.entities.EntityBall;
import gigaherz.elementsofpower.entities.EntityEssence;
import gigaherz.elementsofpower.entities.EntityTeleporter;
import gigaherz.elementsofpower.entitydata.SpellcastEntityData;
import gigaherz.elementsofpower.essentializer.TileEssentializer;
import gigaherz.elementsofpower.models.ObjModelLoader;
import gigaherz.elementsofpower.network.SetSpecialSlot;
import gigaherz.elementsofpower.network.SpellcastSync;
import gigaherz.elementsofpower.renders.*;
import gigaherz.elementsofpower.util.Used;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@Used
public class ClientProxy implements ISideProxy
{
    public void preInit()
    {
        OBJLoader.instance.addDomain(ElementsOfPower.MODID);

        registerClientEvents();
        registerCustomBakedModels();
        registerModels();
        registerEntityRenderers();
    }

    public void init()
    {
        registerParticle();
    }

    public void registerClientEvents()
    {
        MinecraftForge.EVENT_BUS.register(new GuiOverlayMagicContainer());
        MinecraftForge.EVENT_BUS.register(new MagicTooltips());
        MinecraftForge.EVENT_BUS.register(new PlayerBeamRenderOverlay());
        MinecraftForge.EVENT_BUS.register(new TickEventWandControl());
    }

    @Override
    public void handleSpellcastSync(SpellcastSync message)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> handleSpellcastSync2(message));
    }

    public void handleSpellcastSync2(SpellcastSync message)
    {
        World world = Minecraft.getMinecraft().theWorld;
        EntityPlayer player = (EntityPlayer) world.getEntityByID(message.casterID);
        SpellcastEntityData data = SpellcastEntityData.get(player);

        if (data != null)
            data.sync(message.changeMode, message.spellcast);
    }

    @Override
    public void handleSetSpecialSlot(SetSpecialSlot message)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> handleSetSpecialSlot2(message));
    }

    void handleSetSpecialSlot2(SetSpecialSlot message)
    {
        Minecraft gameController = Minecraft.getMinecraft();

        EntityPlayer entityplayer = gameController.thePlayer;

        if (message.windowId == -1)
        {
            entityplayer.inventory.setItemStack(message.stack);
        }
        else
        {
            boolean flag = false;

            if (gameController.currentScreen instanceof GuiContainerCreative)
            {
                GuiContainerCreative guicontainercreative = (GuiContainerCreative) gameController.currentScreen;
                flag = guicontainercreative.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
            }

            if (message.windowId == 0 && message.slot >= 36 && message.slot < 45)
            {
                ItemStack itemstack = entityplayer.inventoryContainer.getSlot(message.slot).getStack();

                if (message.stack != null && (itemstack == null || itemstack.stackSize < message.stack.stackSize))
                {
                    message.stack.animationsToGo = 5;
                }

                entityplayer.inventoryContainer.putStackInSlot(message.slot, message.stack);
            }
            else if (message.windowId == entityplayer.openContainer.windowId && (message.windowId != 0 || !flag))
            {
                entityplayer.openContainer.putStackInSlot(message.slot, message.stack);
            }
        }
    }

    public void registerParticle()
    {
        Minecraft.getMinecraft().effectRenderer.registerParticle(ElementsOfPower.SMALL_CLOUD_PARTICLE_ID, new EntitySmallCloudFX.Factory());
    }

    // ----------------------------------------------------------- Item/Block Custom OBJ Models
    public void registerCustomBakedModels()
    {
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/thing"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/wand_lapis"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/wand_emerald"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/wand_diamond"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/wand_creative"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/staff_lapis"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/staff_emerald"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/staff_diamond"));
        ObjModelLoader.instance.setExplicitOverride(new ResourceLocation(ElementsOfPower.MODID, "models/item/staff_creative"));
    }

    // ----------------------------------------------------------- Item/Block Models
    public void registerModels()
    {
        registerBlockModelAsItem(ElementsOfPower.essentializer, "essentializer");
        registerBlockModelAsItem(ElementsOfPower.dust, "dust");

        registerItemModel(ElementsOfPower.magicOrb, 0, "orb_fire");
        registerItemModel(ElementsOfPower.magicOrb, 1, "orb_water");
        registerItemModel(ElementsOfPower.magicOrb, 2, "orb_air");
        registerItemModel(ElementsOfPower.magicOrb, 3, "orb_earth");
        registerItemModel(ElementsOfPower.magicOrb, 4, "orb_light");
        registerItemModel(ElementsOfPower.magicOrb, 5, "orb_dark");
        registerItemModel(ElementsOfPower.magicOrb, 6, "orb_life");
        registerItemModel(ElementsOfPower.magicOrb, 7, "orb_death");
        registerItemModel(ElementsOfPower.magicWand, 0, "wand_lapis");
        registerItemModel(ElementsOfPower.magicWand, 1, "wand_emerald");
        registerItemModel(ElementsOfPower.magicWand, 2, "wand_diamond");
        registerItemModel(ElementsOfPower.magicWand, 3, "wand_creative");
        registerItemModel(ElementsOfPower.magicWand, 4, "staff_lapis");
        registerItemModel(ElementsOfPower.magicWand, 5, "staff_emerald");
        registerItemModel(ElementsOfPower.magicWand, 6, "staff_diamond");
        registerItemModel(ElementsOfPower.magicWand, 7, "staff_creative");
        registerItemModel(ElementsOfPower.magicContainer, 0, "container_lapis");
        registerItemModel(ElementsOfPower.magicContainer, 1, "container_emerald");
        registerItemModel(ElementsOfPower.magicContainer, 2, "container_diamond");
        registerItemModel(ElementsOfPower.magicRing, 0, "magicRing", "gem=lapis");
        registerItemModel(ElementsOfPower.magicRing, 1, "magicRing", "gem=emerald");
        registerItemModel(ElementsOfPower.magicRing, 2, "magicRing", "gem=diamond");
        registerItemModel(ElementsOfPower.magicRing, 3, "magicRing", "gem=creative");
    }

    public void registerBlockModelAsItem(final Block block, final String blockName)
    {
        registerBlockModelAsItem(block, 0, blockName);
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String blockName)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(ElementsOfPower.MODID + ":" + blockName, "inventory"));
    }

    public void registerItemModel(final Item item, int meta, final String itemName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ElementsOfPower.MODID + ":" + itemName, "inventory"));
    }

    public void registerItemModel(final Item item, int meta, final String itemName, final String variantName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ElementsOfPower.MODID + ":" + itemName, variantName));
    }

    // ----------------------------------------------------------- Entity Renderers
    public void registerEntityRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentializer.class, new RenderEssentializer());

        RenderingRegistry.registerEntityRenderingHandler(EntityTeleporter.class, RenderBall::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBall.class, RenderBall::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEssence.class, RenderEssence::new);

        RenderingStuffs.init();
    }
}
