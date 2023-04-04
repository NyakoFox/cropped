package gay.nyako.cropped;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CroppedMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("cropped");
	public static final CroppedConfig CONFIG = AutoConfig.register(CroppedConfig.class, GsonConfigSerializer::new).getConfig();

	@Override
	public void onInitialize() {
		UseBlockCallback.EVENT.register(CroppedMod::onBlockUse);

		if (CONFIG.dispenserPlanting) {
			DispenserBlock.registerBehavior(Items.CARROT, itemDispenserBehavior);
			DispenserBlock.registerBehavior(Items.POTATO, itemDispenserBehavior);
			DispenserBlock.registerBehavior(Items.WHEAT_SEEDS, itemDispenserBehavior);
			DispenserBlock.registerBehavior(Items.BEETROOT_SEEDS, itemDispenserBehavior);
			DispenserBlock.registerBehavior(Items.PUMPKIN_SEEDS, itemDispenserBehavior);
			DispenserBlock.registerBehavior(Items.MELON_SEEDS, itemDispenserBehavior);
		}
	}

	ItemDispenserBehavior itemDispenserBehavior = new FallibleItemDispenserBehavior() {
		protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
			this.setSuccess(true);
			World world = pointer.getWorld();
			BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
			if (pointer.getBlockState().getBlock() == Blocks.AIR &&
					world.getBlockState(blockPos.down()).getBlock() == Blocks.FARMLAND
			) {
				Block block = Block.getBlockFromItem(stack.getItem());
				if (block instanceof CropBlock cropBlock) {
					world.setBlockState(blockPos, cropBlock.withAge(0));
				} else {
					world.setBlockState(blockPos, block.getDefaultState());
				}
				stack.decrement(1);
			} else {
				this.setSuccess(false);
				return stack;
			}

			return stack;
		}
	};

	private static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
		if (!CONFIG.rightClickHarvest || player.isSneaking() || player.isSpectator()) {
			return ActionResult.PASS;
		}

		var blockState = world.getBlockState(hitResult.getBlockPos());
		var block = blockState.getBlock();
		var handStack = player.getStackInHand(hand);

		if (block instanceof CropBlock cropBlock) {
			if (cropBlock.isMature(blockState)) {
				cropBlock.onUse(blockState, world, hitResult.getBlockPos(), player, hand, hitResult);

				if (!world.isClient())
				{
					world.setBlockState(hitResult.getBlockPos(), cropBlock.withAge(0));
					// By default, getPickStack calls the protected function getSeedsItem
					// So let's hope a mod doesn't change that...
					Item seed = cropBlock.getPickStack(world, hitResult.getBlockPos(), blockState).getItem();
					AtomicBoolean removedSeed = new AtomicBoolean(false);

					Block.getDroppedStacks(blockState, (ServerWorld) world, hitResult.getBlockPos(), null, player, handStack).forEach(stack -> {
						if (!removedSeed.get() && stack.getItem() == seed) {
							// It's PROBABLY a seed, so take one away
							removedSeed.set(true);
							stack.decrement(1);
						}
						Block.dropStack(world, hitResult.getBlockPos(), stack);
					});
				}

				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}
}
