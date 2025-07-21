/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.utils.network;

public class OnlinePlayers {
    private static long lastPingTime;

    private OnlinePlayers() {
    }

    public static void update() {
        long time = System.currentTimeMillis();

        if (time - lastPingTime > 5 * 60 * 1000) {
 ogmq5k-codex/rename-meteor-files-to-cookie
            

            MeteorExecutor.execute(() -> Http.post("https://cookieclient.com/api/online/ping").ignoreExceptions().send());
 master

            lastPingTime = time;
        }
    }

    public static void leave() {
 ogmq5k-codex/rename-meteor-files-to-cookie
        CookieExecutor.execute(() -> Http.post("https://meteorclient.com/api/online/leave").ignoreExceptions().send());

       
 master
    }
}
