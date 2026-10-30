package com.github.ysbbbbbb.kaleidoscopecookery.util.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

//森罗厨房相关的各类事件专用的接口
public interface IKCEvent {

    /**
     * 注册核心
     */
    Event<Callback<IKCEvent>> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback<IKCEvent> callback : callbacks) {
            callback.post(event);
        }
    });

    default void post() {
        CALLBACK.invoker().post(this);
    }

    interface Callback<T extends IKCEvent> {
        void post(T event);
    }
}
