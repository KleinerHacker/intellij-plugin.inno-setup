/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.script.test

import java.util.concurrent.atomic.AtomicBoolean

/** Global per-test-method time budget in seconds. Overridable via `-Dis.test.method.timeout.seconds`. */
internal val TEST_METHOD_TIMEOUT_SECONDS: Long =
    System.getProperty("is.test.method.timeout.seconds")?.toLongOrNull() ?: (15 * 60L)

/**
 * Runs [body] (a single test method) on the **current** thread — IntelliJ platform test fixtures must not be
 * moved to a foreign thread — guarded by a daemon watchdog that interrupts the test thread once
 * [TEST_METHOD_TIMEOUT_SECONDS] elapses and turns the run into an [AssertionError]. This prevents a single
 * hanging/looping test from stalling the whole suite indefinitely.
 *
 * Note: interruption can only unwind a test that is blocked or cooperatively checks the interrupt flag; a
 * pure busy-loop in non-cancellable code cannot be force-killed without tearing down the JVM. The watchdog
 * still flags such a method as failed so the cause is visible.
 */
internal fun runWithMethodTimeout(body: () -> Unit) {
    val timeoutMs = TEST_METHOD_TIMEOUT_SECONDS * 1000
    val testThread = Thread.currentThread()
    val timedOut = AtomicBoolean(false)
    val watchdog = Thread({
        try {
            Thread.sleep(timeoutMs)
        } catch (_: InterruptedException) {
            return@Thread // cancelled because the test finished in time
        }
        timedOut.set(true)
        testThread.interrupt()
    }, "is-test-timeout-watchdog").apply { isDaemon = true }
    watchdog.start()

    val timeoutMessage = "Test method exceeded the ${TEST_METHOD_TIMEOUT_SECONDS}s timeout and was aborted"
    try {
        body()
    } catch (t: Throwable) {
        if (timedOut.get()) throw AssertionError(timeoutMessage, t)
        throw t
    } finally {
        watchdog.interrupt()
        Thread.interrupted() // clear a possibly-set interrupt flag so the pooled test thread stays clean
    }
    if (timedOut.get()) throw AssertionError(timeoutMessage)
}
