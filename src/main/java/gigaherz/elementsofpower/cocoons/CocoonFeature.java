package gigaherz.elementsofpower.cocoons;

import com.mojang.serialization.Codec;
import gigaherz.elementsofpower.ElementsOfPowerMod;
import gigaherz.elementsofpower.magic.MagicAmounts;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Objects;
import java.util.Random;

public class CocoonFeature extends Feature<CocoonFeatureConfig>
{
    @ObjectHolder("elementsofpower:cocoon")
    public static CocoonFeature INSTANCE;

    public static final ITag.INamedTag<Block> REPLACEABLE_TAG = BlockTags.makeWrapperTag("elementsofpower:can_cocoon_replace");

    public CocoonFeature(Codec<CocoonFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator gen, Random rand, BlockPos pos, CocoonFeatureConfig config)
    {
        int top = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, pos.getX(), pos.getZ());
        if (pos.getY() < top && (worldIn.isAirBlock(pos) || worldIn.getBlockState(pos).getBlock() == Blocks.WATER))
        {
            for (Direction f : Direction.values())
            {
                BlockPos pos1 = pos.offset(f);
                if (worldIn.getBlockState(pos1).isSolidSide(worldIn, pos1, f.getOpposite()))
                {
                    generateOne(pos, f, rand, worldIn, config);
                    return true;
                }
            }
        }
        return false;
    }

    private void generateOne(BlockPos pos, Direction f, Random rand, IWorld world, CocoonFeatureConfig config)
    {
        int size = 6 + rand.nextInt(10);

        MagicAmounts am = MagicAmounts.EMPTY;

        while (size-- > 0)
        {
            int y = pos.getY() + rand.nextInt(11) - 5;
            int x = pos.getX() + rand.nextInt(11) - 5;
            int z = pos.getZ() + rand.nextInt(11) - 5;
            if (y < 0)
            {
                am = am.darkness(1);
            }
            else if (y >= world.getHeight())
            {
                am = am.add(config.getAt(1.01f));
            }
            else
            {
                BlockPos pos1 = new BlockPos(x, y, z);
                BlockState state = world.getBlockState(pos1);
                Block b = state.getBlock();

                am = am.add(config.getAt(y/(float)world.getHeight()));

                Material mat = state.getMaterial();
                if (mat == Material.AIR)
                {
                    am = am.air(0.25f);
                }
                else if (mat == Material.WATER)
                {
                    am = am.water(1.5f);
                }
                else if (mat == Material.LAVA)
                {
                    am = am.fire(1);
                    am = am.earth(0.5f);
                }
                else if (mat == Material.FIRE)
                {
                    am = am.fire(1);
                    am = am.air(0.5f);
                }
                else if (mat == Material.ROCK)
                {
                    am = am.earth(1);
                    if (b == Blocks.NETHERRACK)
                    {
                        am = am.fire(0.5f);
                    }
                    else if (b == Blocks.END_STONE || b == Blocks.END_STONE_BRICKS)
                    {
                        am = am.darkness(0.5f);
                    }
                }
                else if (mat == Material.SAND)
                {
                    am = am.earth(0.5f);
                    if (b == Blocks.SOUL_SAND)
                    {
                        am = am.death(1);
                    }
                    else
                    {
                        am = am.air(1);
                    }
                }
                else if (mat == Material.WOOD)
                {
                    am = am.life(1);
                    am = am.earth(0.5f);
                }
                else if (mat == Material.LEAVES)
                {
                    am = am.life(1);
                }
                else if (mat == Material.PLANTS)
                {
                    am = am.life(1);
                }
                else if (mat == Material.CACTUS)
                {
                    am = am.life(1);
                    am = am.earth(0.5f);
                }
                else if (mat == Material.ORGANIC)
                {
                    am = am.life(0.5f);
                    am = am.earth(1);
                }
                else if (mat == Material.EARTH)
                {
                    am = am.earth(1);
                    if (b == Blocks.PODZOL)
                    {
                        am = am.life(0.5f);
                    }
                }
                else if (mat == Material.IRON)
                {
                    am = am.earth(1);
                }
                else if (mat == Material.GLASS)
                {
                    am = am.earth(0.5f);
                    am = am.light(0.5f);
                    am = am.air(0.5f);
                }
                else if (mat == Material.REDSTONE_LIGHT)
                {
                    am = am.earth(0.5f);
                    am = am.light(1);
                }
                else if (mat == Material.ICE || mat == Material.PACKED_ICE)
                {
                    am = am.water(1);
                    am = am.darkness(0.5f);
                }
                else if (mat == Material.SNOW || mat == Material.SNOW_BLOCK)
                {
                    am = am.water(0.5f);
                    am = am.darkness(0.5f);
                }
                else if (mat == Material.CLAY)
                {
                    am = am.earth(0.5f);
                    am = am.water(1);
                }
                else if (mat == Material.GOURD)
                {
                    am = am.earth(0.5f);
                    am = am.life(0.25f);
                }
                else if (mat == Material.DRAGON_EGG)
                {
                    am = am.darkness(1);
                }
            }
        }

        if (!am.isEmpty())
        {
            FluidState fluidState = world.getFluidState(pos);
            world.setBlockState(pos, am.getDominantElement().getCocoon().getDefaultState().with(CocoonBlock.FACING, f).with(CocoonBlock.WATERLOGGED, fluidState.getFluid() == Fluids.WATER), 2);
            CocoonTileEntity te = Objects.requireNonNull((CocoonTileEntity) world.getTileEntity(pos));
            te.essenceContained = te.essenceContained.add(am);

            //ElementsOfPowerMod.LOGGER.debug("Generated cocoon at {} with amounts {}", pos, am);
        }
    }
}
