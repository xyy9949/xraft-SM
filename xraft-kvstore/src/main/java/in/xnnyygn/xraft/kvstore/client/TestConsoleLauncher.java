package in.xnnyygn.xraft.kvstore.client;

import in.xnnyygn.xraft.core.log.LogException;
import in.xnnyygn.xraft.core.node.NodeId;
import in.xnnyygn.xraft.core.rpc.Address;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class TestConsoleLauncher extends ConsoleLauncher implements Runnable{
    private Console clientConsole;
    public TestConsoleLauncher(String[] args){
        Options options = new Options();
        options.addOption(Option.builder("gc")
                .hasArgs()
                .argName("server-config")
                .required()
                .desc("group config, required. format: <server-config> <server-config>. " +
                        "format of server config: <node-id>,<host>,<port-service>. e.g A,localhost,8001 B,localhost,8011")
                .build());
        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("xraft-kvstore-client [OPTION]...", options);
            return;
        }

        CommandLineParser parser = new DefaultParser();
        Map<NodeId, Address> serverMap;
        try {
            CommandLine commandLine = parser.parse(options, args);
            serverMap = parseGroupConfig(commandLine.getOptionValues("gc"));
        } catch (ParseException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        clientConsole = new Console(serverMap);
    }

    @Override
    public void run() {
        try {
//            clientConsole.start();
            clientConsole.startCommand();
        }catch (IllegalArgumentException e){
            throw new LogException("start client error", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
