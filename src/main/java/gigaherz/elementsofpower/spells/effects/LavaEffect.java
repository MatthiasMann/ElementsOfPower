package gigaherz.elementsofpower.spells.effects;

import gigaherz.elementsofpower.spells.InitializedSpellcast;
import gigaherz.elementsofpower.spells.Spellcast;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;

public class LavaEffect extends SpellEffect
{
    boolean spawnSourceBlocks;

    public LavaEffect(boolean spawnSourceBlocks)
    {
        this.spawnSourceBlocks = spawnSourceBlocks;
    }

    @Override
    public int getColor(InitializedSpellcast cast)
    {
        return 0x8080FF;
    }

    @Override
    public int getDuration(InitializedSpellcast cast)
    {
        return 20 * 5;
    }

    @Override
    public int getInterval(InitializedSpellcast cast)
    {
        return 10;
    }

    @Override
    public int getForceModifier(InitializedSpellcast cast)
    {
        return cast.world.func_230315_m_().func_236040_e_() ? +1 : 0;
    }

    @Override
    public void processDirectHit(InitializedSpellcast cast, Entity entity, Vector3d hitVec)
    {

    }

    @Override
    public boolean processEntitiesAroundBefore(InitializedSpellcast cast, Vector3d hitVec)
    {
        return true;
    }

    @Override
    public void processEntitiesAroundAfter(InitializedSpellcast cast, Vector3d hitVec)
    {

    }

    @Override
    public void spawnBallParticles(InitializedSpellcast cast, RayTraceResult mop)
    {
        for (int i = 0; i < 8; ++i)
        {
            Vector3d hitVec = mop.getHitVec();
            cast.spawnRandomParticle(ParticleTypes.LAVA, hitVec.x, hitVec.y, hitVec.z);
        }
    }

    @Override
    public void processBlockWithinRadius(InitializedSpellcast cast, BlockPos blockPos, BlockState currentState, float r, @Nullable RayTraceResult mop)
    {
        if (mop != null && mop.getType() == RayTraceResult.Type.BLOCK)
        {
            blockPos = blockPos.offset(((BlockRayTraceResult) mop).getFace());
            currentState = cast.world.getBlockState(blockPos);
        }

        if (currentState.isAir(cast.world, blockPos))
        {
            if (spawnSourceBlocks)
            {
                cast.world.setBlockState(blockPos, Fluids.LAVA.getDefaultState().getBlockState());
            }
            else
            {
                cast.world.setBlockState(blockPos, Fluids.FLOWING_LAVA.getDefaultState().with(LavaFluid.LEVEL_1_8, 8).getBlockState());
            }
        }
    }
}
