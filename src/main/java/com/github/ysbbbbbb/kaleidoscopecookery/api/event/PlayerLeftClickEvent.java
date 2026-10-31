package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.IKCEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record PlayerLeftClickEvent(@Nullable Player player, InteractionHand hand) implements IKCEvent {
}
