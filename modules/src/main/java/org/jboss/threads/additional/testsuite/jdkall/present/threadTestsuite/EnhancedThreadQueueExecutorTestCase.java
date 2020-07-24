package org.jboss.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

@EapAdditionalTestsuite({"modules/testcases/jdkAll/master/threadTestsuite/src/main/java","modules/testcases/jdkAll/3.x/threadTestsuite/src/main/java"})
public class EnhancedThreadQueueExecutorTestCase {

    @Test
    public void testEnhancedExecutorShutdownNoTasks() throws Exception {
        final CountDownLatch terminateLatch = new CountDownLatch(1);
        EnhancedQueueExecutor executor = new EnhancedQueueExecutor.Builder()
                .setCorePoolSize(10)
                .setKeepAliveTime(1, TimeUnit.NANOSECONDS)
                .setTerminationTask(new Runnable() {
                    @Override
                    public void run() {
                        terminateLatch.countDown();
                    }
                })
                .build();

        executor.shutdown();
        Assert.assertTrue(terminateLatch.await(10, TimeUnit.SECONDS));
    }

    @Test //JBTHR-50
    public void testEnhancedExecutorShutdown() throws Exception {
        final CountDownLatch terminateLatch = new CountDownLatch(1);
        EnhancedQueueExecutor executor = new EnhancedQueueExecutor.Builder()
                .setCorePoolSize(10)
                .setKeepAliveTime(1, TimeUnit.NANOSECONDS)
                .setTerminationTask(new Runnable() {
                    @Override
                    public void run() {
                        terminateLatch.countDown();
                    }
                })
                .build();

        for(int i = 0; i < 10000; ++i) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        //ignore
                    }
                }
            });
        }
        executor.shutdown();
        Assert.assertTrue(terminateLatch.await(10, TimeUnit.SECONDS));
    }
}
