package com.github.mrbean355.android

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks

class EnhancedAdapterExecutorsImplTest {
    @Mock
    private lateinit var delegate: EnhancedAdapterExecutors

    @Before
    fun setUp() {
        initMocks(this)
        EnhancedAdapterExecutorsImpl.setDelegate(delegate)
    }

    @After
    fun tearDown() {
        EnhancedAdapterExecutorsImpl.setDelegate(null)
    }

    @Test
    fun testSetDelegate_WithNonNullInput_SetsDelegateFieldToInput() {
        val delegate = mock<EnhancedAdapterExecutors>()

        EnhancedAdapterExecutorsImpl.setDelegate(delegate)

        assertSame(delegate, EnhancedAdapterExecutorsImpl.delegate)
    }

    @Test
    fun testSetDelegate_WithNullInput_SetsDelegateFieldToDefaultExecutors() {
        EnhancedAdapterExecutorsImpl.setDelegate(null)

        assertSame(EnhancedAdapterExecutorsImpl.defaultExecutors, EnhancedAdapterExecutorsImpl.delegate)
    }

    @Test
    fun testExecuteOnMainThread_CallsDelegate() {
        val block = mock<() -> Unit>()

        EnhancedAdapterExecutorsImpl.executeOnMainThread(block)

        verify(delegate).executeOnMainThread(block)
    }

    @Test
    fun testExecuteOnWorkerThread_CallsDelegate() {
        val block = mock<() -> Unit>()

        EnhancedAdapterExecutorsImpl.executeOnWorkerThread(block)

        verify(delegate).executeOnWorkerThread(block)
    }
}