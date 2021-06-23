package io.omniedge

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

@Suppress("UNUSED")
object ThreadUtils {

    private val sThreadPoolExecutor: ThreadPoolExecutor
    private val sMainThreadHandler = Handler(Looper.getMainLooper())
    private var sWorkHandler: Handler? = null

    // 初始化
    init {
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = 2.coerceAtLeast((cpuCount - 1).coerceAtMost(4))
        val maxPoolSize = cpuCount * 2 + 1
        val blockingQueue: BlockingQueue<Runnable> = LinkedBlockingQueue(128)
        sThreadPoolExecutor = ThreadPoolExecutor(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS, blockingQueue, ARThreadFactory())
        sThreadPoolExecutor.allowCoreThreadTimeOut(true)
    }

    // 主线程任务
    fun runOnMainThread(r: Runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            r.run()
        } else {
            sMainThreadHandler.post(r)
        }
    }

    // 主线程延时任务
    fun runOnMainThread(r: Runnable, delay: Long) {
        sMainThreadHandler.postDelayed(r, delay)
    }

    // 移除主线程的任务
    fun removeMainCallbacks(r: Runnable) {
        sMainThreadHandler.removeCallbacks(r)
    }

    // 移除主线程所有任务
    fun removeMainAllTasks() {
        sMainThreadHandler.removeCallbacksAndMessages(null)
    }

    // 无返回值的异步任务
    fun execute(r: Runnable) {
        sThreadPoolExecutor.execute(r)
    }

    // 有返回值的异步任务
    fun <T> submit(task: Callable<T>): Future<T> {
        return sThreadPoolExecutor.submit(task)
    }

    // 异步延时任务，使用 HandlerThread
    fun postDelayed(r: Runnable, delayMillis: Long) {
        ensureSubHandler()
        sWorkHandler!!.postDelayed(r, delayMillis)
    }

    // 异步定时任务，使用 HandlerThread
    fun postAtTime(r: Runnable, uptimeMillis: Long) {
        ensureSubHandler()
        sWorkHandler!!.postAtTime(r, uptimeMillis)
    }

    // 移除异步线程的任务
    fun removeWorkCallbacks(r: Runnable) {
        sWorkHandler?.removeCallbacks(r)
    }

    // 有返回值的异步任务，在工作线程回调
    fun <T> enqueue(callable: Callable<T>, callback: Callback<T>) {
        sThreadPoolExecutor.execute {
            try {
                callback.onStart()
                callable.call()
                callback.onSuccess()
            } catch (throwable: Throwable) {
                callback.onFailure()
            } finally {
                callback.onFinish()
            }
        }
    }

    // 有返回值的异步任务，主线程调用并接收回调
    fun <T> enqueueOnMainThread(callable: Callable<T>, callback: Callback<T>) {
        sThreadPoolExecutor.execute {
            try {
                val countDownLatch = CountDownLatch(1)
                sMainThreadHandler.post {
                    callback.onStart()
                    countDownLatch.countDown()
                }
                countDownLatch.await(1000, TimeUnit.MILLISECONDS)
                callable.call()
                sMainThreadHandler.post { callback.onSuccess() }
            } catch (throwable: Throwable) {
                sMainThreadHandler.post { callback.onFailure() }
            } finally {
                sMainThreadHandler.post { callback.onFinish() }
            }
        }
    }

    @Synchronized
    private fun ensureSubHandler() {
        if (sWorkHandler == null) {
            val handlerThread = HandlerThread("WorkHandler")
            handlerThread.start()
            sWorkHandler = Handler(handlerThread.looper)
        }
    }

    // 主线程的回调  // 执行顺序： // onStart-->onSuccess-->onFinish  // onStart-->onFailure-->onFinish
    abstract class Callback<T> {
        fun onStart() {}
        fun onFinish() {}
        fun onSuccess() {}
        fun onFailure() {}
    }

    private class ARThreadFactory : ThreadFactory {

        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String

        override fun newThread(r: Runnable): Thread {
            val t = Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0)
            if (t.isDaemon) t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
            return t
        }

        companion object {
            private val poolNumber = AtomicInteger(1)
        }

        init {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup!!
            namePrefix = "ARThreadPool-" + poolNumber.getAndIncrement() + "-thread-"
        }
    }
}