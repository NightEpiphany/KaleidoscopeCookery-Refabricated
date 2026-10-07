package com.github.ysbbbbbb.kaleidoscopecookery.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class CookingParticle extends SingleQuadParticle {
    private final SpriteSet sprites;

    protected CookingParticle(ClientLevel level, double pX, double pY, double pZ, SpriteSet sprites, TextureAtlasSprite textureAtlasSprite) {
        super(level, pX, pY, pZ, textureAtlasSprite);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;
        this.quadSize *= 0.75f;
        this.lifetime = 50;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Math.clamp((this.age + scaleFactor) / this.lifetime * 32.0F, 0, 1);
    }

    @Override
    protected @NonNull Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        @Override
        public @Nullable Particle createParticle(@NonNull SimpleParticleType particleOptions, @NonNull ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, @NonNull RandomSource randomSource) {
            var particle =  new CookingParticle(clientLevel, d, e, f, this.sprites, this.sprites.get(randomSource));
            particle.setAlpha(0.98865F);
            return particle;
        }
    }
}
