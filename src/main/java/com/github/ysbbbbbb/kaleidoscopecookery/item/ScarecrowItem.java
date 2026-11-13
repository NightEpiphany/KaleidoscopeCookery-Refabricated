package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.critereon.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PostSpawnProcessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ScarecrowItem extends WithTooltipsItem {
    public ScarecrowItem(Properties p) {
        super(p, "scarecrow");
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos clickedPos = placeContext.getClickedPos();
        ItemStack stack = context.getItemInHand();
        Vec3 center = Vec3.atBottomCenterOf(clickedPos);
        AABB aabb = ModEntities.SCARECROW.getDimensions().makeBoundingBox(center.x(), center.y(), center.z());
        if (level.noCollision(null, aabb) && level.getEntities(null, aabb).isEmpty()) {
            if (level instanceof ServerLevel serverLevel) {
                PostSpawnProcessor<ScarecrowEntity> consumer = EntityType.createDefaultStackConfig(serverLevel, stack, context.getPlayer());
                ScarecrowEntity scarecrow = ModEntities.SCARECROW.create(serverLevel, consumer, clickedPos, EntitySpawnReason.SPAWN_ITEM_USE, true, true);
                if (scarecrow == null) {
                    return InteractionResult.FAIL;
                }
                float rotation = Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                scarecrow.setPosRaw(scarecrow.getX(), scarecrow.getY(), scarecrow.getZ());
                scarecrow.setYRot(rotation);
                scarecrow.setXRot(0);
                serverLevel.addFreshEntityWithPassengers(scarecrow);
                level.playSound(null, scarecrow.getX(), scarecrow.getY(), scarecrow.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
                scarecrow.gameEvent(GameEvent.ENTITY_PLACE, context.getPlayer());
                ModTrigger.EVENT.trigger(context.getPlayer(), ModEventTriggerType.PLACE_SCARECROW);
            }
            stack.consume(1, placeContext.getPlayer());
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }
}
