/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.utils.notebot.instrumentdetect;

import net.minecraft.block.NoteBlock;
import net.minecraft.client.MinecraftClient;

public enum InstrumentDetectMode {
    BlockState(((noteBlock, blockPos) -> noteBlock.get(NoteBlock.INSTRUMENT))),
    BelowBlock(((noteBlock, blockPos) -> MinecraftClient.getInstance().world.getBlockState(blockPos.down()).getInstrument()));

    private final InstrumentDetectFunction instrumentDetectFunction;

    InstrumentDetectMode(InstrumentDetectFunction instrumentDetectFunction) {
        this.instrumentDetectFunction = instrumentDetectFunction;
    }

    public InstrumentDetectFunction getInstrumentDetectFunction() {
        return instrumentDetectFunction;
    }
}
