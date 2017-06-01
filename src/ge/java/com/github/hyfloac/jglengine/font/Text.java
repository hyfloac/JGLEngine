package com.github.hyfloac.jglengine.font;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Text implements Iterable<Char>
{
    public FontData font;
    public List<Char> chars;

    static final int[] indices = new int[] { 0, 1, 3, 3, 1, 2 };
    static final float[] vertices = new float[] { 0, 1, 0, 0, 1, 0, 1, 1 };

    public Text(FontData font, String text)
    {
        this.font = font;
        this.chars = new ArrayList<>();
        Collections.addAll(chars, font.getChars(text));
    }

    public void addText(String text)
    {
        Collections.addAll(chars, font.getChars(text));
    }

    @Nonnull
    @Override
    public Iterator<Char> iterator()
    {
        return chars.iterator();
    }
}
