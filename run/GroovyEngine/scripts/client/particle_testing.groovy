
// Tick Events
TickEvents.onStartClientTick { ctx ->
    //Logger.info("TickEvents: Start Client Tick - Event: ${ctx.event}")

    def world = ctx.client.world;
    def player = ctx.client.player;
    def particleManager = ctx.client.particleManager

    if (world != null && player != null) {

        def vx  = Math.random()
        def vy = Math.random()
        def vz = Math.random()

        ParticleBuilder.create(world, ctx.client.particleManager)
                .setType(GroovyParticleTypes.COLORED)
                .setPosition(player.getPos())
                .setVelocity(vx, vy, vz)
                .setVelocitySpread(1.0f)
                .setColor(1.0f, 1.0f, 0.0f)
                .setScale(1.0f)
                .setCount(10)
                .saveAs("test_particle")


        ParticleBuilder.get("test_particle", world, particleManager)
                .setPosition(0, 70, 0)
                .spawn()

        ParticleBuilder.create(world, ctx.client.particleManager)
                .setType(GroovyParticleTypes.RANDOM_SIMPLEPARTICLE())
                .setPosition(player.getPos())
                .setVelocity(vx, vy, vz)
                .setVelocitySpread(1.0f)
                .setColor(1.0f, 1.0f, 0.0f)
                .setScale(1.0f)
                .setCount(10)
                .spawn()

    }
}