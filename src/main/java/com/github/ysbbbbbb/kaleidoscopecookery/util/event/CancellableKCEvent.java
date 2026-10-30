package com.github.ysbbbbbb.kaleidoscopecookery.util.event;

import org.jetbrains.annotations.ApiStatus;

// 森罗厨房相关的各类可取消事件专用的类
public abstract class CancellableKCEvent implements IKCEvent {

    private boolean canceled;

    public final void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @ApiStatus.NonExtendable
    public boolean isCanceled() {
        return this.canceled;
    }
}
