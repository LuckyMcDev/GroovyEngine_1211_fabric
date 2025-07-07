package io.github.luckymcdev.groovyengine;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class GroovyEnginePreLoad implements PreLaunchEntrypoint {
    /**
     * Runs the entrypoint.
     */
    @Override
    public void onPreLaunch() {
        GroovyEngine.LOGGER.debug("Hello, prelaunch");
    }


}
