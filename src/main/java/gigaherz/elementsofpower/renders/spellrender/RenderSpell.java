package gigaherz.elementsofpower.renders.spellrender;

import com.google.common.collect.Maps;
import gigaherz.elementsofpower.renders.RenderingStuffs;
import gigaherz.elementsofpower.spells.Spellcast;
import gigaherz.elementsofpower.spells.effects.FlameEffect;
import gigaherz.elementsofpower.spells.effects.WaterEffect;
import gigaherz.elementsofpower.spells.effects.WindEffect;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.Map;

public abstract class RenderSpell
{
    Map<String, RenderingStuffs.ModelHandle> cones = Maps.newHashMap();
    Map<String, RenderingStuffs.ModelHandle> spheres = Maps.newHashMap();
    Map<String, RenderingStuffs.ModelHandle> cylinders = Maps.newHashMap();

    protected IBakedModel getCone(String tex)
    {
        RenderingStuffs.ModelHandle h = cones.get(tex);
        if(h == null)
        {
            h = RenderingStuffs.handle("elementsofpower:entity/cone.obj").replace("#Default", tex);
            cones.put(tex, h);
        }

        return RenderingStuffs.loadModel(h);
    }

    protected IBakedModel getSphere(String tex)
    {
        RenderingStuffs.ModelHandle h = spheres.get(tex);
        if(h == null)
        {
            h = RenderingStuffs.handle("elementsofpower:entity/sphere.obj").replace("#Default", tex);
            spheres.put(tex, h);
        }

        return RenderingStuffs.loadModel(h);
    }

    protected IBakedModel getCylinder(String tex)
    {
        RenderingStuffs.ModelHandle h = cylinders.get(tex);
        if(h == null)
        {
            h = RenderingStuffs.handle("elementsofpower:entity/cylinder.obj").replace("#Default", tex);
            cylinders.put(tex, h);
        }

        return RenderingStuffs.loadModel(h);
    }

    public abstract void doRender(Spellcast spellcast, EntityPlayer player, RenderManager renderManager, double x, double y, double z, float partialTicks, Vec3d offset, String tex);

    public void doRender(Spellcast spellcast, EntityPlayer player, RenderManager renderManager, double x, double y, double z, float partialTicks, Vec3d offset)
    {
        String tex = "minecraft:white";

        if (spellcast.getEffect() instanceof FlameEffect)
        {
            tex = "minecraft:blocks/lava_still";
        }
        else if (spellcast.getEffect() instanceof WaterEffect)
        {
            tex = "minecraft:blocks/water_still";
        }
        else if (spellcast.getEffect() instanceof WindEffect)
        {
            tex = "elementsofpower:blocks/cone";
        }

        doRender(spellcast, player, renderManager, x, y, z, partialTicks, offset, tex);
    }
}
