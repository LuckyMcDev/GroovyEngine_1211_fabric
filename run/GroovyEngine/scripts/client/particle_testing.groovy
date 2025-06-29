
// Tick Events
TickEvents.onStartClientTick { ctx ->
    //Logger.info("TickEvents: Start Client Tick - Event: ${ctx.event}")

    def world = ctx.client.world;
    def player = ctx.client.player;
    def particleManager = ctx.client.particleManager

    if (world != null && player != null) {

        def vx = Math.random() * 10
        def vy = Math.random() * 10
        def vz = Math.random() * 10
        Logger.info("Velocity: $vx, $vy, $vz")

        ParticleBuilder.create(world, ctx.client.particleManager)
                .setType(GroovyParticleTypes.BASE)
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

    }
}