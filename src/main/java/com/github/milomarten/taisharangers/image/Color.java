package com.github.milomarten.taisharangers.image;

/**
 * Wrapper around the native Java color class, for additional methods.
 * Methods are provided for either working with components as bytes, or as decimals. For ease of debugging,
 * all that work with decimals are suffixed with 01.
 * @param rgba The RGBA value of the color, as a four-byte integer.
 */
public record Color(int rgba) {
    /**
     * A color that is completely transparent white
     */
    public static final Color TRANSPARENT = new Color(0);
    /**
     * The fully opaque color white
     */
    public static final Color WHITE = new Color(0, 0, 0, 255);

    /**
     * Create a color from four doubles. Range of values should be 0 to 1.
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     * @param a The alpha component
     */
    public Color(double r, double g, double b, double a) {
        this(getFromComponents(r, g, b, a));
    }

    /**
     * Create a color from four bytes. Range of values should be 0 to 255.
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     * @param a The alpha component
     */
    public Color(int r, int g, int b, int a) {
        this(getFromComponents(r, g, b, a));
    }

    /**
     * Create a color from three bytes. Alpha is assumed full transparency
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
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

    /**
     * Get the red component, 0 to 255
     * @return The red component
     */
    public int red() { return component(2); }

    /**
     * Get the green component, 0 to 255
     * @return The green component
     */
    public int green() { return component(1); }

    /**
     * Get the blue component, 0 to 255
     * @return The blue component
     */
    public int blue() { return component(0); }

    /**
     * Get the alpha component, 0 to 255
     * @return The alpha component
     */
    public int alpha() { return component(3); }

    /**
     * Get the red component as a fraction from 0 to 1
     * @return The red component
     */
    public double red01() { return red() / 255.0; }

    /**
     * Get the green component as a fraction from 0 to 1
     * @return The green component
     */
    public double green01() { return green() / 255.0; }

    /**
     * Get the blue component as a fraction from 0 to 1
     * @return the blue component
     */
    public double blue01() { return blue() / 255.0; }

    /**
     * Get the alpha component as a fraction from 0 to 1
     * @return The alpha component
     */
    public double alpha01() { return alpha() / 255.0; }

    /**
     * Get all components as bytes
     * @return The four components, as red, green, blue, and alpha.
     */
    public int[] components() {
        var c = new int[4];
        int iter = this.rgba;
        for (int i = 0; i < 4; i++) {
            c[i] = (iter & 0xFF);
            iter >>>= 8;
        }
        return c;
    }

    /**
     * Adjust the transparency of this color
     * @param newA The new alpha, 0 to 255
     * @return The new color
     */
    public Color withAlpha(int newA) {
        var components = components();
        return new Color(components[2], components[1], components[0], newA);
    }

    /**
     * Adjust the transparency of this color
     * @param newA The new alpha, 0 to 1
     * @return The new color
     */
    public Color withAlpha01(double newA) {
        return withAlpha(((int)(newA * 255)) & 0xFF);
    }
}
