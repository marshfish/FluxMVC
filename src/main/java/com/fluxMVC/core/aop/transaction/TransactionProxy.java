package com.fluxMVC.core.aop.transaction;

import com.fluxMVC.core.aop.proxy.Proxy;
import com.fluxMVC.core.aop.proxy.ProxyChain;
import com.fluxMVC.core.initialize.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/10
 */
public class TransactionProxy implements Proxy {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProxy.class);
    private static final ThreadLocal<Boolean> FLAG_HOLDER = ThreadLocal.withInitial(() -> false);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;
        boolean flag = FLAG_HOLDER.get();
        Method targetMethod = proxyChain.getTargetMethod();
        if (!flag && targetMethod.isAnnotationPresent(Transaction.class)) {
            FLAG_HOLDER.set(true);
            try {
                TransactionHandler.beginTransaction();
                logger.debug("begin transaction");
                result = proxyChain.doProxyChain();
                TransactionHandler.commitTransaction();
                logger.debug("commit transaction");
            } catch (Exception e) {
                TransactionHandler.rollbackTransaction();
                logger.debug("rollback transaction");
            } finally {
                FLAG_HOLDER.remove();
            }
        } else {
            result = proxyChain.doProxyChain();
        }
        return result;
    }
}
