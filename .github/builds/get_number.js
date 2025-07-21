/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

import { getMcVersion } from "./mc_version.js"

const mcVersion = await getMcVersion();

fetch("https://cookieclient.com/api/stats")
    .then(async res => {
        let stats = await res.json()
        let build = 0

        if (mcVersion in stats.builds) {
            build = parseInt(stats.builds[mcVersion])
        }

        console.log("number=" + (build + 1))
    })
    .catch(err => {
        console.log("Failed to fetch stats:")
        console.log(err)
        process.exit(1)
    })
