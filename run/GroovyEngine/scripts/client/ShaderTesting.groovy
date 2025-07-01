//priority=2

Logger.info("This is a log line from the ingame editor")

// Standardized ShaderBuilder usage
def blitShader = ShaderManager.register("blit")
        .path("groovyengine:shaders/post/blit.json")
        .build() // Finalize and register

// Enable if needed
// blitShader.enable()