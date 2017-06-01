package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.array.ArrayUtil;
import com.github.vitrifiedcode.javautilities.io.IO;
import com.github.vitrifiedcode.javautilities.list.container.Tuple;
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
    private static final transient Pattern REMOVE_SPACE = Pattern.compile("[ \\t]+");
    private static final transient Pattern F_SLASH = Pattern.compile("/");
    private static final transient Pattern SPACE = Pattern.compile(" ");

    public static Model loadOBJ(InputStream in)
    {
        List<Vector3f> vertices = new ArrayList<Vector3f>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3f> normals = new ArrayList<Vector3f>();
        List<Tuple<Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>>> indices = new ArrayList<Tuple<Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>>>();
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
                    line = REMOVE_SPACE.matcher(line.trim()).replaceAll(" ");
                    String[] split = SPACE.split(line);
                    if(split.length == 0) { continue; }
                    if("v".equals(split[0])) { vertices.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]))); }
                    else if("vt".equals(split[0])) { textures.add(new Vector2f(Float.parseFloat(split[1]), Float.parseFloat(split[2]))); }
                    else if("vn".equals(split[0])) { normals.add(new Vector3f(Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]))); }
                    else if("f".equals(split[0]))
                    {
                        String[] splitL = F_SLASH.split(split[1]);
                        String[] splitM = F_SLASH.split(split[2]);
                        String[] splitR = F_SLASH.split(split[3]);
                        if(split[1].contains("//") || splitL.length == 1) { hasTextures = false; }
                        if(splitL.length <= 2) { hasNormals = false; }
                        Tuple<Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>> indice = new Tuple<Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>>(new Tuple<Integer, Integer, Integer>(Integer.parseInt(splitL[0]) - 1, null, null), new Tuple<Integer, Integer, Integer>(Integer.parseInt(splitM[0]) - 1, null, null), new Tuple<Integer, Integer, Integer>(Integer.parseInt(splitR[0]) - 1, null, null));
                        if(hasTextures)
                        {
                            indice.left.middle = Integer.parseInt(splitL[1]) - 1;
                            indice.middle.middle = Integer.parseInt(splitM[1]) - 1;
                            indice.right.middle = Integer.parseInt(splitR[1]) - 1;
                        }

                        if(hasNormals)
                        {
                            indice.left.right = Integer.parseInt(splitL[2]) - 1;
                            indice.middle.right = Integer.parseInt(splitM[2]) - 1;
                            indice.right.right = Integer.parseInt(splitR[2]) - 1;
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
                verticesOut[index++] = v.x;
                verticesOut[index++] = v.y;
                verticesOut[index++] = v.z;
            }

            List<Integer> indicesOut = new ArrayList<Integer>();
            for(Tuple<Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>, Tuple<Integer, Integer, Integer>> ind : indices)
            {
                indicesOut.add(ind.left.left);
                indicesOut.add(ind.middle.left);
                indicesOut.add(ind.right.left);

                if(hasTextures)
                {
                    texturesOut[ind.left.left * 2] = textures.get(ind.left.middle).x;
                    texturesOut[ind.left.left * 2 + 1] = 1 - textures.get(ind.left.middle).y;
                    texturesOut[ind.middle.left * 2] = textures.get(ind.middle.middle).x;
                    texturesOut[ind.middle.left * 2 + 1] = 1 - textures.get(ind.middle.middle).y;
                    texturesOut[ind.right.left * 2] = textures.get(ind.right.middle).x;
                    texturesOut[ind.right.left * 2 + 1] = 1 - textures.get(ind.right.middle).y;
                }

                if(hasNormals)
                {
                    normalsOut[ind.left.left * 3] = normals.get(ind.left.right).x;
                    normalsOut[ind.left.left * 3 + 1] = normals.get(ind.left.right).y;
                    normalsOut[ind.left.left * 3 + 2] = normals.get(ind.left.right).z;
                    normalsOut[ind.middle.left * 3] = normals.get(ind.middle.right).x;
                    normalsOut[ind.middle.left * 3 + 1] = normals.get(ind.middle.right).y;
                    normalsOut[ind.middle.left * 3 + 2] = normals.get(ind.middle.right).z;
                    normalsOut[ind.right.left * 3] = normals.get(ind.right.right).x;
                    normalsOut[ind.right.left * 3 + 1] = normals.get(ind.right.right).y;
                    normalsOut[ind.right.left * 3 + 2] = normals.get(ind.right.right).z;
                }
            }
            if(!hasTextures) { texturesOut = null; }
            if(!hasNormals) { normalsOut = null; }
            return new Model(verticesOut, texturesOut, normalsOut, ArrayUtil.toIntArray(indicesOut));
        }
        catch(IOException e) { Logger.traceS(e); }

        return null;
    }
}