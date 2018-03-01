package com.kcz.common;

import com.kcz.configure.FormRepeatException;
import com.kcz.configure.Token;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;

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
    @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(token)")
    public void testToken(final JoinPoint joinPoint, Token token){
        try {
            if (token != null) {
                //获取 joinPoint 的全部参数
                Object[] args = joinPoint.getArgs();
                HttpServletRequest request = null;
                HttpServletResponse response = null;
                for (int i = 0; i < args.length; i++) {
                    //获得参数中的 request && response
                    if (args[i] instanceof HttpServletRequest) {
                        request = (HttpServletRequest) args[i];
                    }
                    if (args[i] instanceof HttpServletResponse) {
                        response = (HttpServletResponse) args[i];
                    }
                }

                boolean needSaveSession = token.save();
                if (needSaveSession){
                    String uuid = UUID.randomUUID().toString();
                    request.getSession().setAttribute( "token" , uuid);
                    logger.debug("进入表单页面，Token值为："+uuid);
                }

                boolean needRemoveSession = token.remove();
                if (needRemoveSession) {
                    if (isRepeatSubmit(request)) {
                        logger.error("表单重复提交");
                        throw new FormRepeatException("表单重复提交");
                    }
                    request.getSession(false).removeAttribute( "token" );
                }
            }

        } catch (FormRepeatException e){
            throw e;
        } catch (Exception e){
            logger.error("token 发生异常 : "+e);
        }
    }

    private boolean isRepeatSubmit(HttpServletRequest request) throws FormRepeatException {
        String serverToken = (String) request.getSession( false ).getAttribute( "token" );
        if (serverToken == null ) {
            //throw new FormRepeatException("session 为空");
            return true;
        }
        String clinetToken = request.getParameter( "token" );
        if (clinetToken == null || clinetToken.equals("")) {
            //throw new FormRepeatException("请从正常页面进入！");
            return true;
        }
        if (!serverToken.equals(clinetToken)) {
            //throw new FormRepeatException("重复表单提交！");
            return true ;
        }
        logger.debug("校验是否重复提交：表单页面Token值为："+clinetToken + ",Session中的Token值为:"+serverToken);
        return false ;
    }
}