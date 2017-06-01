package com.github.hyfloac.jglengine.command;

import com.github.hyfloac.jglengine.command.commands.Command;
import com.github.hyfloac.jglengine.command.commands.GLCommand;
import com.github.hyfloac.jglengine.command.commands.exception.CommandUsageException;
import com.github.vitrifiedcode.javautilities.string.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CLI extends ChannelInboundHandlerAdapter
{
    //    private static final Pattern SPACE_SPLITTER = Pattern.compile("((?<= )|(?= ))");
    private static final Pattern SPACE_SPLITTER = Pattern.compile(" ");

    static List<Command> commands;
    Thread t;

    static
    {

        commands = new ArrayList<Command>();
        commands.add(new GLCommand());
    }

    public CLI()
    {

        this.t = new Thread(() ->
                            {
                                EventLoopGroup bossGroup = new NioEventLoopGroup();
                                EventLoopGroup workerGroup = new NioEventLoopGroup();
                                try
                                {
                                    ServerBootstrap b = new ServerBootstrap();
                                    b.group(bossGroup, workerGroup)
                                     .channel(NioServerSocketChannel.class)
                                     .childHandler(new ChannelInitializer<SocketChannel>()
                                     {
                                         @Override
                                         public void initChannel(SocketChannel ch) throws Exception
                                         {
                                             ch.pipeline().addLast(new ChannelHandler());
                                         }
                                     })
                                     .option(ChannelOption.SO_BACKLOG, 128)
                                     .childOption(ChannelOption.SO_KEEPALIVE, true);

                                    // Bind and start to accept incoming connections.
                                    ChannelFuture f = b.bind(8080).sync();

                                    // Wait until the server socket is closed.
                                    // In this example, this does not happen, but you can do that to gracefully
                                    // shut down your server.
                                    f.channel().closeFuture().sync();
                                }
                                catch(InterruptedException e) { e.printStackTrace(); }
                                finally
                                {
                                    workerGroup.shutdownGracefully();
                                    bossGroup.shutdownGracefully();
                                }
                            });
        t.start();
    }

    public void update()
    {
    }

    @Override
    protected void finalize() throws Throwable
    {
        t.join();
    }

    public static void parse(String in)
    {
        if(!in.startsWith("/")) { return; }
        String[] split = SPACE_SPLITTER.split(in.substring(1, in.length() - 2));
        for(Command c : commands)
        {
            if(Command.equals(split[0], c.getNames()))
            {
                String[] args;
                if(split.length > 1)
                {
                    args = Arrays.copyOfRange(split, 1, split.length);
                }
                else { args = new String[0]; }
                try
                {
                    System.out.println(Arrays.toString(args));
                    if(!c.execute(args))
                    {
                        System.out.println("Command Failed.");
                    }
                }
                catch(CommandUsageException e)
                {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
