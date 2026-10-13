package com.github.ysbbbbbb.kaleidoscopecookery.compat.trinkets.aglets.arch;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModArmorMaterials;
import com.github.ysbbbbbb.kaleidoscopecookery.item.StrawHatItem;
import eu.pb4.trinkets.api.*;
import eu.pb4.trinkets.impl.slots.TrinketSlot;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public final class StrawHatTrinketItem extends StrawHatItem implements Trinket {
    public StrawHatTrinketItem(boolean hasFlower, Properties properties) {
        super(hasFlower, properties.stacksTo(1).humanoidArmor(ModArmorMaterials.FARMER, ArmorType.HELMET));
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (equipItem(player, stack)) {
            return InteractionResult.Success.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    public static boolean equipItem(Player user, ItemStack stack) {
        return equipItem((LivingEntity) user, stack);
    }

    public static boolean equipItem(LivingEntity user, ItemStack stack) {
        Optional<TrinketAttachment> optional = Optional.ofNullable(TrinketsApi.getAttachment(user));
        if (optional.isPresent()) {
            TrinketAttachment comp = optional.get();
            for (TrinketInventory inv : comp.getInventories().values()) {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (inv.getItem(i).isEmpty()) {
                        TrinketSlotAccess ref = new TrinketSlotAccess(inv, i);
                        if (TrinketSlot.canInsert(stack, ref, user)) {
                            ItemStack newStack = stack.copy();
                            inv.setItem(i, newStack);
                            Holder<SoundEvent> soundEvent = Trinket.getEquipSound(stack);
                            if (!stack.isEmpty() && soundEvent != null) {
                                user.gameEvent(GameEvent.EQUIP);
                                user.playSound(soundEvent.value(), 1.0F, 1.0F);
                            }
                            stack.setCount(0);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
