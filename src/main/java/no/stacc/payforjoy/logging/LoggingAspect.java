package no.stacc.payforjoy.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final PayForJoyLogger logger;

    @Autowired
    public LoggingAspect(PayForJoyLogger logger) {
        this.logger = logger;
    }

    @Pointcut("execution(* no.stacc.payforjoy.service..*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* no.stacc.payforjoy.controller..*(..))")
    public void controllerLayer() {}

    @Around("serviceLayer()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.debug("Entering service method: {}.{}", className, methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            logger.debug("Service method {}.{} executed in {} ms", className, methodName, (endTime - startTime));
            return result;
        } catch (Exception e) {
            logger.error("Error in service method {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    @Before("controllerLayer()")
    public void logControllerEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Controller request: {}.{}", className, methodName);
    }

    @AfterThrowing(pointcut = "serviceLayer() || controllerLayer()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.error("Exception in {}.{}: {}", className, methodName, ex.getMessage(), ex);
    }
}

