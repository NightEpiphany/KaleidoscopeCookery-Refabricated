package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum Part implements StringRepresentable {
    SINGLE("single"),
    LEFT("left"),
    MIDDLE("middle"),
    RIGHT("right");

    private final String name;

    Part(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return name;
    }
}
