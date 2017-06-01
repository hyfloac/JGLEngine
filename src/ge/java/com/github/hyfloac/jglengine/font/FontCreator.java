package com.github.hyfloac.jglengine.font;

import com.github.hyfloac.jglengine.font.FontCreator.FontFile.Page;
import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.model.Texture;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.vitrifiedcode.javautilities.array.ArrayUtil;
import com.github.vitrifiedcode.javautilities.io.IO;
import com.github.vitrifiedcode.javautilities.string.StringUtil;
import org.joml.Vector4f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FontCreator
{
    private static final Pattern QUOTE = Pattern.compile("\"");

    static final int[] indices = new int[] { 0, 1, 3, 3, 1, 2 };

    public static FontFile genModels(FontFile in)
    {
        List<Texture> textureList = new ArrayList<Texture>();
        for(Page p : in.pages) { textureList.add(new Texture(p.texName, false)); }
        for(Char c : in.chars)
        {
            float texX = (float) c.x / (float) (in.texW - 1);
            float texY = (float) c.y / (float) (in.texH - 1);
            float texWidth = texX + (c.width / (float) (in.texW - 1));
            float texHeight = texY + (c.height / (float) (in.texH - 1));
            float div = 120F;
            float verW = c.width / (div);
            float verH = c.height / (div);
            float[] vertices = new float[] { 0, verH, 0, 0, verW, 0, verW, verH };
            float[] textures = new float[] { texX, texY, texX, texHeight, texWidth, texHeight, texWidth, texY };
            Model model = new Model(vertices, 2, textures, 2, null, 2, indices);
            model.getMaterial().useLight = false;
            model.setTexID(textureList.get(c.page).texID);
            model.getMaterial().color = new Vector4f(1.0f, 0.0f, 0.0f, 0.6f);
            model.getMaterial().useColor = true;

            c.model = model;
        }
        return in;
    }

    static Char getChar(Char[] chars, char c)
    {
        for(Char cc : chars)
        {
            if(cc.id == c) { return cc; }
        }
        return null;
    }

    static float[] genTextures(Char c, FontFile in)
    {
        float texX = (float) c.x / (float) (in.texW - 1);
        float texY = (float) c.y / (float) (in.texH - 1);
        float texWidth = texX + (c.width / (float) (in.texW - 1));
        float texHeight = texY + (c.height / (float) (in.texH - 1));
        return new float[] { texX, texY, texX, texHeight, texWidth, texHeight, texWidth, texY };
    }

    static float[] genVerticies(Char c, FontFile in, float baseX, float baseY, float fontSize)
    {
        float x = baseX + (c.xOffset * fontSize);
        float y = baseY + (c.yOffset * fontSize);
        float maxX = x + (c.width * fontSize);
        float maxY = y + (c.height * fontSize);
        float properX = (2 * x) - 1;
        float properY = (-2 * y) + 1;
        float properMaxX = (2 * maxX) - 1;
        float properMaxY = (-2 * maxY) + 1;
        return new float[] { properX, properMaxY, properX, properY, properMaxX, properY, properMaxX, properMaxY };
    }

    public static void genModels(FontFile in, String text, boolean centered, float fontSize)
    {
        float baseX = 0.0F;
        float baseY = 0.0F;
        if(centered)
        {
            baseX = ((Reference.WIDTH - 10) - text.length()) / 2;
        }

        for(char c : text.toCharArray())
        {
            Char cc = getChar(in.chars, c);

        }
    }

    public static FontFile loadCharacters(InputStream in) throws IOException
    {
        FontFile out = new FontFile();
        int charIndex = 0;
        for(List<String> lineSet : IO.readFile(in))
        {
            for(String line : lineSet)
            {
                String[] spaceSplit = StringUtil.SPACE_PATTERN.split(line);
                if(spaceSplit.length < 1) { continue; }
                List<String> commaSplit = new ArrayList<String>();
                for(String s : spaceSplit)
                {
                    String[] split = StringUtil.EQUALS_SINGLE_PATTERN.split(s);
                    commaSplit.addAll(Arrays.asList(split));
                }

                String[] ssss = commaSplit.toArray(new String[0]);

                if(StringUtil.equalsIgnoreCase("info", ssss[0]))
                {
                    //region info
                    String[] s;
                    for(int i = 1; i < ssss.length; i += 2)
                    {
                        switch(ssss[i])
                        {
                            case "face":
                                out.name = QUOTE.matcher(ssss[i + 1]).replaceAll("");
                                break;
                            case "size":
                                out.size = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "bold":
                                out.bold = Boolean.parseBoolean(ssss[i + 1]);
                                break;
                            case "italic":
                                out.italic = Boolean.parseBoolean(ssss[i + 1]);
                                break;
                            case "charset":
                                out.charset = QUOTE.matcher(ssss[i + 1]).replaceAll("");
                                break;
                            case "unicode":
                                out.unicode = Boolean.parseBoolean(ssss[i + 1]);
                                break;
                            case "stretchH":
                                out.fontHeightPercent = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "smooth":
                                out.smooth = Boolean.parseBoolean(ssss[i + 1]);
                                break;
                            case "aa":
                                out.superSampling = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "padding":
                                s = StringUtil.COMMA_SINGLE_PATTERN.split(ssss[i + 1]);
                                out.padding = ArrayUtil.toInt(s[0], s[1], s[2], s[3]);
                                break;
                            case "spacing":
                                s = StringUtil.COMMA_SINGLE_PATTERN.split(ssss[i + 1]);
                                out.spacing = ArrayUtil.toInt(s[0], s[1]);
                                break;
                            case "outline":
                                out.outline = Integer.parseInt(ssss[i + 1]);
                                break;
                        }
                    }
                    //endregion
                }
                else if(StringUtil.equalsIgnoreCase("common", ssss[0]))
                {
                    //region common
                    for(int i = 1; i < ssss.length; i += 2)
                    {
                        switch(ssss[i])
                        {
                            case "lineHeight":
                                out.lineHeight = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "base":
                                out.base = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "scaleW":
                                out.texW = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "scaleH":
                                out.texH = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "pages":
                                out.numPages = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "packed":
                                out.packed = Boolean.parseBoolean(ssss[i + 1]);
                                break;
                        }
                    }
                    //endregion
                }
                else if(StringUtil.equalsIgnoreCase("page", ssss[0]))
                {
                    //region page
                    Page page = new Page();
                    for(int i = 1; i < ssss.length; i += 2)
                    {
                        switch(ssss[i])
                        {
                            case "id":
                                page.id = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "file":
                                page.texName = "/" + QUOTE.matcher(ssss[i + 1]).replaceAll("");
                                break;
                        }
                    }

                    if(out.pages == null || out.pages.length == 0) { out.pages = new Page[] { page }; }
                    else { ArrayUtil.append(out.pages, page); }
                    //endregion
                }
                else if(StringUtil.equalsIgnoreCase("chars", ssss[0]))
                {
                    //region chars
                    switch(ssss[1])
                    {
                        case "count":
                            out.numChars = Integer.parseInt(ssss[2]);
                            out.chars = new Char[out.numChars];
                            break;
                    }
                    //endregion
                }
                else if(line.startsWith("char "))
                {
                    //region char
                    Char c = new Char();
                    for(int i = 1; i < ssss.length; i += 2)
                    {
                        switch(ssss[i])
                        {
                            case "id":
                                c.id = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "x":
                                c.x = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "y":
                                c.y = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "width":
                                c.width = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "height":
                                c.height = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "xoffset":
                                c.xOffset = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "yoffset":
                                c.yOffset = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "xadvance":
                                c.xAdvance = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "page":
                                c.page = Integer.parseInt(ssss[i + 1]);
                                break;
                            case "chnl":
                                c.channel = Integer.parseInt(ssss[i + 1]);
                                break;
                        }
                    }
                    out.chars[charIndex] = c;
                    ++charIndex;
                    //endregion
                }
            }
        }
        return out;
    }

    public static class FontFile
    {
        // Info
        public String name;
        public int size;
        public boolean bold;
        public boolean italic;
        public String charset;
        public boolean unicode;
        public int fontHeightPercent;
        public boolean smooth;
        public int superSampling;
        public int[] padding;
        public int[] spacing;
        public int outline;

        // Common
        public int lineHeight;
        public int base;
        public int texW;
        public int texH;
        public int numPages;
        public boolean packed;

        // Page
        public Page[] pages;

        // Char
        public int numChars;
        public Char[] chars;

        public static class Page
        {
            public int id;
            public String texName;
            public Texture texture;
        }
    }
}
