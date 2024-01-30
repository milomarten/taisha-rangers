package com.github.milomarten.taisharangers.image.sources;

import com.github.milomarten.taisharangers.image.Color;

public interface WriteableImageSource extends ImageSource {
    void setPixel(int x, int y, Color color);
}
