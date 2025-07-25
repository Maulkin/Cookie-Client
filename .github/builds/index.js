/*
 * This file is part of the Cookie Client distribution (https://github.com/cookie-client/cookie-client).
 * Copyright (c) Cookie Development.
 */

import { getMcVersion } from "./mc_version.js"

const buildNumber = process.argv[2];
const branch = process.argv[3];
const compareUrl = process.argv[4];
const success = process.argv[5] === "true";

const mcVersion = await getMcVersion();

function sendDiscordWebhook() {
    fetch(compareUrl)
        .then(res => res.json())
        .then(res => {
            let description = "";

            description += "**Branch:** " + branch;
            description += "\n**Status:** " + (success ? "success" : "failure");

            let changes = "\n\n**Changes:**";
            let hasChanges = false;
            for (let i in res.commits) {
                let commit = res.commits[i];

                changes += "\n- [`" + commit.sha.substring(0, 7) + "`](https://github.com/cookie-client/cookie-client/commit/" + commit.sha + ") *" + commit.commit.message + "*";
                hasChanges = true;
            }
            if (hasChanges) description += changes;

            if (success) {
                description += "\n\nVisit our [website](https://cookieclient.com) for download";
            }

            const webhook = {
                username: "Builds",
                avatar_url: "https://cookieclient.com/icon.png",
                embeds: [
                    {
                        title: "Cookie Client " + mcVersion + " build #" + buildNumber,
                        description: description,
                        url: "https://cookieclient.com",
                            color: success ? 2672680 : 13117480
                    }
                ]
            };

            fetch(process.env.DISCORD_WEBHOOK, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(webhook)
            });
        });
}

if (success) {
    fetch("https://cookieclient.com/api/recheckMaven", {
        method: "POST",
        headers: {
            "Authorization": process.env.SERVER_TOKEN
        }
    });
}

sendDiscordWebhook()
