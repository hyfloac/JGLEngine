package com.github.hyfloac.jglengine.config;

import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.array.ArrayUtil;
import com.github.vitrifiedcode.javautilities.io.IO;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 'B' boolean; 'c' char; 'S' string; 'b' byte; 's' short; 'i' int; 'l' long;
 * 'f' float; 'd' double; '[]' array
 */
@SuppressWarnings("unused")
public class Configuration
{
    private File config;
    private Map<String, String> settings;
    private Map<String, String> settingType;
    private Map<String, String> settingComments;

    public Configuration(String fileName)
    {
        config = new File(fileName);
        settings = new HashMap<>();
        settingType = new HashMap<>();
        settingComments = new HashMap<>();
    }

    public void writeSettings()
    {
        create();
        try
        {
            BufferedWriter br = IO.newBufWriter(new FileOutputStream(config));
            for(Entry<String, String> pair : settings.entrySet())
            {
                if(settingComments.containsKey(pair.getKey())) { br.write("# " + settingComments.get(pair.getKey())); }
                br.newLine();
                br.write(settingType.get(pair.getKey()) + ":" + pair.getKey() + "=" + pair.getValue());
                br.newLine();
                br.newLine();
            }
            br.close();
        }
        catch(IOException e) { Logger.traceS(e); }
    }

    public File getConfigFile()
    {
        return config;
    }

    public void create()
    {
        if(!config.exists())
        {
            try { config.createNewFile(); }
            catch(IOException e) { Logger.traceS(e); }
        }
    }

    public Map<String, String> getSettings()
    {
        return settings;
    }

    public void addSetting(String comment, String key, boolean value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "B");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, char value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "c");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, String value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "S");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, byte value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "b");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, short value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "s");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, int value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "i");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, long value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "l");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, float value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "f");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, double value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "d");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, boolean... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "B[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, char... value)
    {
        settings.put(key, String.valueOf(value));
        settingType.put(key, "c[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, String... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "S[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, byte... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "b[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, short... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "s[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, int... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "i[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, long... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "l[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, float... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "f[]");
        settingComments.put(key, comment);
    }

    public void addSetting(String comment, String key, double... value)
    {
        settings.put(key, Arrays.toString(value));
        settingType.put(key, "d[]");
        settingComments.put(key, comment);
    }

    public static class Singleton<T>
    {
        public T value;

        public Singleton() {}

        public Singleton(T value) { this.value = value; }
    }

    public Singleton getSetting(String key) throws IOException
    {
        Singleton out = new Singleton();
        boolean array = false;
        if(settingType.get(key).length() > 1) { array = true; }
        switch(settingType.get(key).charAt(0))
        {
            case 'B': // boolean
                if(array) { out = new Singleton<>(ArrayUtil.toBoolean(settings.get(key))); }
                else { out = new Singleton<>(Boolean.parseBoolean(settings.get(key))); }
                break;
            case 'c': // char
                if(array) { out = new Singleton<>(ArrayUtil.toChar(settings.get(key))); }
                else { out = new Singleton<>(settings.get(key).charAt(0)); }
                break;
            case 'S': // String
                if(array) { out = new Singleton<>(ArrayUtil.toString(settings.get(key))); }
                else { out = new Singleton<>(settings.get(key)); }
                break;
            case 'b': // byte
                if(array) { out = new Singleton<>(ArrayUtil.toByte(settings.get(key))); }
                else { out = new Singleton<>(Byte.parseByte(settings.get(key))); }
                break;
            case 's': // short
                if(array) { out = new Singleton<>(ArrayUtil.toShort(settings.get(key))); }
                else { out = new Singleton<>(Short.parseShort(settings.get(key))); }
                break;
            case 'i': // int
                if(array) { out = new Singleton<>(ArrayUtil.toInt(settings.get(key))); }
                else { out = new Singleton<>(Integer.parseInt(settings.get(key))); }
                break;
            case 'l': // long
                if(array) { out = new Singleton<>(ArrayUtil.toLong(settings.get(key))); }
                else { out = new Singleton<>(Long.parseLong(settings.get(key))); }
                break;
            case 'f': // float
                if(array) { out = new Singleton<>(ArrayUtil.toFloat(settings.get(key))); }
                else { out = new Singleton<>(Float.parseFloat(settings.get(key))); }
                break;
            case 'd': // double
                if(array) { out = new Singleton<>(ArrayUtil.toDouble(settings.get(key))); }
                else { out = new Singleton<>(Double.parseDouble(settings.get(key))); }
                break;
        }
        if(out.value == null)
        {
            backup();
            config.deleteOnExit();
            throw new IOException("Config File outdated, config will be backed up and deleted.");
        }
        return out;
    }

    public void backup()
    {
        try { Files.copy(config.toPath(), new File(config.getAbsolutePath() + ".backup").toPath(), StandardCopyOption.REPLACE_EXISTING); }
        catch(IOException e) { Logger.INSTANCE.trace(e); }
    }

    /**
     * Iterate through the configuration file and add all the settings to a map.
     *
     * @throws IOException
     */
    public void readConfig() throws IOException
    {
        BufferedReader br = IO.newBufReader(new FileInputStream(config));
        String line = "";
        String key;
        String value;
        String[] tmp;
        while((line = br.readLine()) != null)
        {
            line = line.trim();
            if(!line.startsWith("#") && !line.isEmpty())
            {
                key = line.split(":")[1];
                tmp = key.split("=");
                key = tmp[0];
                value = tmp[1];
                switch(line.charAt(0))
                {
                    case 'B': // boolean
                        settings.put(key, value);
                        settingType.put(key, "B");
                        break;
                    case 'c': // char
                        settings.put(key, value);
                        settingType.put(key, "c");
                        break;
                    case 'S': // String
                        settings.put(key, value);
                        settingType.put(key, "S");
                        break;
                    case 'b': // byte
                        settings.put(key, value);
                        settingType.put(key, "b");
                        break;
                    case 's': // short
                        settings.put(key, value);
                        settingType.put(key, "s");
                        break;
                    case 'i': // int
                        settings.put(key, value);
                        settingType.put(key, "i");
                        break;
                    case 'l': // long
                        settings.put(key, value);
                        settingType.put(key, "l");
                        break;
                    case 'f': // float
                        settings.put(key, value);
                        settingType.put(key, "f");
                        break;
                    case 'd': // double
                        settings.put(key, value);
                        settingType.put(key, "d");
                        break;
                    default:
                        Logger.errorS("Unknown line \"" + line + "\" in config \"" + config.getName() + "\".");
                }
            }
        }
        br.close();
    }
}
