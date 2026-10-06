package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import com.github.ysbbbbbb.kaleidoscopecookery.util.SitUtil;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PortFixGameTests {
    @GameTest
    public void longBenchSitsAndCleansUp(GameTestHelper helper) {
        BlockPos seat = new BlockPos(2, 1, 2);
        BlockPos absoluteSeat = helper.absolutePos(seat);
        helper.setBlock(seat, ModBlocks.LONG_BENCH);

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(absoluteSeat.getX() + 0.5, absoluteSeat.getY() + 1, absoluteSeat.getZ() + 0.5);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absoluteSeat.above()), Direction.UP, absoluteSeat, false);
        var result = UseBlockCallback.EVENT.invoker().interact(player, helper.getLevel(), InteractionHand.MAIN_HAND, hit);

        helper.assertTrue(result.consumesAction(), "Long bench interaction should seat the player");
        SitEntity seatEntity = SitUtil.getSitEntity(helper.getLevel(), absoluteSeat);
        helper.assertTrue(seatEntity != null && seatEntity.hasPassenger(player), "Seat entity should track the passenger");

        player.stopRiding();
        helper.assertFalse(SitUtil.isOccupied(helper.getLevel(), absoluteSeat), "Dismount should clear the occupied seat");
        helper.succeed();
    }

    @GameTest
    public void riceBowlUsesLoadedCookedRiceTag(GameTestHelper helper) {
        ItemStack rice = new ItemStack(ModItems.COOKED_RICE);
        helper.assertTrue(rice.is(TagCommon.COOKED_RICE), "Cooked rice should be present in its loaded tag");

        RiceBowlRecipe recipe = new RiceBowlRecipe(CraftingBookCategory.MISC, Ingredient.of(Items.BEEF),
                ItemStackTemplate.fromNonEmptyStack(new ItemStack(Items.BREAD)));
        CraftingInput input = CraftingInput.of(2, 1, List.of(rice, new ItemStack(Items.BEEF)));
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Tagged rice should match the recipe");
        helper.assertTrue(recipe.getIngredients(helper.getLevel().registryAccess()).get(1).test(rice),
                "Displayed recipe ingredient should use the loaded tag");
        helper.assertTrue(recipe.getRemainingItems(input).get(0).is(Items.BOWL), "Rice should return its bowl");
        helper.succeed();
    }

    @GameTest
    public void potSoupReturnsFlowerPot(GameTestHelper helper) {
        Item soup = FoodBiteRegistry.FOOD_ITEM_MAP.get(FoodBiteRegistry.BROWN_MUSHROOM_POT_SOUP);
        helper.assertTrue(soup != null, "Pot soup item should be registered");

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack remainder = new ItemStack(soup).finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(remainder.is(Items.FLOWER_POT), "Pot soup should return a flower pot");
        helper.assertFalse(player.getInventory().contains(new ItemStack(Items.FLOWER_POT)),
                "Pot soup should not grant a duplicate flower pot");
        helper.succeed();
    }
}
