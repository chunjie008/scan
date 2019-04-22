package com.ycnote.scan.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 游超
 * @description 默认线程池
 */
@Slf4j
public class DefaultThreadPoolExecutor extends ThreadPoolExecutor{
    /**
     * 核心线程数量
     */
    private static  int THREAD_NUMBER_CORE = 32;

    /**
     * 最大线程数
     */
    private static  int THREAD_NUMBER_MAX = 1024;

    /**
     * 单线程池最大线程数
     */
    private static  int THREAD_NUMBER_CRITICAL = 512;
    /**
     * 队列长度
     */
    private static  int QUEUE_SIZE = 0;
    /**
     * 线程等待时间
     */
    private static  long LEISUR_TIME = 0;

    /**
     * 线程池set
     */
    private static CopyOnWriteArraySet<DefaultThreadPoolExecutor> executorSet = new CopyOnWriteArraySet<DefaultThreadPoolExecutor>();
    /**
     * 锁
     */
    private  ReentrantLock lock = new ReentrantLock();


    public DefaultThreadPoolExecutor() {
        super(THREAD_NUMBER_CORE, THREAD_NUMBER_CRITICAL,
                LEISUR_TIME, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        executorSet.add(this);
        for(DefaultThreadPoolExecutor executor:executorSet){
            if(executor.equals(this)){

            }else{
                executor.shutdownNow();
            }
        }
    }

    /**
     * 执行线程
     *
     * @param r
     */
    public  boolean executeTask(Runnable r) {
        lock.lock();
        try {
            int treadCount = 0;
            for(DefaultThreadPoolExecutor d:executorSet){
                treadCount = treadCount + d.getActiveCount();
            }
            int activeCount = getActiveCount();
            if (activeCount < THREAD_NUMBER_CRITICAL&&treadCount<THREAD_NUMBER_MAX) {
                execute(r);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("task error",e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 结束线程池内所有任务
     * @return
     */
    public List<Runnable> shutdownNow() {
        List<Runnable> list  = super.shutdownNow();
        executorSet.remove(this);
        return list;
    }

}
