/*
 * This file is part of the Cookie Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Cookie Development.
 */

package meteordevelopment.meteorclient.systems.waypoints.events;

import meteordevelopment.meteorclient.systems.waypoints.Waypoint;

public class WaypointRemovedEvent {

    public final Waypoint waypoint;

    public WaypointRemovedEvent(Waypoint waypoint) {
        this.waypoint = waypoint;
    }
}
