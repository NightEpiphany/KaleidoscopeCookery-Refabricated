package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("all")
public final class ModAttachmentType {
    public static final AttachmentType<Vec3> FLATULENCE_EFFECT_STARTING_POSITION = AttachmentRegistry.createDefaulted(
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flatulence_effect_starting_position"),
            () -> Vec3.ZERO);
    public static final AttachmentType<Integer> END_SOUL_CRYSTAL_ID = AttachmentRegistry.<Integer>builder()
            .syncWith(ByteBufCodecs.VAR_INT, AttachmentSyncPredicate.all())
            .buildAndRegister(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "end_soul_crystal_id"));

    public static void registerAttachments() {
    }
}
