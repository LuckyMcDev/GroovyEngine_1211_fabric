package io.github.luckymcdev.groovyengine.scripting.builders.particle;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class ParticleBuilder {
    private static final Map<String, ParticleBuilder> REGISTERED = new HashMap<>();

    public static ParticleBuilder create(World world, ParticleManager particleManager) {
        return new ParticleBuilder(world, particleManager);
    }

    public static ParticleBuilder get(String name, World world, ParticleManager mgr) {
        ParticleBuilder original = REGISTERED.get(name);
        if (original == null) return null;
        return original.copy(world, mgr);
    }

    public ParticleBuilder saveAs(String name) {
        REGISTERED.put(name, this.copy());
        return this;
    }

    // Instance fields
    private final World world;
    private final ParticleManager particleManager;

    private ParticleType<DustParticleEffect> type;
    private Vec3d position = Vec3d.ZERO;
    private Vec3d velocity = Vec3d.ZERO;
    private float red = 1.0f, green = 1.0f, blue = 1.0f, scale = 1.0f;
    private int count = 1;
    private int lifetime = 20;
    private float velocitySpread = 0.0f;

    private ParticleBuilder(World world, ParticleManager particleManager) {
        this.world = world;
        this.particleManager = particleManager;
    }

    // Fluent setters
    public ParticleBuilder setType(ParticleType<DustParticleEffect> type) {
        this.type = type;
        return this;
    }

    public ParticleBuilder setPosition(double x, double y, double z) {
        this.position = new Vec3d(x, y, z);
        return this;
    }

    public ParticleBuilder setPosition(Vec3d pos) {
        this.position = pos;
        return this;
    }

    public ParticleBuilder setVelocity(double vx, double vy, double vz) {
        this.velocity = new Vec3d(vx, vy, vz);
        return this;
    }

    public ParticleBuilder setVelocitySpread(float spread) {
        this.velocitySpread = spread;
        return this;
    }

    public ParticleBuilder setVelocity(Vec3d vel) {
        this.velocity = vel;
        return this;
    }

    public ParticleBuilder setColor(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    public ParticleBuilder setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public ParticleBuilder setLifetime(int lifetime) {
        this.lifetime = lifetime;
        return this;
    }

    public ParticleBuilder setCount(int count) {
        this.count = count;
        return this;
    }

    public void spawn() {
        if (type == null) {
            throw new IllegalStateException("Particle type not set");
        }

        DustParticleEffect effect = new DustParticleEffect(new Vector3f(red, green, blue), scale);

        for (int i = 0; i < count; i++) {
            double dx = velocity.x + (Math.random() - 0.5) * velocitySpread;
            double dy = velocity.y + (Math.random() - 0.5) * velocitySpread;
            double dz = velocity.z + (Math.random() - 0.5) * velocitySpread;

            world.addParticle(effect, position.x, position.y, position.z, dx, dy, dz);
        }
    }


    private ParticleBuilder copy(World world, ParticleManager mgr) {
        ParticleBuilder b = new ParticleBuilder(world, mgr);
        b.type = this.type;
        b.position = this.position;
        b.velocity = this.velocity;
        b.red = this.red;
        b.green = this.green;
        b.blue = this.blue;
        b.scale = this.scale;
        b.count = this.count;
        b.lifetime = this.lifetime;
        return b;
    }

    private ParticleBuilder copy() {
        return copy(this.world, this.particleManager);
    }
}
