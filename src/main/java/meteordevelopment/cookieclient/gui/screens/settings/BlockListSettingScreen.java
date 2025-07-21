/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.gui.screens.settings;

import meteordevelopment.cookieclient.gui.GuiTheme;
import meteordevelopment.cookieclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.mixin.IdentifierAccessor;
import meteordevelopment.cookieclient.settings.BlockListSetting;
import meteordevelopment.cookieclient.settings.Setting;
import meteordevelopment.cookieclient.utils.misc.Names;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;

public class BlockListSettingScreen extends CollectionListSettingScreen<Block> {
    private static final Identifier ID = Identifier.of("minecraft", "");

    public BlockListSettingScreen(GuiTheme theme, Setting<List<Block>> setting) {
        super(theme, "Select Blocks", setting, setting.get(), Registries.BLOCK);
    }

    @Override
    protected boolean includeValue(Block value) {
        Predicate<Block> filter = ((BlockListSetting) setting).filter;

        if (filter == null) return value != Blocks.AIR;
        return filter.test(value);
    }

    @Override
    protected WWidget getValueWidget(Block value) {
        return theme.itemWithLabel(value.asItem().getDefaultStack(), getValueName(value));
    }

    @Override
    protected String getValueName(Block value) {
        return Names.get(value);
    }

    @Override
    protected boolean skipValue(Block value) {
        return Registries.BLOCK.getId(value).getPath().endsWith("_wall_banner");
    }

    @Override
    protected Block getAdditionalValue(Block value) {
        String path = Registries.BLOCK.getId(value).getPath();
        if (!path.endsWith("_banner")) return null;

        ((IdentifierAccessor) (Object) ID).setPath(path.substring(0, path.length() - 6) + "wall_banner");
        return Registries.BLOCK.get(ID);
    }
}
