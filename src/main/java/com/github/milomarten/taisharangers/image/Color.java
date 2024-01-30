package com.github.milomarten.taisharangers.image;

public record Color(int rgba) {
    public static final Color TRANSPARENT = new Color(0);

    public Color(double r, double g, double b, double a) {
        this(getFromComponents(r, g, b, a));
    }

    public Color(int r, int g, int b, double a) {
        this(getFromComponents(r, g, b, a));
    }

    private static int getFromComponents(double r, double g, double b, double a) {
        return new java.awt.Color((float)r, (float)g, (float)b, (float) a).getRGB();
    }

    private static int getFromComponents(int r, int g, int b, double a) {
        return new java.awt.Color(r, g, b, (float)a).getRGB();
    }

    private int component(int shift) {
        return ((this.rgba >>> (shift * 8)) & 0b11111111);
    }
    public int red() { return component(2); }
    public int green() { return component(1); }
    public int blue() { return component(0); }
    public double alpha() { return component(3) / 255.0; }

    public int[] components() {
        var c = new int[4];
        int iter = this.rgba;
        for (int i = 0; i < 4; i++) {
            c[i] = (iter & 0xFF);
            iter >>>= 8;
        }
        return c;
    }

    public Color withAlpha(double newA) {
        var components = components();
        return new Color(components[0], components[1], components[2], newA);
    }
}
