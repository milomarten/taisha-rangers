package com.github.milomarten.taisharangers.image;

/**
 * Utility class representing a coordinate point
 * @param x The X value
 * @param y The Y value
 */
public record Point(int x, int y) {
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }
}
