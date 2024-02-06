package com.github.milomarten.taisharangers.image;

public record Color(int rgba) {
    public static final Color TRANSPARENT = new Color(0);
    public static final Color WHITE = new Color(0, 0, 0, 255);

    public Color(double r, double g, double b, double a) {
        this(getFromComponents(r, g, b, a));
    }

    public Color(int r, int g, int b, int a) {
        this(getFromComponents(r, g, b, a));
    }

    public Color(int r, int g, int b) {
        this(getFromComponents(r, g, b, 255));
    }

    private static int getFromComponents(double r, double g, double b, double a) {
        return new java.awt.Color((float)r, (float)g, (float)b, (float) a).getRGB();
    }

    private static int getFromComponents(int r, int g, int b, int a) {
        return new java.awt.Color(r, g, b, a).getRGB();
    }

    private int component(int shift) {
        return ((this.rgba >>> (shift * 8)) & 0b11111111);
    }
    public int red() { return component(2); }
    public int green() { return component(1); }
    public int blue() { return component(0); }
    public int alpha() { return component(3); }

    public double red01() { return red() / 255.0; }
    public double green01() { return green() / 255.0; }
    public double blue01() { return blue() / 255.0; }
    public double alpha01() { return alpha() / 255.0; }

    public int[] components() {
        var c = new int[4];
        int iter = this.rgba;
        for (int i = 0; i < 4; i++) {
            c[i] = (iter & 0xFF);
            iter >>>= 8;
        }
        return c;
    }

    public Color withAlpha(int newA) {
        var components = components();
        return new Color(components[2], components[1], components[0], newA);
    }

    public Color withAlpha01(double newA) {
        return withAlpha(((int)(newA * 255)) & 0xFF);
    }
}
