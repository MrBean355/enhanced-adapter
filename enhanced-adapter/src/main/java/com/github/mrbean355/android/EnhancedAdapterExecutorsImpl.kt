package com.github.mrbean355.android

import android.os.Handler
import android.os.Looper
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Provides a mechanism for the [EnhancedAdapter] to execute tasks in the background or on the main
 * thread.
 *
 * Can set a custom delegate to allow easier testing (e.g. run everything on the main thread).
 */
internal object EnhancedAdapterExecutorsImpl : EnhancedAdapterExecutors {

    @VisibleForTesting
    internal val defaultExecutors: EnhancedAdapterExecutors = DefaultExecutors()

    @VisibleForTesting
    internal var delegate: EnhancedAdapterExecutors = defaultExecutors

    fun setDelegate(delegate: EnhancedAdapterExecutors?) {
        this.delegate = delegate ?: defaultExecutors
    }

    override fun executeOnMainThread(block: () -> Unit) {
        delegate.executeOnMainThread(block)
    }

    override fun executeOnWorkerThread(block: () -> Unit) {
        delegate.executeOnWorkerThread(block)
    }

    class DefaultExecutors : EnhancedAdapterExecutors {
        private val backgroundExecutor: Executor by lazy { Executors.newSingleThreadExecutor() }
        private val handler by lazy { Handler(Looper.getMainLooper()) }

        override fun executeOnMainThread(block: () -> Unit) {
            handler.post(block)
        }

        override fun executeOnWorkerThread(block: () -> Unit) {
            backgroundExecutor.execute(block)
        }
    }
}
