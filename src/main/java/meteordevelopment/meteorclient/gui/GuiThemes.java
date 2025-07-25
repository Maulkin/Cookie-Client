/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.gui;

import meteordevelopment.meteorclient.CookieClient;
import meteordevelopment.meteorclient.gui.themes.meteor.CookieGuiTheme;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.PreInit;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiThemes {
    private static final File FOLDER = new File(CookieClient.FOLDER, "gui");
    private static final File THEMES_FOLDER = new File(FOLDER, "themes");
    private static final File FILE = new File(FOLDER, "gui.nbt");

    private static final List<GuiTheme> themes = new ArrayList<>();
    private static GuiTheme theme;

    private GuiThemes() {
    }

    @PreInit
    public static void init() {
        add(new CookieGuiTheme());
    }

    @PostInit
    public static void postInit() {
        if (FILE.exists()) {
            try {
                NbtCompound tag = NbtIo.read(FILE.toPath());

                if (tag != null) select(tag.getString("currentTheme", ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (theme == null) select("Cookie");
    }

    public static void add(GuiTheme theme) {
        for (Iterator<GuiTheme> it = themes.iterator(); it.hasNext();) {
            if (it.next().name.equals(theme.name)) {
                it.remove();

                CookieClient.LOG.error("Theme with the name '{}' has already been added.", theme.name);
                break;
            }
        }

        themes.add(theme);
    }

    public static void select(String name) {
        // Find theme with the provided name
        GuiTheme theme = null;

        for (GuiTheme t : themes) {
            if (t.name.equals(name)) {
                theme = t;
                break;
            }
        }

        if (theme != null) {
            // Save current theme
            saveTheme();

            // Select new theme
            GuiThemes.theme = theme;

            // Load new theme
            try {
                File file = new File(THEMES_FOLDER, get().name + ".nbt");

                if (file.exists()) {
                    NbtCompound tag = NbtIo.read(file.toPath());
                    if (tag != null) get().fromTag(tag);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Save global gui settings with the new theme
            saveGlobal();
        }
    }

    public static GuiTheme get() {
        return theme;
    }

    public static String[] getNames() {
        String[] names = new String[themes.size()];

        for (int i = 0; i < themes.size(); i++) {
            names[i] = themes.get(i).name;
        }

        return names;
    }

    // Saving

    private static void saveTheme() {
        if (get() != null) {
            try {
                NbtCompound tag = get().toTag();

                THEMES_FOLDER.mkdirs();
                NbtIo.write(tag, new File(THEMES_FOLDER, get().name + ".nbt").toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveGlobal() {
        try {
            NbtCompound tag = new NbtCompound();
            tag.putString("currentTheme", get().name);

            FOLDER.mkdirs();
            NbtIo.write(tag, FILE.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        saveTheme();
        saveGlobal();
    }
}
