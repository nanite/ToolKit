package com.sunekaer.mods.toolkit.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    private static CommonConfig instance;
    private final ForgeConfigSpec.Builder builder;

    public static ForgeConfigSpec.BooleanValue messageOnJoin;
    public static ForgeConfigSpec.ConfigValue<String> joinMessage;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        instance = this;

        this.builder = builder;
        builder.comment("ToolKit common config");

        playerEvent();
    }

    public static CommonConfig get()
    {
        return instance;
    }

    private void playerEvent() {
        builder.push("playerEvent");
        messageOnJoin = builder
                .comment("Send messages to users on join?")
                .define("message_on_join",true);

        joinMessage = builder
                .comment("if messageOnJoin is true this message will be send to the player on joining world")
                .define("message", "§5Hello from ToolKit, this message can be change or disabled in config.");

        builder.pop();
    }
}
