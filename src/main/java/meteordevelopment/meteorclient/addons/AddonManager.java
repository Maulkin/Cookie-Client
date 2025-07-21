/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.addons;

import meteordevelopment.meteorclient.CookieClient;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
import java.util.List;

public class AddonManager {
    public static final List<CookieAddon> ADDONS = new ArrayList<>();

    public static void init() {
        // Meteor pseudo addon
        {
            CookieClient.ADDON = new CookieAddon() {
                @Override
                public void onInitialize() {}

                @Override
                public String getPackage() {
                    return "meteordevelopment.meteorclient";
                }

                @Override
                public String getWebsite() {
                    return "https://meteorclient.com";
                }

                @Override
                public GithubRepo getRepo() {
                    return new GithubRepo("MeteorDevelopment", "cookie-client");
                }

                @Override
                public String getCommit() {
                    String commit = CookieClient.MOD_META.getCustomValue(CookieClient.MOD_ID + ":commit").getAsString();
                    return commit.isEmpty() ? null : commit;
                }
            };

            ModMetadata metadata = FabricLoader.getInstance().getModContainer(CookieClient.MOD_ID).get().getMetadata();

            CookieClient.ADDON.name = metadata.getName();
            CookieClient.ADDON.authors = new String[metadata.getAuthors().size()];
            if (metadata.containsCustomValue(CookieClient.MOD_ID + ":color")) {
                CookieClient.ADDON.color.parse(metadata.getCustomValue(CookieClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                CookieClient.ADDON.authors[i++] = author.getName();
            }

            ADDONS.add(CookieClient.ADDON);
        }

        // Addons
        for (EntrypointContainer<CookieAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("meteor", CookieAddon.class)) {
            ModMetadata metadata = entrypoint.getProvider().getMetadata();
            CookieAddon addon;
            try {
                addon = entrypoint.getEntrypoint();
            } catch (Throwable throwable) {
                throw new RuntimeException("Exception during addon init \"%s\".".formatted(metadata.getName()), throwable);
            }

            addon.name = metadata.getName();

            if (metadata.getAuthors().isEmpty()) throw new RuntimeException("Addon \"%s\" requires at least 1 author to be defined in it's fabric.mod.json. See https://fabricmc.net/wiki/documentation:fabric_mod_json_spec".formatted(addon.name));
            addon.authors = new String[metadata.getAuthors().size()];

            if (metadata.containsCustomValue(CookieClient.MOD_ID + ":color")) {
                addon.color.parse(metadata.getCustomValue(CookieClient.MOD_ID + ":color").getAsString());
            }

            int i = 0;
            for (Person author : metadata.getAuthors()) {
                addon.authors[i++] = author.getName();
            }

            ADDONS.add(addon);
        }
    }
}
