package com.sunekaer.toolkit.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

public class Handler {

    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), SetCopy.TYPE, SetCopy.CODEC, SetCopy::handle);
        } else {
            NetworkManager.registerS2CPayloadType(SetCopy.TYPE, SetCopy.CODEC);
        }
    }
}
