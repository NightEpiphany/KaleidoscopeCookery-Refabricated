package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class CandiedSkewerItem extends WithTooltipsBlockItem implements IHasContainer {
    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "candied_skewer_knockback");
    private static final ItemAttributeModifiers ATTRIBUTES = ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_KNOCKBACK, new AttributeModifier(KNOCKBACK_ID, 3, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.MAINHAND)
            .build();

    private final Block sideBlock;
    private final @Nullable Holder<MobEffect> attackEffect;
    private final int attackEffectDuration;

    private final boolean randomTeleport;

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, FoodProperties food) {
        this(name, uprightBlock, sideBlock, food, null, 0, false);
    }

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, FoodProperties food, @Nullable Holder<MobEffect> attackEffect, int attackEffectDuration) {
        this(name, uprightBlock, sideBlock, food, attackEffect, attackEffectDuration, false);
    }

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, FoodProperties food, boolean randomTeleport) {
        this(name, uprightBlock, sideBlock, food, null, 0, randomTeleport);
    }

    public CandiedSkewerItem(String name, Block uprightBlock, Block sideBlock, FoodProperties food,
                             @Nullable Holder<MobEffect> attackEffect, int attackEffectDuration, boolean randomTeleport) {
        super(uprightBlock, new Properties().food(food).attributes(ATTRIBUTES), name);
        this.randomTeleport = randomTeleport;
        this.sideBlock = sideBlock;
        this.attackEffect = attackEffect;
        this.attackEffectDuration = attackEffectDuration;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        return player == null || player.isSecondaryUseActive() ? super.useOn(context) : InteractionResult.PASS;
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        if (face == Direction.UP) {
            return super.getPlacementState(context);
        }
        if (face.getAxis().isHorizontal()) {
            BlockState state = this.sideBlock.getStateForPlacement(context);
            return state != null && this.canPlace(context, state) ? state : null;
        }
        return null;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockItems, Item item) {
        super.registerBlocks(blockItems, item);
        blockItems.put(this.sideBlock, item);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getKey()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this.attackEffect != null && !target.level().isClientSide) {
            target.addEffect(new MobEffectInstance(this.attackEffect, this.attackEffectDuration), attacker);
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack remaining = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && this.randomTeleport) {
            for(int i = 0; i < 16; ++i) {
                double d = entity.getX() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                double e = Mth.clamp(entity.getY() + (double)(entity.getRandom().nextInt(16) - 8), level.getMinBuildHeight(), level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1);
                double f = entity.getZ() + (entity.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                Vec3 vec3 = entity.position();
                if (entity.randomTeleport(d, e, f, true)) {
                    level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entity));
                    SoundSource soundSource;
                    SoundEvent soundEvent;
                    if (entity instanceof Fox) {
                        soundEvent = SoundEvents.FOX_TELEPORT;
                        soundSource = SoundSource.NEUTRAL;
                    } else {
                        soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundSource = SoundSource.PLAYERS;
                    }

                    level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, soundSource);
                    entity.resetFallDistance();
                    break;
                }
            }

            if (entity instanceof Player player) {
                player.resetCurrentImpulseContext();
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return level.isClientSide || entity.hasInfiniteMaterials() ? remaining : this.returnContainerToEntity(remaining, level, entity);
    }

    @Override
    public Item getContainerItem() {
        return Items.STICK;
    }
}
