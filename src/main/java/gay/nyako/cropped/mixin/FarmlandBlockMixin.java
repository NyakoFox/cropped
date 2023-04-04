package gay.nyako.cropped.mixin;

import gay.nyako.cropped.CroppedMod;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin {

    @Shadow
    private static boolean hasCrop(BlockView world, BlockPos pos) {
        return false;
    };

    @Inject(at = @At("HEAD"), method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V", cancellable=true)
    private void cropped$dontBreakOnJumpMixin(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance, CallbackInfo ci) {
        if (!world.isClient() && CroppedMod.CONFIG.dontBreakOnJump) {
            if (hasCrop(world, pos)) {
                ci.cancel();
            }
        }
    }
}