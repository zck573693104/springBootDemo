package com.kcz.common;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.kcz.*.*(..))" )
    private void serviceAspect() {
    }

    @Around(value = "serviceAspect()")
    public Object doServiceLog(ProceedingJoinPoint proceed) throws Throwable {
        long start = System.nanoTime();
        boolean var11 = false;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String param = "Request Address:" + request.getRequestURL().toString()+"\t"+"Request Method:" + request.getMethod()+"\t"+"Request Class Method:" + proceed.getSignature()
                +"\t"+"Request Param:" + Arrays.toString(proceed.getArgs())+"\t";
        String time = "totalTime:";
        Object var5;
        try {
            var11 = true;
            this.writeBeginLog(proceed);
            Object e = proceed.proceed();
            this.writeEndLog(proceed, e);
            var5 = e;
            var11 = false;
        } catch (Exception var12) {
            //this.writeExceptionLog(proceed, var12);
            throw var12;
        } finally {
            if (var11) {
                String methodIdentifier1 = this.getMethodIdentifier(proceed);
                logger.warn("{} {} {}", param,time, Long.valueOf((System.nanoTime() - start) / 1000000L));
            }
        }

        String methodIdentifier = this.getMethodIdentifier(proceed);
        logger.warn("{} {} {}",param, time,Long.valueOf((System.nanoTime() - start) / 1000000L));
        return var5;
    }
    private void writeBeginLog(JoinPoint joinPoint) {

    }

    private void writeEndLog(JoinPoint joinPoint, Object returnValue) {

    }
    private void writeExceptionLog(JoinPoint joinPoint, Exception e) {
        String methodIdentifier = this.getMethodIdentifier(joinPoint);
        if (!(e instanceof Exception) && !this.isValidateException(e)) {
            logger.warn("结束【{}】，结果异常，异常信息为：{}", methodIdentifier, e.getMessage());
            logger.warn(String.format("调用%s时异常结束", new Object[]{methodIdentifier}), e);
        } else {
            logger.warn("结束【{}】，结果异常，异常信息为：{}", methodIdentifier, e.getMessage());
        }

        logger.warn(String.format("%1$s, %2$s end.", new Object[]{this.getSessionId(), methodIdentifier}));
    }
    private String getMethodIdentifier(JoinPoint joinPoint) {
        try {
            return String.format("%s.%s", new Object[]{joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName()});
        } catch (Exception var3) {
            return "Get Method Name Exception." + var3.getMessage();
        }
    }
    private String getSessionId() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        return attrs == null ? "session is null." : attrs.getSessionId();
    }

    private boolean isValidateException(Exception ex) {
        return ex != null && FieldUtils.getField(ex.getClass(), "isLwValidateException", true) != null;
    }
}