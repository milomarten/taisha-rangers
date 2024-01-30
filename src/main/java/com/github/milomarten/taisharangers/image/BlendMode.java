package com.github.milomarten.taisharangers.image;

public enum BlendMode {
    /**
     * Standard blending, which composites the top layer over the bottom using standard rules.
     */
    NORMAL {
        @Override
        public Color blend(Color top, Color bottom) {
            return alphaComposite(top, bottom);
        }
    },
    /**
     * Mask blending, which returns the top color at the same alpha as the bottom value.
     */
    MASK {
        @Override
        public Color blend(Color top, Color bottom) {
            return top.withAlpha(bottom.alpha());
        }
    }
    ;

    public abstract Color blend(Color top, Color bottom);

    protected static Color alphaComposite(Color top, Color bottom) {
        double bottomAlpha = bottom.alpha();
        double topAlpha = top.alpha();

        if (topAlpha == 1) { return top; }
        else if(topAlpha == 0) { return bottom; }
        else {
            double finalAlpha = topAlpha + (bottomAlpha * (1 - topAlpha));
            return new Color(
                    mixComponentsWithAlpha(top.red(), topAlpha, bottom.red(), bottomAlpha, finalAlpha),
                    mixComponentsWithAlpha(top.green(), topAlpha, bottom.green(), bottomAlpha, finalAlpha),
                    mixComponentsWithAlpha(top.blue(), topAlpha, bottom.blue(), bottomAlpha, finalAlpha),
                    finalAlpha
            );
        }
    }

    private static double mixComponentsWithAlpha(int colorA, double alphaA, int colorB, double alphaB, double alphaNaught) {
        float colorADec = colorA / 255f;
        float colorBDec = colorB /255f;
        double mixed = ((colorADec * alphaA) + (colorBDec * alphaB * (1 - alphaA))) / alphaNaught;
        if (mixed < 0) return 0;
        if (mixed > 1) return 1;
        else return mixed;
    }
}
