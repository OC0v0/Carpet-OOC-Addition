package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The bundled data pack adds {@code minecraft:end_portal_frame} to
 * {@code #minecraft:replaceable_by_mushrooms}. This hook makes that data-driven permission conditional
 * on the rule: with the rule off the frame is treated as not replaceable, i.e. vanilla behaviour.
 */
@Mixin(AbstractHugeMushroomFeature.class)
public class AbstractHugeMushroomFeatureMixin {

    @WrapOperation(
            method = "placeMushroomBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean carpetoocaddition$gateEndPortalFrame(BlockState state, TagKey<Block> tag, Operation<Boolean> original) {
        boolean replaceable = original.call(state, tag);
        if (!replaceable) return false;
        if (CarpetOOCAdditionSettings.hugeMushroomReplaceEndPortalFrame) return true;

        // Rule off: ignore the tag entry we ship, so the frame behaves like vanilla.
        return !(state.is(Blocks.END_PORTAL_FRAME) && tag == BlockTags.REPLACEABLE_BY_MUSHROOMS);
    }
}
