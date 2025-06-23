package scripts.client



Logger.info("This is a log line from the ingame editor")

def blitShader = ShaderBuilder.create("groovyengine:blit")
        .path("groovyengine:shaders/post/blit.json")

blitShader.build();

ShaderRegistry.register("Crt Shader", blitShader);

