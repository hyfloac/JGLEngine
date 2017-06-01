package com.github.hyfloac.jglengine.fontMeshCreator;

import com.github.hyfloac.jglengine.fontMeshCreator.MetaFile.FontFile.Char;
import com.github.hyfloac.jglengine.fontMeshCreator.MetaFile.FontFile.Page;
import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.vitrifiedcode.javautilities.array.ArrayUtil;
import com.github.vitrifiedcode.javautilities.io.IO;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides functionality for getting the values from a font file.
 *
 * @author Karl
 */
public class MetaFile
{

    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;

    private static final int DESIRED_PADDING = 3;

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";

    private double aspectRatio;

    private double verticalPerPixelSize;
    private double horizontalPerPixelSize;
    private double spaceWidth;
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;

    private Map<Integer, Character> metaData = new HashMap<Integer, Character>();

    private BufferedReader reader;
    private Map<String, String> values = new HashMap<String, String>();

    /**
     * Opens a font file in preparation for reading.
     *
     * @param file - the font file.
     */
    protected MetaFile(File file)
    {
        this.aspectRatio = Reference.ASPECT_RATIO;
        openFile(file);
        loadPaddingData();
        loadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        loadCharacterData(imageWidth);
        close();
    }

    protected double getSpaceWidth()
    {
        return spaceWidth;
    }

    protected Character getCharacter(int ascii)
    {
        return metaData.get(ascii);
    }

    /**
     * Read in the next line and store the variable values.
     *
     * @return {@code true} if the end of the file hasn't been reached.
     */
    private boolean processNextLine()
    {
        values.clear();
        String line = null;
        try
        {
            line = reader.readLine();
        }
        catch(IOException e1)
        {
        }
        if(line == null) { return false; }
        for(String part : line.split(SPLITTER))
        {
            String[] valuePairs = part.split("=");
            if(valuePairs.length == 2)
            {
                values.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    /**
     * Gets the {@code int} value of the variable with a certain name on the
     * current line.
     *
     * @param variable - the name of the variable.
     * @return The value of the variable.
     */
    private int getValueOfVariable(String variable)
    {
        return Integer.parseInt(values.get(variable));
    }

    /**
     * Gets the array of ints associated with a variable on the current line.
     *
     * @param variable - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    private int[] getValuesOfVariable(String variable)
    {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for(int i = 0; i < actualValues.length; i++)
        {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    /**
     * Closes the font file after finishing reading.
     */
    private void close()
    {
        try
        {
            reader.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Opens the font file, ready for reading.
     *
     * @param file - the font file.
     */
    private void openFile(File file)
    {
        try
        {
            reader = new BufferedReader(new FileReader(file));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }

    /**
     * Loads the data about how much padding is used around each character in
     * the texture atlas.
     */
    private void loadPaddingData()
    {
        processNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }

    /**
     * Loads information about the line height for this font in pixels, and uses
     * this as a way to find the conversion rate between pixels in the texture
     * atlas and screen-space.
     */
    private void loadLineSizes()
    {
        processNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        verticalPerPixelSize = TextMeshCreator.LINE_HEIGHT / (double) lineHeightPixels;
        horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
    }

    /**
     * Loads in data about each character and stores the data in the
     * {@link Character} class.
     *
     * @param imageWidth - the width of the texture atlas in pixels.
     */
    private void loadCharacterData(int imageWidth)
    {
        processNextLine();
        processNextLine();
        while(processNextLine())
        {
            Character c = loadCharacter(imageWidth);
            if(c != null)
            {
                metaData.put(c.id, c);
            }
        }
    }

    /**
     * Loads all the data about one character in the texture atlas and converts
     * it all from 'pixels' to 'screen-space' before storing. The effects of
     * padding are also removed from the data.
     *
     * @param imageSize - the size of the texture atlas in pixels.
     * @return The data about the character.
     */
    private Character loadCharacter(int imageSize)
    {
        int id = getValueOfVariable("id");
        if(id == TextMeshCreator.SPACE_ASCII)
        {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }

    public static Model[] genModels(FontFile in)
    {
        Model[] models = new Model[in.chars.length];
        final int[] indices = new int[] { 0, 1, 3, 3, 1, 2 };
        int index = 0;
        for(Char c : in.chars)
        {
            float texX = c.x / (in.texW - 1);
            float texY = 1 - c.y / (in.texH - 1);
            float texWidth = c.width / (in.texW - 1) + texX;
            float texHeight = c.height / (in.texH - 1) + texY;
            float[] vertices = new float[] { 0, c.height, 0, 0, c.width, 0, c.width, c.height };
            float[] textures = new float[] { texX, texY, texX, texHeight, texWidth, texY, texWidth, texY };
            models[index] = new Model(vertices, 2, textures, 2, null, 2, indices);
            models[index].getMaterial().useLight = false;
            ++index;
        }
        return models;
    }

    public static FontFile loadCharacters(InputStream in) throws IOException
    {
        FontFile out = new FontFile();
        int charIndex = 0;
        for(List<String> lineSet : IO.readFile(in))
        {
            for(String line : lineSet)
            {
                if(line.startsWith("info "))
                {
                    for(String s : line.split(" "))
                    {
                        String[] split = s.split("=");
                        switch(split[0])
                        {
                            case "face":
                                out.name = split[1].replaceAll("\"", "");
                                break;
                            case "size":
                                out.size = Integer.parseInt(split[1]);
                                break;
                            case "bold":
                                out.bold = Boolean.parseBoolean(split[1]);
                                break;
                            case "italic":
                                out.italic = Boolean.parseBoolean(split[1]);
                                break;
                            case "charset":
                                out.charset = split[1].replaceAll("\"", "");
                                break;
                            case "unicode":
                                out.unicode = Boolean.parseBoolean(split[1]);
                                break;
                            case "stretchH":
                                out.fontHeightPercent = Integer.parseInt(split[1]);
                                break;
                            case "smooth":
                                out.smooth = Boolean.parseBoolean(split[1]);
                                break;
                            case "aa":
                                out.superSampling = Integer.parseInt(split[1]);
                                break;
                            case "padding":
                                split = split[1].split(",");
                                out.padding = ArrayUtil.toInt(split[0], split[1], split[2], split[3]);
                                break;
                            case "spacing":
                                split = split[1].split(",");
                                out.spacing = ArrayUtil.toInt(split[0], split[1]);
                                break;
                            case "outline":
                                out.outline = Integer.parseInt(split[1]);
                                break;
                        }
                    }
                }
                else if(line.startsWith("common "))
                {
                    for(String s : line.split(" "))
                    {
                        String[] split = s.split("=");
                        switch(split[0])
                        {
                            case "lineHeight":
                                out.lineHeight = Integer.parseInt(split[1]);
                                break;
                            case "base":
                                out.base = Integer.parseInt(split[1]);
                                break;
                            case "scaleW":
                                out.texW = Integer.parseInt(split[1]);
                                break;
                            case "scaleH":
                                out.texH = Integer.parseInt(split[1]);
                                break;
                            case "pages":
                                out.numPages = Integer.parseInt(split[1]);
                                break;
                            case "packed":
                                out.packed = Boolean.parseBoolean(split[1]);
                                break;
                        }
                    }
                }
                else if(line.startsWith("page "))
                {
                    Page page = new Page();
                    for(String s : line.split(" "))
                    {
                        String[] split = s.split("=");
                        switch(split[0])
                        {
                            case "id":
                                page.id = Integer.parseInt(split[1]);
                                break;
                            case "file":
                                page.texName = split[1].replaceAll("\"", "");
                                break;
                        }
                    }
                    if(out.pages == null || out.pages.length == 0) { out.pages = new Page[] { page }; }
                    else { ArrayUtil.append(out.pages, page); }
                }
                else if(line.startsWith("chars "))
                {
                    for(String s : line.split(" "))
                    {
                        String[] split = s.split("=");
                        switch(split[0])
                        {
                            case "count":
                                out.numChars = Integer.parseInt(split[1]);
                                out.chars = new Char[out.numChars];
                                break;
                        }
                    }
                }
                else if(line.startsWith("char "))
                {
                    Char c = new Char();
                    for(String s : line.split(" "))
                    {
                        String[] split = s.split("=");
                        switch(split[0])
                        {
                            case "id":
                                c.id = Integer.parseInt(split[1]);
                                break;
                            case "x":
                                c.x = Integer.parseInt(split[1]);
                                break;
                            case "y":
                                c.y = Integer.parseInt(split[1]);
                                break;
                            case "width":
                                c.width = Integer.parseInt(split[1]);
                                break;
                            case "height":
                                c.height = Integer.parseInt(split[1]);
                                break;
                            case "xoffset":
                                c.xOffset = Integer.parseInt(split[1]);
                                break;
                            case "yoffset":
                                c.yOffset = Integer.parseInt(split[1]);
                                break;
                            case "xadvance":
                                c.xAdvance = Integer.parseInt(split[1]);
                                break;
                            case "page":
                                c.page = Integer.parseInt(split[1]);
                                break;
                            case "chnl":
                                c.channel = Integer.parseInt(split[1]);
                                break;
                        }
                    }
                    out.chars[charIndex] = c;
                    ++charIndex;
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
        }

        public static class Char
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
        }
    }
}
