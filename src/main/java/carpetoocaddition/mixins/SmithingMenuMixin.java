package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * The smithing result is only ever computed on the server: {@code SmithingMenu#createResult} returns
 * an empty optional on the client and the result slot is synced, so a vanilla client needs no change
 * for this rule.
 * <p>
 * With the rule on and an empty template slot, the recipe lookup is fed a virtual netherite upgrade
 * template, which makes the ordinary diamond -> netherite recipes match. The template is never
 * consumed: {@code SmithingMenu#onTake} shrinks slot 0, which is empty.
 */
@Mixin(SmithingMenu.class)
public class SmithingMenuMixin {

    @ModifyReturnValue(method = "createRecipeInput", at = @At("RETURN"))
    private SmithingRecipeInput carpetoocaddition$useVirtualNetheriteTemplate(SmithingRecipeInput input) {
        if (!CarpetOOCAdditionSettings.netheriteUpgradeWithoutTemplate) {
            return input;
        }
        if (!input.template().isEmpty() || input.base().isEmpty() || input.addition().isEmpty()) {
            return input;
        }
        return new SmithingRecipeInput(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), input.base(), input.addition());
    }
}
