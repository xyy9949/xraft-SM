package in.xnnyygn.xraft.kvstore.test;

import java.util.concurrent.CountDownLatch;

public class TestLatchCountDown {
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 3;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for(int i = 0; i < threadCount; i++){
            Thread thread = new Thread(new Worker(latch));
            thread.start();
        }
        System.out.println("waiting for all threads to finish");
        latch.await();
        System.out.println("all threads finished");
    }

    static class Worker implements Runnable{
        private final CountDownLatch latch;
        public Worker(CountDownLatch latch){
            this.latch = latch;
        }

        public void run(){
            try {
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " finished");
            latch.countDown();
        }
    }
}
