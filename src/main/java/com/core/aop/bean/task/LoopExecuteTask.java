 package com.core.aop.bean.task;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 public abstract class LoopExecuteTask
   implements Runnable
 {
   private static final Logger log = LoggerFactory.getLogger(LoopExecuteTask.class);
   private long sleepTime = 1000L;
   private long reportIntervalSecond = 60L;
   private long executeCount;
   private long failedCount;
   private Thread thread;
   private volatile boolean running = false;
   
   public LoopExecuteTask()
   {
     this(0L);
   }
   
   public LoopExecuteTask(long sleepTime)
   {
     this.sleepTime = sleepTime;
   }
   
   public final long getSleepTime()
   {
     return this.sleepTime;
   }
   
   public long getReportIntervalSecond()
   {
     return this.reportIntervalSecond;
   }
   
   public long getFailedCount()
   {
     return this.failedCount;
   }
   
   public LoopExecuteTask setReportIntervalSecond(long reportIntervalSecond)
   {
     this.reportIntervalSecond = reportIntervalSecond;
     return this;
   }
   
   public long getExecuteCount()
   {
     return this.executeCount;
   }
   
   public Thread getThread()
   {
     return this.thread;
   }
   
   public boolean isRunning()
   {
     return this.running;
   }
   
   public final LoopExecuteTask setSleepTime(long sleepTime)
   {
     this.sleepTime = sleepTime;
     return this;
   }
   
   public final void run()
   {
     if (this.thread != null) {
       throw new IllegalStateException("LoopExecuteTask Object can't be running in multi-thread.");
     }
     onStart();
     
     this.thread = Thread.currentThread();
     String threadName = this.thread.getName();
     if (log.isInfoEnabled()) {
       log.info("{} is starting...", threadName);
     }
     this.executeCount = 0L;
     this.failedCount = 0L;
     long totalSpendTime = 0L;
     long lastReportTime = System.currentTimeMillis();
     this.running = true;
     long retrySleepTime = 100L;
     label167:
     while (!this.thread.isInterrupted())
     {
       this.executeCount += 1L;
       try
       {
         long t0 = System.currentTimeMillis();
         boolean willContinue = execute();
         long t = System.currentTimeMillis() - t0;
         totalSpendTime += t;
         if (!willContinue) {
           sleep();
         }
         retrySleepTime = 100L;
       }
       catch (Exception e)
       {
         this.failedCount += 1L;
         boolean willContinue = handle(e);
         if (willContinue) {
           break label167;
         }
       }
       break;
       retrySleepTime *= 2l;
       if (retrySleepTime > this.sleepTime) {
         retrySleepTime = this.sleepTime;
       }
       sleep(retrySleepTime);
       
 
 
       long t = System.currentTimeMillis() - lastReportTime;
       if (t > this.reportIntervalSecond * 1000L)
       {
         if (log.isInfoEnabled()) {
           log.info(
             "{} is running..., executeCount = {}, failedCount = {}, totalSpendTime = {}, avgSpendTime = {}", new Object[] {
             threadName, Long.valueOf(this.executeCount), Long.valueOf(this.failedCount), Long.valueOf(totalSpendTime), Long.valueOf(totalSpendTime / this.executeCount) });
         }
         lastReportTime = System.currentTimeMillis();
       }
     }
     this.running = false;
     if (log.isInfoEnabled()) {
       log.info("{} is stopped...", threadName);
     }
     this.thread = null;
     
     onStop();
   }
   
   protected final void sleep()
   {
     try
     {
       Thread.sleep(this.sleepTime);
     }
     catch (InterruptedException e)
     {
       if (log.isWarnEnabled()) {
         log.warn("{} is Interrupted.", this.thread.getName());
       }
     }
   }
   
   protected final void sleep(long sleepTime)
   {
     try
     {
       Thread.sleep(sleepTime);
     }
     catch (InterruptedException e)
     {
       if (log.isWarnEnabled()) {
         log.warn("{} is Interrupted.", this.thread.getName());
       }
     }
   }
   
   protected boolean handle(Exception e)
   {
     String threadName = this.thread.getName();
     log.error("{} execute occur Exception[{}], cause by {}", new Object[] { threadName, e.getClass().getName(), e.getMessage(), e });
     return true;
   }
   
   protected abstract boolean execute()
     throws Exception;
   
   protected void onStart() {}
   
   protected void onStop() {}
 }