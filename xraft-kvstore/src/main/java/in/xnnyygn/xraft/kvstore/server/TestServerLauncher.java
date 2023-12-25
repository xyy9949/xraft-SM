package in.xnnyygn.xraft.kvstore.server;

import org.apache.commons.cli.*;

import java.util.concurrent.CountDownLatch;

public class TestServerLauncher extends ServerLauncher implements Runnable{

    CommandLine cmdLine;
    private CountDownLatch testLatch;
    public TestServerLauncher(String[] args, CountDownLatch latch){
        testLatch = latch;

        Options options = new Options();
        options.addOption(Option.builder("m")
                .hasArg()
                .argName("mode")
                .desc("start mode, available: standalone, standby, group-member. default is standalone")
                .build());
        options.addOption(Option.builder("i")
                .longOpt("id")
                .hasArg()
                .argName("node-id")
                .required()
                .desc("node id, required. must be unique in group. " +
                        "if starts with mode group-member, please ensure id in group config")
                .build());
        options.addOption(Option.builder("h")
                .hasArg()
                .argName("host")
                .desc("host, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p1")
                .longOpt("port-raft-node")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .desc("port of raft node, required when starts with standalone or standby mode")
                .build());
        options.addOption(Option.builder("p2")
                .longOpt("port-service")
                .hasArg()
                .argName("port")
                .type(Number.class)
                .required()
                .desc("port of service, required")
                .build());
        options.addOption(Option.builder("d")
                .hasArg()
                .argName("data-dir")
                .desc("data directory, optional. must be present")
                .build());
        options.addOption(Option.builder("gc")
                .hasArgs()
                .argName("node-endpoint")
                .desc("group config, required when starts with group-member mode. format: <node-endpoint> <node-endpoint>..., " +
                        "format of node-endpoint: <node-id>,<host>,<port-raft-node>, eg: A,localhost,8000 B,localhost,8010")
                .build());

        if (args.length == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("xraft-kvstore [OPTION]...", options);
            return;
        }

        CommandLineParser parser = new DefaultParser();
        try {
            cmdLine = parser.parse(options, args);
        } catch (ParseException | IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }

    }

    @Override
    public void run() {
        try {
            startAsGroupMember(cmdLine, testLatch);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
