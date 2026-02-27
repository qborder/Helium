package com.helium.platform;

public final class DwmEnums {

    private DwmEnums() {}

    public enum WindowMaterial {
        AUTO("auto"),
        NONE("none"),
        MICA("mica"),
        ACRYLIC("acrylic"),
        TABBED("tabbed");

        public final String id;

        WindowMaterial(String id) {
            this.id = id;
        }

        public static WindowMaterial fromString(String s) {
            if (s == null) return TABBED;
            for (WindowMaterial m : values()) {
                if (m.id.equalsIgnoreCase(s) || m.name().equalsIgnoreCase(s)) return m;
            }
            return TABBED;
        }
    }

    public enum WindowCorner {
        DEFAULT("default"),
        DO_NOT_ROUND("square"),
        ROUND("round"),
        ROUND_SMALL("round_small");

        public final String id;

        WindowCorner(String id) {
            this.id = id;
        }

        public static WindowCorner fromString(String s) {
            if (s == null) return ROUND;
            for (WindowCorner c : values()) {
                if (c.id.equalsIgnoreCase(s) || c.name().equalsIgnoreCase(s)) return c;
            }
            return ROUND;
        }
    }
}
