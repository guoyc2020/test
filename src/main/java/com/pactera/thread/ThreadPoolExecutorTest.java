package com.pactera.thread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 1.使用executor.execute方法时，try/catch execute方法捕获不到异常；需要通过自定义ThreadFactory捕获并处理异常。
 * 2.使用executor.submit方法时，不需要自定义ThreadFactory处理异常；通过try/catch get方法可以捕获异常。
 *  submit方法传入Runnable实现类时，可以捕获异常，返回值为null
 *  传入Callable实现类时，可以捕获异常，返回值为实现类中指定。
 * 3.ThreadPoolExecutor线程池被shutdown后，线程池不可以用，再发送任务请求触发异常处理机制。
 */
public class ThreadPoolExecutorTest {
    static int corePoolSize = 2; //核心线程数
    static int maximumPoolSize = 4; //最大的线程数
    static int keepAliveTime = 10; //最大空闲时间
    static int queueSize = 3; // 队列大小

    public static void main(String[] args) {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize),
//                new ThreadFactory() {
//                    final AtomicInteger threadNumber = new AtomicInteger(1);
//
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread t = new Thread(Thread.currentThread().getThreadGroup(), r, "run-task-thread-" + (threadNumber.getAndIncrement()));
//                        String name = t.getName();
//                        //System.out.println(name + "----------------- has been created");
//
//                        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                            @Override
//                            public void uncaughtException(Thread t, Throwable e) {
//                                System.out.println("cuowu111");
//                                e.printStackTrace();
//                            }
//                        });
//                        return t;
//                    }
//                },
                new ThreadPoolExecutor.AbortPolicy());


        ArrayList<Integer> lists = new ArrayList<>();
        lists.add(1);
        lists.add(2);
        lists.add(3);
        lists.add(4);
        lists.add(5);
        lists.add(6);
        lists.add(7);

        LinkedList<Future<?>> collect = new LinkedList<>();
        for (int list : lists) {
//			try {
//				executor.execute(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							int x = 1/0;
//						} catch (Exception e) {
//							throw e;
//						} finally {
//
//                        }
//					}
//				});
//			} catch (Exception e) {
//				System.out.println("被零除！！！");
//			}

            Future<?> submit = executor.submit(
                new Runnable() {
                   @Override
                   public void run() {
                       System.out.println(Thread.currentThread().getName());
                       if (list==3) {
                           int x = 1/0;
                       }

                       if (list==4) {
                           throw new RuntimeException("不能是4");
                       }
                   }
               }
            );

            collect.add(submit);
        }

        for (Future<?> future : collect) {
            try {
                Object object = future.get();
                System.out.println("返回值->" + object); //null
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("cuowu------------");
            }
        }

        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                break;
            }
        }
        System.out.println("所有任务全部执行完成，请检查日志查看是否存在异常信息！！");
    }

}

