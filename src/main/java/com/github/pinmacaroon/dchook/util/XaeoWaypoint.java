package com.github.pinmacaroon.dchook.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XaeoWaypoint {
    private static final Pattern WAYPOINT_PATTERN = Pattern.compile(
            //               name1   marker2  x3       y4       z5       yaw6      ?        ?                  dimension7
            "^xaero-waypoint:([^:]+):(.{1,2}):(-?\\d+):(-?\\d+):(-?\\d+):(-?\\d+):[^:]+:[^:]+:Internal-(the-nether|overworld|the-end)$"
    );

    public static XaeoWaypoint parse(String text) {
        Matcher matcher = WAYPOINT_PATTERN.matcher(text);
        if (!matcher.matches()) return null;
        return new XaeoWaypoint(
                matcher.group(1),
                matcher.group(2),
                Integer.parseInt(matcher.group(3)),
                Integer.parseInt(matcher.group(4)),
                Integer.parseInt(matcher.group(5)),
                Integer.parseInt(matcher.group(6)),
                matcher.group(7)
        );
    }

    private enum Dimension {
        OVERWORLD, NETHER, END, OTHER
    }

    public final String name;
    public final String marker;
    public final int x;
    public final int y;
    public final int z;
    public final int yaw;
    private final Dimension dimension;

    private XaeoWaypoint(String name, String marker, int x, int y, int z, int yaw, String dimension) {
        if (name.equals("gui.xaero-deathpoint")) this.name = "Death Point";
        else this.name = name;
        this.marker = marker;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.dimension = switch (dimension) {
            case "overworld" -> Dimension.OVERWORLD;
            case "the-nether" -> Dimension.NETHER;
            case "the-end" -> Dimension.END;
            default -> Dimension.OTHER;
        };
    }

    public String getDimension(){
        return switch (this.dimension) {
            case OVERWORLD -> "The Overworld";
            case NETHER -> "The Nether";
            case END -> "The End";
            default -> "non vanilla dimension";
        };
    }
}
