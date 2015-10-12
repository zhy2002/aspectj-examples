package zhy2002.aspectjexamples.test;


import org.testng.annotations.Test;
import java.util.concurrent.CountDownLatch;


import static org.testng.Assert.*;

public class ThreadTest {


    private static class NewThread extends Thread {

        private long tid;

        public long getTid() {
            return tid;
        }

        @Override
        public void run() {
            tid = this.getId();
        }

    }


    /**
     * Different threads have different ids.
     *
     * @throws Exception
     */
    @Test
    public void eachThreadHasADifferenceId() throws Exception {
        NewThread t1 = new NewThread();
        t1.start();
        NewThread t2 = new NewThread();
        t2.start();
        t1.join();
        t2.join();
        assertNotEquals(0, t1.getTid());
        assertNotEquals(0, t2.getTid());
        assertNotEquals(t1.getTid(), t2.getTid());
        assertNotEquals(Thread.currentThread().getId(), t2.getTid());
        assertNotEquals(t1.getTid(), Thread.currentThread().getId());
    }

    @Test
    public void createThreadWithRunnableInterface() throws Exception {
        final long[] tid1 = new long[1];
        final long[] tid2 = new long[1];
        Thread t1 = new Thread(() -> {
            tid1[0] = Thread.currentThread().getId();
        });
        Thread t2 = new Thread(() -> {
            tid2[0] = Thread.currentThread().getId();
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertNotEquals(0, tid1[0]);
        assertNotEquals(0, tid2[0]);
        assertNotEquals(tid1[0], tid2[0]);
        assertNotEquals(Thread.currentThread().getId(), tid2[0]);
        assertNotEquals(tid1[0], Thread.currentThread().getId());
    }

    @Test
    public void canSleep() throws Exception {
        long startTime = System.currentTimeMillis();
        Thread.sleep(1000);
        double duration = (System.currentTimeMillis() - startTime) / 1000.0;
        assertTrue(duration >= 1);
    }

    @Test
    public void interruptedDuringSleepTriggersInterruptedException() throws Exception {
        final String[] flag = new String[1];
        Thread t1 = new Thread(() -> {
            try {
                flag[0] = "start";
                Thread.sleep(1000);
                flag[0] = "end";
            } catch (InterruptedException ex) {
                flag[0] = "interrupted";
            }
        });
        t1.start();
        Thread.sleep(500);
        t1.interrupt();
        while (t1.getState() != Thread.State.TERMINATED)  //wait for it to terminate
            Thread.sleep(0);
        assertEquals("interrupted", flag[0]);
    }

    @Test
    public void differenceBetweenStaticAndInstanceInterruptedMethods() throws Exception {
        Thread t1 = new Thread(() -> {

            while (!Thread.interrupted()) ; //<i>interrupted status</i> of the thread is cleared by this method.

            assertFalse(Thread.currentThread().isInterrupted());

            while (!Thread.currentThread().isInterrupted());  // The <i>interrupted status</i> of the thread is unaffected by this method.

            assertTrue(Thread.interrupted());

        });

        t1.start();
        Thread.sleep(50);
        t1.interrupt();
        Thread.sleep(50);
        t1.interrupt();
        Thread.sleep(50);
    }


    @Test
    public void joinTest() throws InterruptedException{

        final String[] flag = new String[1];

        Thread t1 = new Thread(()->{
            try {
                Thread.sleep(100);
                flag[0] = "done";
            }catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }

        });

        t1.start();
        t1.join();
        assertEquals("done", flag[0]);
    }

    private static class SynchronizedCounter {
        private int count = 0;

        public SynchronizedCounter (){
            this(0);
        }

        public SynchronizedCounter (int count){
            this.count = count;
        }

        public synchronized void increase(){
            count++;
        }

        public synchronized void decrease(){
            count--;
        }

        public synchronized int getCount(){
            return count;
        }
    }

    /**
     * it is not possible for two invocations of synchronized methods on the same object to interleave.
     * when a synchronized method exits, it automatically establishes a happens-before relationship with any subsequent invocation of a synchronized method for the same object. This guarantees that changes to the state of the object are visible to all threads.
     * @throws InterruptedException
     */
    @Test
    public void synchronizedCounterTest() throws InterruptedException{
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final SynchronizedCounter  counter = new SynchronizedCounter ();
        Thread t1 = new Thread(()->{
            try {
                countDownLatch.await();
                for(int i=0; i<1000;i++){
                    counter.increase();
                }
            }catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }
        });

        Thread t2 = new Thread(()->{
            try {

                countDownLatch.await();
                for(int i=0; i<1000;i++){
                    counter.decrease();
                }

            }catch (InterruptedException ex){
                throw new RuntimeException(ex);
            }
        });

        t1.start();
        t2.start();
        countDownLatch.countDown();
        t1.join();
        t2.join();

        assertEquals(counter.getCount(), 0);

    }

    //resume here: https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html
}