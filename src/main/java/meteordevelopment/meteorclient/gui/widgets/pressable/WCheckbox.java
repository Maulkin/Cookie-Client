/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.gui.widgets.pressable;

public abstract class WCheckbox extends WPressable {
    public boolean checked;

    public WCheckbox(boolean checked) {
        this.checked = checked;
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();
        double s = theme.textHeight();

        width = pad + s + pad;
        height = pad + s + pad;
    }

    @Override
    protected void onPressed(int button) {
        checked = !checked;
    }
}
