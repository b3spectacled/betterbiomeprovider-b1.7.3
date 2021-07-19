package com.bespectacled.betterbiomeprovider;

import java.io.IOException;

import io.github.minecraftcursedlegacy.api.config.Configs;
import io.github.minecraftcursedlegacy.api.registry.Id;
import net.fabricmc.api.ModInitializer;
import tk.valoeghese.zoesteriaconfig.api.container.WritableConfig;
import tk.valoeghese.zoesteriaconfig.api.template.ConfigTemplate;

public class BetterBiomeProvider implements ModInitializer {
    public static final String MOD_ID = "betterbiomeprovider";
    public static final String MOD_NAME = "BetterBiomeProvider";
    
    private static final WritableConfig CONFIG;
    
    public static boolean isEnabled() {
        return CONFIG.getBooleanValue("settings.enabled");
    }
    
    public static void log(String message) {
        System.out.println(String.format("[%s] %s", MOD_NAME, message));
    }
    
    @Override
    public void onInitialize() {
        log("Initializing..");
        log("Enabled? " + isEnabled());
    }
    
    static {
        try {
            CONFIG = Configs.loadOrCreate(new Id(MOD_ID, "settings"),
                    ConfigTemplate.builder()
                    .addContainer("settings", container -> container.addDataEntry("enabled", "true"))
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
    }
}