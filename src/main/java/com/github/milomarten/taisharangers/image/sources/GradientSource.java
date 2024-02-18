package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;
import com.github.milomarten.taisharangers.image.Point;

import java.util.OptionalInt;

/**
 * An ImageSource that represents one linear gradient
 */
public class GradientSource implements ImageSource {
    private final Point start;
    private final Point end;
    private final Color startColor;
    private final Color endColor;

    private final long[] coefficients;

    /**
     * Create the linear gradient.
     * The linear gradient will travel the line delineated by the start and end point, gradually shifting from
     * the start to the end color. Points behind the start point and end point will be solidly the start and end color,
     * respectively.
     * The algorithm I scraped from the internet seems to not care how the points are oriented with respect
     * to each other, although an exception will be thrown at creation time if the same start and end point is used.
     * The color interpolation used is smarter than a standard lerp, and seems to produce reasonably nice colors, even
     * when making a gradient between two opposite colors.
     * @param start The starting point of the gradient
     * @param end The ending point of the gradient
     * @param startColor The starting color of the gradient
     * @param endColor The ending color of the gradient.
     */
    public GradientSource(Point start, Point end, Color startColor, Color endColor) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("You suck");
        }

        this.start = start;
        this.end = end;
        this.startColor = startColor;
        this.endColor = endColor;

        this.coefficients = new long[4];
        coefficients[0] = end.x() - start.x();
        coefficients[1] = end.y() - start.y();
        coefficients[2] = (coefficients[0] * start.x()) + (coefficients[1] * start.y());
        coefficients[3] = (coefficients[0] * end.x()) + (coefficients[1] * end.y());
    }

    @Override
    public Color getPixel(int x, int y) {
        long c = (coefficients[0] * x) + (coefficients[1] * y);
        if (c <= coefficients[2]) { return startColor; }
        else if (c >= coefficients[3]) { return endColor; }
        else {
            return new Color(
                    interpolate(c, startColor.red01(), endColor.red01()),
                    interpolate(c, startColor.green01(), endColor.green01()),
                    interpolate(c, startColor.blue01(), endColor.blue01()),
                    interpolate(c, startColor.alpha01(), endColor.alpha01())
            );
        }
    }

    private double interpolate(long c, double sc, double ec) {
        long c1 = coefficients[2];
        long c2 = coefficients[3];
        return ((sc * (c2 - c)) + (ec * (c - c1))) / (c2 - c1);
    }

    @Override
    public OptionalInt getWidth() {
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt getHeight() {
        return OptionalInt.empty();
    }
}
