package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Right-clicking an unpowered piston (sticky included, both are {@link PistonBaseBlock}) with an axe
 * sets {@code EXTENDED} without a head, i.e. turns it into a headless piston.
 * <p>
 * The flags matter: {@code UPDATE_SKIP_ON_PLACE} keeps {@code PistonBaseBlock#onPlace} (and therefore
 * {@code checkIfExtend}) from running - otherwise the unpowered, now "extended" piston would immediately
 * contract again - and {@code UPDATE_KNOWN_SHAPE} skips the neighbour shape refresh. Only the client
 * block update is sent, no neighbour/redstone update is emitted.
 */
@Mixin(AxeItem.class)
public abstract class AxeItemMixin {

    @Shadow
    private static boolean playerHasBlockingItemUseIntent(UseOnContext context) {
        throw new AssertionError();
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$makeHeadlessPiston(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!CarpetOOCAdditionSettings.axeMakesHeadlessPiston || playerHasBlockingItemUseIntent(context)) {
            return;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PistonBaseBlock) || state.getValue(PistonBaseBlock.EXTENDED)) {
            return;
        }
        if (level.hasNeighborSignal(pos)) {
            return;
        }

        if (!level.isClientSide()) {
            level.setBlock(
                    pos,
                    state.setValue(PistonBaseBlock.EXTENDED, true),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_SKIP_ON_PLACE);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
