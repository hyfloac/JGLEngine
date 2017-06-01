package com.github.hyfloac.jglengine.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputThread
{
    private Thread thread;
    private InputRunnable runner;

    public InputThread()
    {
        runner = new InputRunnable();
    }

    public synchronized void start()
    {
        if(thread == null)
        {
            runner.start();
            thread = new Thread(runner);
            thread.start();
        }
    }

    public synchronized void stop()
    {
        if(thread != null)
        {
            runner.stop();
        }
    }

    public synchronized String getInput(boolean remove)
    {
        return runner.getInput(remove);
    }

    public synchronized String getInput()
    {
        return runner.getInput();
    }

    public synchronized boolean hasNext()
    {
        return runner.hasNext();
    }

    public static class InputRunnable implements Runnable
    {
        private static final Scanner SCANNER = new Scanner(System.in);

        private boolean living;
        private List<String> lines;

        public InputRunnable()
        {
            living = false;
            lines = new ArrayList<String>(15);
        }

        public synchronized String getInput(boolean remove)
        {
            return remove ? lines.remove(0) : lines.get(0);
        }

        public synchronized String getInput()
        {
            return getInput(false);
        }

        public synchronized boolean hasNext()
        {
            return !lines.isEmpty();
        }

        public synchronized void start()
        {
            if(!living) { living = true; }
        }

        public synchronized void stop()
        {
            if(living) { living = false; }
        }

        @Override
        public void run()
        {
            while(living)
            {
                if(SCANNER.hasNextLine())
                {
                    lines.add(SCANNER.nextLine());
                }
            }
        }
    }
}
