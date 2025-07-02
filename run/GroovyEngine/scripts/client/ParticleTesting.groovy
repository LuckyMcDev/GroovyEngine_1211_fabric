//disabled
TickEvents.onStartClientTick { ctx ->
    def world = ctx.client.world
    def player = ctx.client.player
    def particleManager = ctx.client.particleManager

    if (world != null && player != null) {
        def vx = Math.random()
        def vy = Math.random()
        def vz = Math.random()

        // Standardized ParticleBuilder usage
        ParticleBuilder.register(world, particleManager)
                .setType(GroovyParticleTypes.COLORED)
                .setPosition(player.pos)
                .setVelocity(vx, vy, vz)
                .setVelocitySpread(1.0f)
                .setColor(1.0f, 1.0f, 0.0f)
                .setScale(1.0f)
                .setCount(10)
                .build() // Changed from spawn() to build()

        // Random particle example
        ParticleBuilder.register(world, particleManager)
                .setType(GroovyParticleTypes.RANDOM_SIMPLEPARTICLE())
                .setPosition(player.pos)
                .setVelocity(vx, vy, vz)
                .setVelocitySpread(1.0f)
                .setCount(10)
                .build()
    }
}