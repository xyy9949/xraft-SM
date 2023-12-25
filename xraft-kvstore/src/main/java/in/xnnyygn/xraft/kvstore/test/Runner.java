package in.xnnyygn.xraft.kvstore.test;

import in.xnnyygn.xraft.kvstore.client.ConsoleLauncher;
import in.xnnyygn.xraft.kvstore.client.TestConsoleLauncher;
import in.xnnyygn.xraft.kvstore.server.TestServerLauncher;
import sun.security.acl.AclEntryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Runner {

//    private static TestConf conf;
    private static List<TestServerLauncher> servers = new ArrayList<>();
    private static TestConsoleLauncher client;
    private static TestServerLauncher tester;

    public static CountDownLatch heartBeatCountDownLatch;


    private static int SERVER_COUNT = 3;

    public static void main(String[] args) throws InterruptedException {
//        conf = TestConf.initialize("test.conf", args);

//        setupTester();
        heartBeatCountDownLatch = new CountDownLatch(2);
        setupServers();

        Thread.sleep(6000);

        setupClient();

    }


    private static void setupClient(){
        // start client
        String[] setupClinetArgs = "-gc A,localhost,3333 B,localhost,3334 C,localhost,3335".split("\\s+");
        client = new TestConsoleLauncher(setupClinetArgs);
        Thread clientThread = new Thread(client, "testing-client");
        clientThread.start();
    }
    private static void setupServers() {
        String[][] setupServerArgs = {"-gc A,localhost,2333 B,localhost,2334 C,localhost,2335 -m group-member -i A -p2 3333".split("\\s+"),
                "-gc A,localhost,2333 B,localhost,2334 C,localhost,2335 -m group-member -i B -p2 3334".split("\\s+"),
                "-gc A,localhost,2333 B,localhost,2334 C,localhost,2335 -m group-member -i C -p2 3335".split("\\s+")};
        for (int i = 0; i < SERVER_COUNT; i++) {
            TestServerLauncher testServerLauncher = new TestServerLauncher(setupServerArgs[i], heartBeatCountDownLatch);
            Thread serverThread = new Thread(testServerLauncher, "test-server-" + i);
            serverThread.start();
            servers.add(testServerLauncher);
        }
    }

//    private static void setupTester(){
//        String[]  setupTesterArgs = "-gc A,localhost,2333 B,localhost,2334 C,localhost,2335 D,localhost,2336 -m group-member -i D -p2 3336".split("\\s+");
//        TestServerLauncher testServerLauncher = new TestServerLauncher(setupTesterArgs);
//        Thread testerThread = new Thread(testServerLauncher, "tester");
//        testerThread.start();
//        tester = testServerLauncher;
//    }

}