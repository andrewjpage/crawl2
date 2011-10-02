package org.genedb.crawl.dao.proxy;

import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;

public class DAOFactory {
    
    static Logger logger = Logger.getLogger(DAOFactory.class);
    
    private DAOInvocationHandler invocationHandler;
    
    public void setInvocationHandler(DAOInvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }
    
    public Object getProxy(Class<?> cls) {
        logger.info("setting up proxy " + cls.getName());
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {cls}, invocationHandler);
    }
    
}
