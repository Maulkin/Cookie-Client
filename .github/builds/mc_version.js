/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

import * as fs from "fs"
import * as readline from "readline"

export async function getMcVersion() {
    let lines = readline.createInterface({
        input: fs.createReadStream("../../gradle.properties"),
        crlfDelay: Infinity
    })

    let mcVersion = ""

    for await (const line of lines) {
        if (line.startsWith("minecraft_version")) {
            mcVersion = line.substring(line.indexOf("=") + 1)
            break
        }
    }

    if (mcVersion === "") {
        console.log("Failed to read minecraft_version")
        process.exit(1)
    }

    return mcVersion
}
