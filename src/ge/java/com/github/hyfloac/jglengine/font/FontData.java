package com.github.hyfloac.jglengine.font;

import java.util.HashMap;
import java.util.Map;

public class FontData
{
    public final FontCreator.FontFile fontFile;
    private final Map<Integer, Char> charMap;

    public FontData(FontCreator.FontFile fontFile)
    {
        this.fontFile = fontFile;
        this.charMap = new HashMap<Integer, Char>();
        for(Char c : fontFile.chars)
        {
            charMap.put(c.id, c);
        }
    }

    public Char getChar(int id)
    {
        return charMap.get(id);
    }

    public Char getChar(char c)
    {
        return charMap.get((int) c);
    }

    public Char[] getChars(String in)
    {
        Char[] out = new Char[in.length()];
        int index = 0;
        for(char c : in.toCharArray())
        {
            out[index++] = getChar(c);
        }
        return out;
    }
}
