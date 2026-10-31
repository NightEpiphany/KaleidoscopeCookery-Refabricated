package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.PlayerLeftClickEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.ThrowBaoziMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LeftClickEvent {
    //肉包打狗，AUV，地道！
    public static void onHandle(PlayerLeftClickEvent event) {
        Player player = event.player();
        InteractionHand hand = event.hand();
        if (player == null) return;
        ItemStack mainHandItem = player.getMainHandItem();
        if (player.isSecondaryUseActive()
                && hand == InteractionHand.MAIN_HAND
                && mainHandItem.is(ModItems.BAOZI)
                && ClientPlayNetworking.canSend(ThrowBaoziMessage.TYPE)
        ) {
            ClientPlayNetworking.send(new ThrowBaoziMessage());
        }
        if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND
                && mainHandItem.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
            // 摆动动作时，取出物品
            if (TransmutationLunchBagItem.dropContents(mainHandItem, player)
            && mainHandItem.getItem() instanceof TransmutationLunchBagItem t) {
                t.playDropContentsSound(player);
            }
        }
        if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND
                && mainHandItem.is(ModItems.TEAPOT)) {
            TeapotItem.clearAll(mainHandItem, player);
        }
    }
}
