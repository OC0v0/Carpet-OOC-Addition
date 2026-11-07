package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vanilla only applies an item's {@code block_entity_data} component when the block entity type is
 * not in {@code BlockEntityType.OP_ONLY_CUSTOM_DATA} (spawner, command block, lectern, signs, trial
 * spawner) or when the placer has game master permissions.
 * <p>
 * Returning false here disables that check for every block entity type, so any player can restore
 * the stored NBT on placement. Only the server result matters for placing; the client copy is used
 * for the "op only" tooltip warning, and the client never sees this server-side rule value.
 */
@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @Inject(method = "onlyOpCanSetNbt", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$ignoreOpOnlyCustomData(CallbackInfoReturnable<Boolean> cir) {
        if (CarpetOOCAdditionSettings.disableOpOnlyCustomData) {
            cir.setReturnValue(false);
        }
    }
}
