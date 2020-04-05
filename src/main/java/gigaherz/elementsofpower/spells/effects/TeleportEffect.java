package gigaherz.elementsofpower.spells.effects;

import gigaherz.elementsofpower.spells.Spellcast;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class TeleportEffect extends SpellEffect
{
    @Override
    public int getColor(Spellcast cast)
    {
        return 0x20A020;
    }

    @Override
    public int getDuration(Spellcast cast)
    {
        return 0;
    }

    @Override
    public int getInterval(Spellcast cast)
    {
        return 0;
    }

    @Override
    public void processDirectHit(Spellcast cast, Entity entity, Vec3d hitVec)
    {
        if (entity == cast.player)
            return;
        entity.attackEntityFrom(DamageSource.causeThrownDamage(cast.projectile, cast.player), 0.0F);
    }

    @Override
    public boolean processEntitiesAroundBefore(Spellcast cast, Vec3d hitVec)
    {
        if (!cast.world.isRemote)
        {
            if (cast.player instanceof ServerPlayerEntity)
            {
                ServerPlayerEntity playerMP = (ServerPlayerEntity) cast.player;

                if (playerMP.connection.getNetworkManager().isChannelOpen()
                        && playerMP.world == cast.world
                        && !playerMP.isSleeping())
                {
                    if (playerMP.isPassenger())
                    {
                        playerMP.stopRiding();
                    }

                    playerMP.setPositionAndUpdate(hitVec.x, hitVec.y, hitVec.z);
                    playerMP.fallDistance = 0.0F;
                }
            }
            else if (cast.player != null)
            {
                cast.player.setPositionAndUpdate(hitVec.x, hitVec.y, hitVec.z);
                cast.player.fallDistance = 0.0F;
            }
        }

        // Do not process the actual ball, we just want the hit target
        return false;
    }

    @Override
    public void processEntitiesAroundAfter(Spellcast cast, Vec3d hitVec)
    {

    }

    @Override
    public void spawnBallParticles(Spellcast cast, RayTraceResult mop)
    {
        for (int i = 0; i < 32; ++i)
        {
            Vec3d hitVec = mop.getHitVec();
            cast.spawnRandomParticle(ParticleTypes.PORTAL, hitVec.x, hitVec.y, hitVec.z);
        }
    }

    @Override
    public void processBlockWithinRadius(Spellcast cast, BlockPos blockPos, BlockState currentState, float r, @Nullable RayTraceResult mop)
    {

    }
}
