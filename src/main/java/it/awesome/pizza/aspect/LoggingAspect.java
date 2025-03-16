package it.awesome.pizza.aspect;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {


    @Before("execution(* it.awesome.pizza..*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @After("execution(* it.awesome.pizza..*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature());
    }
}