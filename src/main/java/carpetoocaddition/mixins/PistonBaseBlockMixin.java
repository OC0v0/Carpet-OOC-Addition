package carpetoocaddition.mixins;

import carpetoocaddition.PistonDrops;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla deletes the block in front of an unpowered headless piston inside
 * {@code PistonBaseBlock#triggerEvent} with {@code level.removeBlock(pos, false)} (MC-269260).
 * That same call removes the piston head during a normal retraction, so the drop logic itself
 * filters out air and piston heads.
 */
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @WrapOperation(
            method = "triggerEvent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z")
    )
    private boolean carpetoocaddition$dropEatenBlock(Level level, BlockPos pos, boolean movedByPiston, Operation<Boolean> original) {
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            PistonDrops.dropEatenBlock(serverLevel, pos);
        }
        return original.call(level, pos, movedByPiston);
    }
}
