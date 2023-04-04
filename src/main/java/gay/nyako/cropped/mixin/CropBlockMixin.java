package gay.nyako.cropped.mixin;

import gay.nyako.cropped.CroppedMod;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Inject(method = "getAvailableMoisture(Lnet/minecraft/block/Block;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F", at = @At("RETURN"), cancellable = true)
    private static void cropped$rainWateringMixin(Block block, BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (CroppedMod.CONFIG.rainWatering && world instanceof World worldInstance && worldInstance.isRaining())
        {
            cir.setReturnValue(cir.getReturnValue() * CroppedMod.CONFIG.rainWateringMultiplier);
        }
    }
}
