package com.github.mrbean355.android

import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces the default executors with one that runs everything on the current thread.
 *
 * TODO: Extract to separate module for reuse externally.
 */
class MainThreadRule : TestWatcher() {

    override fun starting(description: Description?) {
        EnhancedAdapterExecutorsImpl.setDelegate(object : EnhancedAdapterExecutors {
            override fun executeOnMainThread(block: () -> Unit) {
                block()
            }

            override fun executeOnWorkerThread(block: () -> Unit) {
                block()
            }
        })
    }

    override fun finished(description: Description?) {
        EnhancedAdapterExecutorsImpl.setDelegate(null)
    }
}