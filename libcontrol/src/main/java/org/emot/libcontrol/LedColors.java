package org.emot.libcontrol;

public enum LedColors {
    BLACK(0,0,0),
    WHITE(255,255,255),
    GREEN(0,255,0),
    RED(255,0,0),
    YELLOW(255,255,0),
    BLUE(0,0,255),
    DARK_BLUE(102,178,255);

    int r;
    int g;
    int b;

    private LedColors(int rv, int gv, int bv) {
        this.r = rv;
        this.g = gv;
        this.b = bv;
    }
}
