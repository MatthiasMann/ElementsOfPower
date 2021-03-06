package gigaherz.elementsofpower.client.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import gigaherz.elementsofpower.essentializer.EssentializerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.util.Lazy;

public class EssentializerTileEntityRender extends TileEntityRenderer<EssentializerTileEntity>
{
    private final Lazy<ModelHandle> corner_handle = Lazy.of(() -> ModelHandle.of("elementsofpower:models/block/essentializer_corner.obj"));

    private final ResourceLocation texture = new ResourceLocation("elementsofpower:textures/block/side_obsidian.png");

    public EssentializerTileEntityRender(TileEntityRendererDispatcher rendererDispatcherIn)
    {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(EssentializerTileEntity te, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        //GlStateManager.disableLighting();

        matrixStack.push();
        matrixStack.translate(0.5, 1, 0.5);

        float timeRandom = MathHelper.getPositionRandom(te.getPos()) % 360;

        float time = (te.animateTick + timeRandom + partialTicks) * 1.5f;

        float angle1 = time * 2.5f + 120 * (1 + (float) Math.sin(time * 0.05f));
        float angle2 = time * 0.9f;
        float bob = (float) Math.sin(time * (Math.PI / 180) * 2.91) * 0.06f;

        matrixStack.translate(0, bob, 0);

        for (int i = 0; i < 4; i++)
        {
            matrixStack.push();

            float angle3 = angle2 + 90 * i;
            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle3));
            matrixStack.translate(0.6, 0, 0);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-45));

            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle1));

            corner_handle.get().render(bufferIn, RenderType.getEntityTranslucent(texture), matrixStack, combinedLightIn, 0xFFFFFFFF);

            matrixStack.pop();
        }
        matrixStack.pop();

        /*
        matrixStack.push();
        for (int i = 0; i < 4; i++)
        {
            matrixStack.push();

            float angle3 = angle2 + 90 * i;
            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle3));
            matrixStack.translate(0.6, 0, 0);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-45));

            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle1));

            side_handle.get().render(bufferIn, RenderType.getEntityTranslucent(texture), matrixStack, combinedLightIn, 0xFFFFFFFF);

            matrixStack.pop();
        }
        matrixStack.pop();
         */

        ItemStack stack = te.getInventory().getStackInSlot(0);
        if (stack.getCount() > 0)
        {
            float angle3 = time * 1.5f;
            float bob2 = (float) (1 + Math.sin(time * (Math.PI / 180) * 0.91)) * 0.03f;

            matrixStack.push();

            matrixStack.translate(0.5, 0.45 + bob2, 0.5);

            matrixStack.rotate(Vector3f.YP.rotationDegrees(angle3));

            float scale = 0.45f;
            matrixStack.scale(scale, scale, scale);

            Minecraft mc = Minecraft.getInstance();
            mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStack, bufferIn);

            matrixStack.pop();
        }
    }
}
