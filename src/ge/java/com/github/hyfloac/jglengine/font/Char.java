package com.github.hyfloac.jglengine.font;

import com.github.hyfloac.jglengine.model.Model;

public class Char
{
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int xOffset;
    public int yOffset;
    public int xAdvance;
    public int page;
    public int channel;
    public Model model;

    public Char() {}

    public Char(int id, int x, int y, int width, int height, int xOffset, int yOffset, int xAdvance, int page, int channel, int texW, int texH)
    {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xAdvance = xAdvance;
        this.page = page;
        this.channel = channel;
    }

    public Char(Char other)
    {
        this.id = other.id;
        this.x = other.x;
        this.y = other.y;
        this.width = other.width;
        this.height = other.height;
        this.xOffset = other.xOffset;
        this.yOffset = other.yOffset;
        this.xAdvance = other.xAdvance;
        this.page = other.page;
        this.channel = other.channel;
        this.model = other.model;
    }

    public Char(String s)
    {
        if(s.equals("hi"))
        {
            System.out.println(s);
        }
    }
}
