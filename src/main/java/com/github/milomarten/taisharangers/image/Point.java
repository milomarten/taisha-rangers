package com.github.milomarten.taisharangers.image;

public record Point(int x, int y) {
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }
}
