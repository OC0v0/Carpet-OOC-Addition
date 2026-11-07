package carpetoocaddition;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PistonDrops {

    private PistonDrops() {
    }

    /**
     * Drops the block a headless piston is about to delete, according to the rule value.
     *
     * @return whether anything was dropped
     */
    public static boolean dropEatenBlock(ServerLevel level, BlockPos pos) {
        CarpetOOCAdditionSettings.HeadlessPistonDrops mode = CarpetOOCAdditionSettings.headlessPistonDrops;
        if (mode == CarpetOOCAdditionSettings.HeadlessPistonDrops.FALSE) return false;

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.is(Blocks.PISTON_HEAD)) return false;

        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        List<ItemStack> drops = Block.getDrops(state, level, pos, blockEntity);

        if (drops.isEmpty() && mode == CarpetOOCAdditionSettings.HeadlessPistonDrops.ALL) {
            ItemStack fallback = new ItemStack(state.getBlock().asItem());
            if (blockEntity instanceof SpawnerBlockEntity spawner) {
                // A spawner's mob type and timings live in its block entity, so carry them over as the
                // same component a creative-placed spawner item uses (applied again on placement).
                fallback.set(
                        DataComponents.BLOCK_ENTITY_DATA,
                        TypedEntityData.of(spawner.getType(), spawner.saveCustomOnly(level.registryAccess())));
            }
            if (!fallback.isEmpty()) {
                drops = List.of(fallback);
            }
        }

        for (ItemStack stack : drops) {
            Block.popResource(level, pos, stack);
        }
        return !drops.isEmpty();
    }
}
