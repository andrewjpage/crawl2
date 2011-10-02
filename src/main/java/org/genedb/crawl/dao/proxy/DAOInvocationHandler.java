package org.genedb.crawl.dao.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

class DAOInvocationHandler implements InvocationHandler {
    
    static Logger logger = Logger.getLogger(DAOInvocationHandler.class);
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        DAOFactory.logger.info("invoked! " + method.getName());
        
        Class<?> returnType = method.getReturnType();
        logger.info("returnType");
        logger.info(returnType);
        
        return Proxies.proxyRequest(returnType);
        
    }
    
}