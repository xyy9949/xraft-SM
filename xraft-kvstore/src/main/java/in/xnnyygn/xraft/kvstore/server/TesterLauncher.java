package in.xnnyygn.xraft.kvstore.server;

import in.xnnyygn.xraft.core.node.Node;
import in.xnnyygn.xraft.core.node.NodeBuilder;
import in.xnnyygn.xraft.core.node.NodeEndpoint;
import in.xnnyygn.xraft.core.node.NodeId;
import org.apache.commons.cli.*;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TesterLauncher extends ServerLauncher implements Runnable{
    CommandLine cmdLine;
    public TesterLauncher(String[] args){
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
            startAsTester(cmdLine);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startAsTester(CommandLine cmdLine) throws Exception {
        if (!cmdLine.hasOption("gc")) {
            throw new IllegalArgumentException("group-config required");
        }

        String[] rawGroupConfig = cmdLine.getOptionValues("gc");
        String rawNodeId = cmdLine.getOptionValue('i');
        int portService = ((Long) cmdLine.getParsedOptionValue("p2")).intValue();

        Set<NodeEndpoint> nodeEndpoints = Stream.of(rawGroupConfig)
                .map(this::parseNodeEndpoint)
                .collect(Collectors.toSet());

        Node node = new NodeBuilder(nodeEndpoints, new NodeId(rawNodeId))
                .setDataDir(cmdLine.getOptionValue('d'))
                .build();
        Server server = new Server(node, portService);
//        logger.info("start as group member, group config {}, id {}, port service {}", nodeEndpoints, rawNodeId, portService);
        startServer(server);
    }
}
