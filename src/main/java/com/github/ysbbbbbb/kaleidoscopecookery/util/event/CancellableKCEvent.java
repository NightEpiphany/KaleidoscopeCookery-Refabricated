package com.github.ysbbbbbb.kaleidoscopecookery.util.event;

// 森罗厨房相关的各类可取消事件专用的类
public abstract class CancellableKCEvent implements IKCEvent {
    boolean isCanceled;

    public final void setCanceledState(boolean canceled) {
        this.isCanceled = canceled;
    }

    public final boolean isCanceledState() {
        return this.isCanceled;
    }
}
