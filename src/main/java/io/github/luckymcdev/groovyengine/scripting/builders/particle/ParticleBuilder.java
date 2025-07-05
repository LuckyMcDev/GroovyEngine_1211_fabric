package io.github.luckymcdev.groovyengine.scripting.builders.particle;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

/**
 * Builder for creating and spawning particle effects.
 */
public class ParticleBuilder {
    private final Level world;
    private final ParticleEngine particleManager;
    private ParticleType<?> type;
    private ParticleOptions particleData;
    private Vec3 position = Vec3.ZERO;
    private Vec3 velocity = Vec3.ZERO;
    private float red = 1.0f, green = 1.0f, blue = 1.0f, scale = 1.0f;
    private int count = 1;
    private int lifetime = 20;
    private float velocitySpread = 0.0f;

    private ParticleBuilder(Level world, ParticleEngine particleManager) {
        this.world = world;
        this.particleManager = particleManager;
    }

    /**
     * Creates a new ParticleBuilder instance.
     */
    public static ParticleBuilder register(Level world, ParticleEngine particleManager) {
        return new ParticleBuilder(world, particleManager);
    }

    /**
     * Sets the particle type.
     */
    public ParticleBuilder setType(ParticleType<?> type) {
        this.type = type;
        return this;
    }

    /**
     * Sets custom particle effect data.
     */
    public ParticleBuilder setParticleData(ParticleOptions data) {
        this.particleData = data;
        return this;
    }

    /**
     * Sets the particle position. Using 3 doubles
     */
    public ParticleBuilder setPosition(double x, double y, double z) {
        this.position = new Vec3(x, y, z);
        return this;
    }

    /**
     * Sets the particle position. Using a Vector
     */
    public ParticleBuilder setPosition(Vec3 position) {
        this.position = position;
        return this;
    }

    /**
     * Sets the particle velocity.
     */
    public ParticleBuilder setVelocity(double vx, double vy, double vz) {
        this.velocity = new Vec3(vx, vy, vz);
        return this;
    }

    /**
     * Sets the velocity spread.
     */
    public ParticleBuilder setVelocitySpread(float spread) {
        this.velocitySpread = spread;
        return this;
    }

    /**
     * Sets the particle color.
     */
    public ParticleBuilder setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    /**
     * Sets the particle scale.
     */
    public ParticleBuilder setScale(float scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Sets the particle lifetime.
     */
    public ParticleBuilder setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    /**
     * Sets the particle count.
     */
    public ParticleBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    /**
     * Spawns the particle effect (equivalent to build()).
     */
    public void build() {
        if (type == null) throw new IllegalStateException("Particle type not set");

        ParticleOptions effectData = this.particleData;
        if (effectData == null && type == ParticleTypes.DUST) {
            effectData = new DustParticleOptions(new Vector3f(red, green, blue), scale);
        }

        for (int i = 0; i < count; i++) {
            double dx = velocity.x + (Math.random() - 0.5) * velocitySpread;
            double dy = velocity.y + (Math.random() - 0.5) * velocitySpread;
            double dz = velocity.z + (Math.random() - 0.5) * velocitySpread;

            if (effectData != null) {
                world.addParticle(effectData, position.x, position.y, position.z, dx, dy, dz);
            } else {
                world.addParticle((ParticleOptions) type, position.x, position.y, position.z, dx, dy, dz);
            }
        }
    }
}