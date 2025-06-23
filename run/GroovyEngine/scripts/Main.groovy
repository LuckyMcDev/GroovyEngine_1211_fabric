package scripts

import io.github.luckymcdev.groovyengine.scripting.core.GroovyEngineInitializer

println this.binding.variables.keySet()

Logger.info("This is now also working, as the bindings are not fucked anymore")

class MainInitializer extends GroovyEngineInitializer {
    @Override
    void onInitialize() {
        println("This is now Working?")
    }

}

return new MainInitializer()

