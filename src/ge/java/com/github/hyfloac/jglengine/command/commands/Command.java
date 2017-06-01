package com.github.hyfloac.jglengine.command.commands;

import com.github.hyfloac.jglengine.command.commands.exception.CommandUsageException;
import com.github.vitrifiedcode.javautilities.object.BitConverter;
import com.github.vitrifiedcode.javautilities.object.ObjectUtil;
import com.github.vitrifiedcode.javautilities.string.StringUtil;

import javax.annotation.Nonnull;
import java.util.Base64;

@SuppressWarnings("unused")
public abstract class Command
{
    public abstract String[] getNames();

    public abstract String getUsage();

    public static Boolean isTrue(String in)
    {
        if(StringUtil.equalsIgnoreCase("true", in) || StringUtil.equals("1", in) || StringUtil.equalsIgnoreCase("enabled", in)) { return Boolean.TRUE; }
        else if(StringUtil.equalsIgnoreCase("false", in) || StringUtil.equals("0", in) || StringUtil.equalsIgnoreCase("disabled", in)) { return Boolean.FALSE; }
        return null;
    }

    public static int parseInt(String in) throws CommandUsageException
    {
        try
        {
            if(in.startsWith("0x")) { return Integer.parseInt(in.substring(2), 16); }
            else if(in.startsWith("0o")) { return Integer.parseInt(in.substring(2), 8); }
            else if(in.startsWith("0b")) { return Integer.parseInt(in.substring(2), 2); }
            else if(in.startsWith("0s")) { return BitConverter.toInt(Base64.getDecoder().decode(in.substring(2))); }
            else { return Integer.parseInt(in); }
        }
        catch(NumberFormatException e)
        {
            throw new CommandUsageException("Input is not a number.", e);
        }
    }

    public static long parseLong(String in) throws CommandUsageException
    {
        try
        {
            if(in.startsWith("0x")) { return Long.parseLong(in.substring(2), 16); }
            else if(in.startsWith("0o")) { return Long.parseLong(in.substring(2), 8); }
            else if(in.startsWith("0b")) { return Long.parseLong(in.substring(2), 2); }
            else if(in.startsWith("0s")) { return BitConverter.toLong(Base64.getDecoder().decode(in.substring(2))); }
            else { return Long.parseLong(in); }
        }
        catch(NumberFormatException e)
        {
            throw new CommandUsageException("Input is not a number.", e);
        }
    }

    public abstract boolean execute(String[] args) throws CommandUsageException;

    public static boolean equals(@Nonnull String o0, final @Nonnull String... o1)
    {
        if(o0.isEmpty()) { return false; }
        o0 = o0.toLowerCase();
        int hash0 = o0.hashCode();
        for(String s : o1)
        {
            if(s == null || s.isEmpty()) { return false; }
            s = s.toLowerCase();
            if(s.hashCode() == hash0 && s.equals(o0)) { return true; }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj != null && obj instanceof Command && StringUtil.equalsIgnoreCase(getNames(), ((Command) obj).getNames());
    }
}
