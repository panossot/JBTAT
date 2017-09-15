/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jboss.eap.additional.testsuite.annotations.EapAdditionalTestsuite;

import junit.framework.TestCase;

/**
 *
 */
@EapAdditionalTestsuite({"modules/testcases/jdkAll/master/threadTestsuite/src/main/java"})
public final class ThreadPoolMoreTestCase extends TestCase {

    public void testQueuelessKeepAlive() throws InterruptedException {
        // Test for https://issues.jboss.org/browse/JBTHR-32 QueuelessExecutor doesn't shrink with keepAliveTime
        final QueuelessExecutor simpleQueuelessExecutor = new QueuelessExecutor(threadFactory, SimpleDirectExecutor.INSTANCE, null, 100L);
        simpleQueuelessExecutor.setMaxThreads(1);
        final Holder<Thread> thread1 = new Holder<Thread>(null);
        simpleQueuelessExecutor.execute(new Runnable() {
                boolean first = true;
                public void run() {
                    thread1.set(Thread.currentThread());
                }
            });
        try {
            Thread.sleep(500L);
        } catch (InterruptedException ignore) { }
        final CountDownLatch latch = new CountDownLatch(1);
        final Holder<Thread> thread2 = new Holder<Thread>(null);
        simpleQueuelessExecutor.execute(new Runnable() {
                public void run() {
                    thread2.set(Thread.currentThread());
                    latch.countDown();
                }
            });
        latch.await(100L, TimeUnit.MILLISECONDS);
        assertTrue("First task and second task should be executed on different threads", thread1.get() != thread2.get());
        simpleQueuelessExecutor.shutdown();
        assertTrue("Executor not shut down in 800ms", simpleQueuelessExecutor.awaitTermination(800L, TimeUnit.MILLISECONDS));
        Thread.interrupted();
    }

}

