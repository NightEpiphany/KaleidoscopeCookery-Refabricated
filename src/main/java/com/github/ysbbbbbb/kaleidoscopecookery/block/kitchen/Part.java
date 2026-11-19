package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String getSerializedName() {
        return name;
    }
}
