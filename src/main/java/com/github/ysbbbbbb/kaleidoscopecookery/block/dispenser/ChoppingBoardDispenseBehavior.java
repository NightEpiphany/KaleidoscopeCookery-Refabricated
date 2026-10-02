package com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class ChoppingBoardDispenseBehavior extends OptionalDispenseItemBehavior {
    private final boolean knife;

    public ChoppingBoardDispenseBehavior(boolean knife) {
        this.knife = knife;
    }

    @Override
    protected @NotNull ItemStack execute(BlockSource source, @NonNull ItemStack stack) {
        this.setSuccess(false);
        Direction facing = source.state().getValue(DispenserBlock.FACING);
        BlockPos boardPos = source.pos().relative(facing);
        if (source.level().getBlockEntity(boardPos) instanceof IChoppingBoard board) {
            // onPutItem consumes the ingredient itself.
            this.setSuccess(this.knife
                    ? board.onCutItem(source.level(), null, stack)
                    : board.onPutItem(source.level(), null, stack));
            if (this.knife && board.hasItemOnBoard())
                if (stack.getDamageValue() >= stack.getMaxDamage())
                    stack.shrink(1);
                else
                    stack.setDamageValue(stack.getDamageValue() + 1);
        }
        return stack;
    }

    public static void register() {
        ChoppingBoardDispenseBehavior knifeBehavior = new ChoppingBoardDispenseBehavior(true);
        DispenserBlock.registerBehavior(ModItems.IRON_KITCHEN_KNIFE, knifeBehavior);
        DispenserBlock.registerBehavior(ModItems.COPPER_KITCHEN_KNIFE, knifeBehavior);
        DispenserBlock.registerBehavior(ModItems.GOLD_KITCHEN_KNIFE, knifeBehavior);
        DispenserBlock.registerBehavior(ModItems.DIAMOND_KITCHEN_KNIFE, knifeBehavior);
        DispenserBlock.registerBehavior(ModItems.NETHERITE_KITCHEN_KNIFE, knifeBehavior);

        ChoppingBoardDispenseBehavior foodBehavior = new ChoppingBoardDispenseBehavior(false);
        Item[] ingredients = {
                Items.TROPICAL_FISH, Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON,
                Items.MUTTON, Items.COOKED_MUTTON, Items.BEEF, Items.COOKED_BEEF,
                Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.CHICKEN, Items.COOKED_CHICKEN,
                Items.RABBIT, Items.COOKED_RABBIT, ModItems.RAW_DOUGH
        };
        for (Item ingredient : ingredients) {
            DispenserBlock.registerBehavior(ingredient, foodBehavior);
        }
        KaleidoscopeCookery.LOGGER.debug(
                "Registered chopping board dispenser support for four knives and {} ingredients", ingredients.length);
    }
}
