/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.cookieclient.utils.notebot.instrumentdetect;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.util.math.BlockPos;

public interface InstrumentDetectFunction {
    /**
     * Detects an instrument for noteblock
     *
     * @param noteBlock Noteblock state
     * @param blockPos Noteblock position
     * @return Detected instrument
     */
    NoteBlockInstrument detectInstrument(BlockState noteBlock, BlockPos blockPos);
}
