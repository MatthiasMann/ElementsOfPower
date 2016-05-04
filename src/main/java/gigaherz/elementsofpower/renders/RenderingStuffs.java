package gigaherz.elementsofpower.renders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.pipeline.LightUtil;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderingStuffs
{
    static Map<String, IBakedModel> loadedModels = Maps.newHashMap();

    public static void init()
    {
        IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
        if (rm instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) rm).registerReloadListener(new IResourceManagerReloadListener()
            {
                @Override
                public void onResourceManagerReload(IResourceManager __)
                {
                    loadedModels.clear();
                }
            });
        }
    }

    public static void renderModel(IBakedModel model)
    {
        renderModel(model, DefaultVertexFormats.ITEM);
    }

    public static void renderModel(IBakedModel model, VertexFormat fmt)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, fmt);
        for (BakedQuad bakedquad : model.getQuads(null, null, 0))
        {
            worldrenderer.addVertexData(bakedquad.getVertexData());
        }
        tessellator.draw();
    }

    public static void renderModel(IBakedModel model, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        for (BakedQuad bakedquad : model.getQuads(null, null, 0))
        {
            LightUtil.renderQuadColor(worldrenderer, bakedquad, color);
        }
        tessellator.draw();
    }

    public static IBakedModel loadModel(ModelHandle handle)
    {
        IBakedModel model = loadedModels.get(handle.getKey());
        if (model != null)
            return model;

        try
        {
            TextureMap textures = Minecraft.getMinecraft().getTextureMapBlocks();
            IModel mod = ModelLoaderRegistry.getModel(new ResourceLocation(handle.getModel()));
            if (mod instanceof IRetexturableModel && handle.getTextureReplacements().size() > 0)
            {
                IRetexturableModel rtm = (IRetexturableModel) mod;
                mod = rtm.retexture(ImmutableMap.copyOf(handle.getTextureReplacements()));
            }
            model = mod.bake(mod.getDefaultState(), handle.getVertexFormat(),
                    (location) -> textures.getAtlasSprite(location.toString()));
            loadedModels.put(handle.getKey(), model);
            return model;
        }
        catch (Exception e)
        {
            throw new ReportedException(new CrashReport("Error loading custom model " + handle.getModel(), e));
        }
    }

    public static ModelHandle handle(String model)
    {
        return new ModelHandle(model);
    }

    public static class ModelHandle
    {
        private String model;
        private String key;
        private final Map<String, String> textureReplacements = Maps.newHashMap();
        private VertexFormat vertexFormat = Attributes.DEFAULT_BAKED_FORMAT;

        ModelHandle(String model)
        {
            this.model = model;
            this.key = model;
        }

        public ModelHandle replace(String texChannel, String resloc)
        {
            key += "//" + texChannel + "/" + resloc;
            textureReplacements.put(texChannel, resloc);
            return this;
        }

        public ModelHandle vertexFormat(VertexFormat fmt)
        {
            key += "//VF:" + fmt.hashCode();
            vertexFormat = fmt;
            return this;
        }

        public String getModel()
        {
            return model;
        }

        public String getKey()
        {
            return key;
        }

        public Map<String, String> getTextureReplacements()
        {
            return textureReplacements;
        }

        public VertexFormat getVertexFormat()
        {
            return vertexFormat;
        }

        public void setVertexFormat(VertexFormat vertexFormat)
        {
            this.vertexFormat = vertexFormat;
        }
    }
}
