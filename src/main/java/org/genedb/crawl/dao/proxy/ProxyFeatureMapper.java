package org.genedb.crawl.dao.proxy;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.genedb.crawl.client.CrawlClient;
import org.genedb.crawl.dao.FeatureDAO;
import org.genedb.crawl.json.JsonIzer;
import org.genedb.crawl.model.Argument;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;
import org.codehaus.jackson.map.type.TypeFactory;


public class ProxyFeatureMapper {
    
    private String[] urls;
    
    private JsonIzer jsonIzer = new JsonIzer();
    
    public void proxy () {
        
        Proxy.newProxyInstance(
                ProxyFeatureMapper.class.getClassLoader(), 
                new Class[] {FeatureDAO.class}, 
                
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object obj, Method method, Object[] arguments) throws Throwable {
                        
                        String mapperName = "features";
                        String methodName = method.getName();
                        
                        Class returnType = method.getReturnType();
                        
                        Annotation[][] methodAnnotations = method.getParameterAnnotations();
                        
                        List<String> argNames = new ArrayList<String>();
                        
                        for (Annotation[] paramAnnotations : methodAnnotations) {
                            for (Annotation paramAnnotation : paramAnnotations) {
                                if (paramAnnotation instanceof RequestParam) {
                                    RequestParam requestParamAnnotation = (RequestParam)paramAnnotation;
                                    String argName = requestParamAnnotation.value();
                                    argNames.add(argName);
                                } 
                            }
                        }
                        
                        assert (argNames.size() == arguments.length);
                        
                        Map<String,String[]> parameters = new HashMap<String,String[]>();
                        
                        for (int i = 0; i < argNames.size(); i++) {
                            String argName = argNames.get(i);
                            Object value = arguments[i];
                            
                            List<String> valueStrings = new ArrayList<String>();
                            
                            Object[] valueArray = (Object[]) value;
                            
                            for (Object v : valueArray) {
                                String vString = v.toString();
                                valueStrings.add(vString);
                            }
                            
                            parameters.put(argName, valueStrings.toArray(new String[valueStrings.size()]));
                        }
                        
                        for (String url : urls) {
                            
                            CrawlClient client = new CrawlClient(url);
                            
                            String result = client.result(mapperName, methodName, parameters);
                            
                            TypeReference tr = new TypeReference<List>() {};
                            
                            JsonParser parser = jsonIzer.getParser(result);
                            
                            
                            
//                            jsonIzer.getMapper().readValue(
//                                  result.toString(), 
//                                  TypeFactory.collectionType(ArrayList.class, Object.class));
//                            
//                            
//                            TypeFactory.fromCanonical("org.genedb.crawl.model.Feature");
//                            
//                            JavaType jt = TypeFactory.collectionType(ArrayList.class, returnType);
                            
                            //TypeFactory.
                            
                            //jsonIzer.getMapper().
                            
                        }
                        
                        
                        // TODO Auto-generated method stub
                        return null;
                    }
                    
                });
        
    }
    
    private void parseJson(JsonParser jp) throws JsonParseException, IOException {
        if (jp.nextToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();
            
            
            
        }
        jp.close();
    }

}
