package com.kalzn.judger.aspect.logger;


import com.kalzn.judger.config.JudgerServerConfig;
import com.kalzn.judger.service.judgehandle.config.TaskConfig;
import com.kalzn.judger.service.judgehandle.config.TaskRequestConfig;
import com.kalzn.judger.leaderserver.LeaderServer;
import com.kalzn.judger.service.judgehandle.runbox.Runbox;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("judgerLogger")
@Aspect
public class JudgerLogger {


    @Pointcut("execution(public * com.kalzn.judger.leaderserver.LeaderServer.*(..))")
    public void logDataRequestPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.runbox.Runbox.run())")
    public void logTaskStartPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.accept(..))")
    public void logTaskCommitPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.submit(..))")
    public void logTaskSubmitPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.start(..))")
    public void logManagerStartPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.restart(..))")
    public void logManagerRestartPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.pause(..))")
    public void logManagerPausePoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.shutdown(..))")
    public void logManagerShutdownPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.resetData(..))")
    public void logManagerResetDataPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.releaseData(..))")
    public void logManagerReleaseDataPoint(){}
    @Pointcut("execution(* com.kalzn.judger.service.judgehandle.manager.*.impl.*.registerData(..))")
    public void logManagerRegisterDataPoint(){}
    @Pointcut("execution(public void com.kalzn.judger.config.JudgerServerConfig.*(..))")
    public void logJudgerServerConfigModify(){}

    @Around("logDataRequestPoint()")
    public Object logDataRequestAdvice(ProceedingJoinPoint pjp) throws Throwable{

        Logger logger = LoggerFactory.getLogger(LeaderServer.class);
        logger.info("Data request has been sent with :" + pjp.getSignature().getName());
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
        } catch (Throwable e) {
            logger.error("Data request error");
            throw e;
        } finally {
            logger.info("Data request has been done with :" + pjp.getSignature().getName());
        }
        return returnValue;
    }

    @Around("logTaskStartPoint()")
    public Object logRunboxPointAdvice(ProceedingJoinPoint pjp) {
        Logger logger = LoggerFactory.getLogger(Runbox.class);
        logger.info("Judge task start taskID :" + ((Runbox)pjp.getTarget()).getTaskConfig().getRealTimeID());
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
        } catch (Throwable e) {
            logger.error("Run task error" + "\n" + e);
        } finally {
        }
        return returnValue;
    }

    @Around("logTaskCommitPoint()")
    public Object logTaskCommitPointAdvice(ProceedingJoinPoint pjp) {

        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        if (((TaskConfig)pjp.getArgs()[0]).getExceptionCode() != TaskConfig.SUCCESS) {
            logger.error("Judge Task error : taskID :" +  ((TaskConfig)pjp.getArgs()[0]).getRealTimeID() + " " + ((TaskConfig)pjp.getArgs()[0]).getTaskInfo() );
        } else {
            logger.info("Judge Task done (Task has been committed) : taskID : " +  ((TaskConfig)pjp.getArgs()[0]).getRealTimeID() + " " + ((TaskConfig)pjp.getArgs()[0]).getTaskInfo());
        }
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
        } catch (Throwable e) {
            logger.error("Commit task error taskID : " + ((TaskRequestConfig)pjp.getArgs()[0]).getTaskID() + "\n" + e);
        } finally {
        }
        return returnValue;
    }

    @Around("logTaskSubmitPoint()")
    public Object logTaskSubmitPointAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        logger.info("Try submit task taskID :" + ((TaskRequestConfig)pjp.getArgs()[0]).getTaskID());
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
            logger.info("Task request has been submitted taskID : " + ((TaskRequestConfig)pjp.getArgs()[0]).getTaskID());
        } catch (Throwable e) {
            logger.error("Task request error taskID : " + ((TaskRequestConfig)pjp.getArgs()[0]).getTaskID() + "\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }

    @Around("logManagerStartPoint()")
    public Object logManagerStartPointAdvice(ProceedingJoinPoint pjp) throws Throwable{
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
            logger.info("Manager has started");
        } catch (Throwable e) {
            logger.error("Start manager error : " + "\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }

    @Around("logManagerRestartPoint()")
    public Object logManagerRestartPointAdvice(ProceedingJoinPoint pjp) throws Throwable {
        return logManagerStartPointAdvice(pjp);
    }

    @Around("logManagerPausePoint()")
    public Object logManagerPausePointAdvice(ProceedingJoinPoint pjp) throws Throwable{
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
            logger.info("Manager has paused");
        } catch (Throwable e) {
            logger.error("Pause manager error : "  + "\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }

    @Around("logManagerShutdownPoint()")
    public Object logManagerShutdownPointAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        logger.info("Try shutdown manager");
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
            logger.info("Manager has shutdown");
        } catch (Throwable e) {
            logger.error("Shutdown manager error" + "\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }


    @Around("logManagerResetDataPoint()")
    public Object logManagerResetDataPointAdvice(ProceedingJoinPoint pjp) throws Throwable{

        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        logger.info("Start reset data space");
        Object returnValue = null;
        try {
            Object[] args = pjp.getArgs();
            returnValue = pjp.proceed(args);
            logger.info("Reset data done");
        } catch (Throwable e) {
            logger.error("Reset data error\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }
    @Around("logManagerReleaseDataPoint()")
    public Object logManagerReleaseDataPointAdvice(ProceedingJoinPoint pjp) throws Throwable{

        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        Object[] args = pjp.getArgs();
        Object returnValue = null;
        try {
            returnValue = pjp.proceed(args);
            logger.info("release data done dataID : " + args[0]);
        } catch (Throwable e) {
            logger.error("release data error dataID : " + args[0] + "\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }

    @Around("logManagerRegisterDataPoint()")
    public Object logManagerRegisterDataPointAdvice(ProceedingJoinPoint pjp) throws Throwable{

        Logger logger = LoggerFactory.getLogger(pjp.getTarget().getClass());
        Object[] args = pjp.getArgs();
        Object returnValue = null;
        try {
            returnValue = pjp.proceed(args);
            if (args != null && args.length >= 1)
                logger.info("register data done dataID : " + args[0]);
            else
                logger.info("register data done total : " + returnValue);
        } catch (Throwable e) {
            logger.error("Rest data error\n" + e);
            throw e;
        } finally {
        }
        return returnValue;
    }

    @Around("logJudgerServerConfigModify()")
    public Object logJudgerServerConfigModifyAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Logger logger = LoggerFactory.getLogger(JudgerServerConfig.class);
        Object[] args = pjp.getArgs();
        Object returnValue = null;
        if (args.length >= 1) {
            returnValue = pjp.proceed(args);
            logger.warn("Server config has been updated -- config item : <" + pjp.getSignature().getName().substring(3) + "> new value : <" +  args[0].toString()+">" );
            return returnValue;
        } else {
            return pjp.proceed(args);
        }
    }
}
