package org.genedb.crawl.dao.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.genedb.crawl.annotations.ListType;

class DAOInvocationHandler implements InvocationHandler {
    
    static Logger logger = Logger.getLogger(DAOInvocationHandler.class);
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        
        logger.info("invoked! " + method.getName());
        
        Class<?> returnType = method.getReturnType();
        JavaType type = null;
        
        if (returnType.equals(List.class)) {
            
            ListType listType = method.getAnnotation(ListType.class);
            
            if (listType != null) {
                listType.value();
                type = TypeFactory.collectionType(List.class, Class.forName(listType.value()));
            }
            
        }
        
        if (type == null)
            type = TypeFactory.type(returnType);
        
        
        return Proxies.proxyRequest(type);
        
    }
    
}