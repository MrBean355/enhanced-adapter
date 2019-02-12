package com.github.mrbean355.android

internal interface EnhancedAdapterExecutors {
    fun executeOnMainThread(block: () -> Unit)
    fun executeOnWorkerThread(block: () -> Unit)
}