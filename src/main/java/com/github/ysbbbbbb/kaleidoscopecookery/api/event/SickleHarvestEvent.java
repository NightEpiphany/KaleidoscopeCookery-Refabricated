package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.util.event.CancellableKCEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class SickleHarvestEvent extends CancellableKCEvent {
    private final ItemStack sickle;
    private final BlockPos harvestPos;
    private final BlockState harvestState;
    private boolean costDurability = false;
    private final Player player;

    public SickleHarvestEvent(Player player, ItemStack sickle, BlockPos harvestPos, BlockState harvestState) {
        this.player = player;
        this.sickle = sickle;
        this.harvestPos = harvestPos;
        this.harvestState = harvestState;
    }

    public ItemStack getSickle() {
        return sickle;
    }

    public BlockPos getHarvestPos() {
        return harvestPos;
    }

    public BlockState getHarvestState() {
        return harvestState;
    }

    public boolean isCostDurability() {
        return costDurability;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * 此次收割是否消耗镰刀耐久度，默认不消耗
     */
    public void setCostDurability(boolean costDurability) {
        this.costDurability = costDurability;
    }
}
