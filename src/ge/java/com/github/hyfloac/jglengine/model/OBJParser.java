package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.io.IO;
import com.github.vitrifiedcode.javautilities.list.container.Tuple;
import com.github.vitrifiedcode.javautilities.string.StaticPattern;
import com.github.vitrifiedcode.javautilities.string.StringUtil;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OBJParser
{
    //    private static final Pattern TAB_PATTERN = StaticPattern.getPattern("\\t");
    private static final Pattern SPACE_PATTERN = StaticPattern.getPattern("[\\t ]+");
    private static final Pattern SLASH_PATTERN = StaticPattern.getPattern("/");

    public static Model loadOBJ(InputStream in)
    {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Tuple<int[], int[], int[]>> indices = new ArrayList<>();
        boolean hasTextures = true;
        boolean hasNormals = true;
        try
        {
            BufferedReader br = IO.newBufReader(in);
            String line;
            while((line = br.readLine()) != null)
            {
                if(!line.startsWith("#") && !line.isEmpty())
                {
                    line = SPACE_PATTERN.matcher(line.trim()).replaceAll(" ");
//                    line = TAB_PATTERN.matcher(line).replaceAll(" ");
                    String[] split = StringUtil.SPACE_SINGLE_PATTERN.split(line);
                    if(split.length == 0) { continue; }
                    if("v".equals(split[0])) { vertices.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]))); }
                    else if("vt".equals(split[0])) { textures.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2]))); }
                    else if("vn".equals(split[0])) { normals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]))); }
                    else if("f".equals(split[0]))
                    {
                        String[] splitL = SLASH_PATTERN.split(split[1]);
                        String[] splitM = SLASH_PATTERN.split(split[2]);
                        String[] splitR = SLASH_PATTERN.split(split[3]);
                        if(split[1].contains("//") || splitL.length == 1) { hasTextures = false; }
                        if(splitL.length <= 2) { hasNormals = false; }

                        Tuple<int[], int[], int[]> indice = new Tuple<>(new int[] { Integer.parseInt(splitL[0]) - 1, 0, 0 }, new int[] { Integer.parseInt(splitM[0]) - 1, 0, 0 }, new int[] { Integer.parseInt(splitR[0]) - 1, 0, 0 });
                        if(hasTextures)
                        {
                            indice.left[1] = Integer.parseInt(splitL[1]) - 1;
                            indice.middle[1] = Integer.parseInt(splitM[1]) - 1;
                            indice.right[1] = Integer.parseInt(splitR[1]) - 1;
                        }

                        if(hasNormals)
                        {
                            indice.left[2] = Integer.parseInt(splitL[2]) - 1;
                            indice.middle[2] = Integer.parseInt(splitM[2]) - 1;
                            indice.right[2] = Integer.parseInt(splitR[2]) - 1;
                        }
                        indices.add(indice);
                    }
                }
            }
            br.close();
            float[] verticesOut = new float[vertices.size() * 3];
            float[] texturesOut = new float[vertices.size() * 2];
            float[] normalsOut = new float[vertices.size() * 3];

            int index = 0;
            for(Vector3f v : vertices)
            {
                verticesOut[index * 3] = v.x;
                verticesOut[index * 3 + 1] = v.y;
                verticesOut[index * 3 + 2] = v.z;
                ++index;
            }

            index = 0;
            int[] indicesOut = new int[indices.size() * 3];
            for(Tuple<int[], int[], int[]> ind : indices)
            {
                indicesOut[index++] = ind.left[0];
                indicesOut[index++] = ind.middle[0];
                indicesOut[index++] = ind.right[0];

                if(hasTextures)
                {
                    texturesOut[ind.left[0] * 2] = textures.get(ind.left[1]).x;
                    texturesOut[ind.left[0] * 2 + 1] = 1 - textures.get(ind.left[1]).y;
                    texturesOut[ind.middle[0] * 2] = textures.get(ind.middle[1]).x;
                    texturesOut[ind.middle[0] * 2 + 1] = 1 - textures.get(ind.middle[1]).y;
                    texturesOut[ind.right[0] * 2] = textures.get(ind.right[1]).x;
                    texturesOut[ind.right[0] * 2 + 1] = 1 - textures.get(ind.right[1]).y;
                }

                if(hasNormals)
                {
                    normalsOut[ind.left[0] * 3] = normals.get(ind.left[2]).x;
                    normalsOut[ind.left[0] * 3 + 1] = normals.get(ind.left[2]).y;
                    normalsOut[ind.left[0] * 3 + 2] = normals.get(ind.left[2]).z;
                    normalsOut[ind.middle[0] * 3] = normals.get(ind.middle[2]).x;
                    normalsOut[ind.middle[0] * 3 + 1] = normals.get(ind.middle[2]).y;
                    normalsOut[ind.middle[0] * 3 + 2] = normals.get(ind.middle[2]).z;
                    normalsOut[ind.right[0] * 3] = normals.get(ind.right[2]).x;
                    normalsOut[ind.right[0] * 3 + 1] = normals.get(ind.right[2]).y;
                    normalsOut[ind.right[0] * 3 + 2] = normals.get(ind.right[2]).z;
                }
            }
            if(!hasTextures) { texturesOut = null; }
            if(!hasNormals) { normalsOut = null; }

            vertices.clear();
            textures.clear();
            normals.clear();
            indices.clear();

            return new Model(verticesOut, texturesOut, normalsOut, indicesOut);
        }
        catch(IOException e) { Logger.traceS(e); }

        return null;
    }
}