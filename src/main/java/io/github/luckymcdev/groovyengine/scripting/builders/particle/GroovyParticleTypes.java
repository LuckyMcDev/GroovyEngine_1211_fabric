package io.github.luckymcdev.groovyengine.scripting.builders.particle;

import java.util.Random;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public class GroovyParticleTypes {


    // Dust (colored) particle — supports color & scale
    public static final ParticleType<DustParticleEffect> COLORED = ParticleTypes.DUST;

    // Simple particles without extra data
    public static final ParticleType<SimpleParticleType> ASH = ParticleTypes.ASH;
    public static final ParticleType<SimpleParticleType> WHITE_ASH = ParticleTypes.WHITE_ASH;
    public static final ParticleType<SimpleParticleType> BUBBLE = ParticleTypes.BUBBLE;
    public static final ParticleType<SimpleParticleType> CLOUD = ParticleTypes.CLOUD;
    public static final ParticleType<SimpleParticleType> CRIT = ParticleTypes.CRIT;
    public static final ParticleType<SimpleParticleType> CRIMSON_SPORE = ParticleTypes.CRIMSON_SPORE;
    public static final ParticleType<SimpleParticleType> DRIPPING_WATER = ParticleTypes.DRIPPING_WATER;
    public static final ParticleType<SimpleParticleType> DRIPPING_LAVA = ParticleTypes.DRIPPING_LAVA;
    public static final ParticleType<SimpleParticleType> FALLING_WATER = ParticleTypes.FALLING_WATER;
    public static final ParticleType<SimpleParticleType> FALLING_LAVA = ParticleTypes.FALLING_LAVA;
    public static final ParticleType<SimpleParticleType> FIREWORK = ParticleTypes.FIREWORK;
    public static final ParticleType<SimpleParticleType> FLAME = ParticleTypes.FLAME;
    public static final ParticleType<SimpleParticleType> HEART = ParticleTypes.HEART;
    public static final ParticleType<SimpleParticleType> SOUL = ParticleTypes.SOUL;
    public static final ParticleType<SimpleParticleType> SNOWFLAKE = ParticleTypes.SNOWFLAKE;
    public static final ParticleType<SimpleParticleType> TOTEM_OF_UNDYING = ParticleTypes.TOTEM_OF_UNDYING;
    public static final ParticleType<SimpleParticleType> SPIT = ParticleTypes.SPIT;

    // Block and item particles
    public static final ParticleType<BlockStateParticleEffect> BLOCK = ParticleTypes.BLOCK;
    public static final ParticleType<ItemStackParticleEffect> ITEM = ParticleTypes.ITEM;

    // Water and lava splash
    public static final ParticleType<SimpleParticleType> SPLASH = ParticleTypes.SPLASH;

    // Portal particle
    public static final ParticleType<SimpleParticleType> PORTAL = ParticleTypes.PORTAL;

    // Smoke
    public static final ParticleType<SimpleParticleType> SMOKE = ParticleTypes.SMOKE;


    private static final ParticleType<?>[] SIMPLE_PARTICLES = new ParticleType<?>[]{
            ASH, WHITE_ASH, BUBBLE, CLOUD, CRIT, CRIMSON_SPORE,
            DRIPPING_WATER, DRIPPING_LAVA, FALLING_WATER, FALLING_LAVA,
            FIREWORK, FLAME, HEART, SOUL, SNOWFLAKE,
            TOTEM_OF_UNDYING, SPIT, SPLASH, PORTAL, SMOKE
    };

    private static final Random RANDOM = new Random();

    /**
     * Returns a random SimpleParticleType from the predefined list.
     * Use with casting to ParticleType<SimpleParticleType>.
     */
    @SuppressWarnings("unchecked")
    public static ParticleType<SimpleParticleType> RANDOM_SIMPLEPARTICLE() {
        return (ParticleType<SimpleParticleType>) SIMPLE_PARTICLES[RANDOM.nextInt(SIMPLE_PARTICLES.length)];
    }
}
