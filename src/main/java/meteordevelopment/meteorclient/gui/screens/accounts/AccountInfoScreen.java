/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.gui.screens.accounts;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.TokenAccount;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static meteordevelopment.meteorclient.CookieClient.mc;

public class AccountInfoScreen extends WindowScreen {
    private final Account<?> account;

    public AccountInfoScreen(GuiTheme theme, Account<?> account) {
        super(theme, account.getUsername() + " details");
        this.account = account;
    }

    @Override
    public void initWidgets() {
        TokenAccount e = (TokenAccount) account;
        WHorizontalList l = add(theme.horizontalList()).expandX().widget();

        WButton copy = theme.button("Copy");
        copy.action = () -> mc.keyboard.setClipboard(e.getToken());

        l.add(theme.label("TheAltening token:"));
        l.add(theme.label(e.getToken()).color(Color.GRAY)).pad(5);
        l.add(copy);
    }
}
