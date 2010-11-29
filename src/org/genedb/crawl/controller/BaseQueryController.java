package org.genedb.crawl.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.genedb.crawl.CrawlException;
import org.genedb.crawl.model.Argument;
import org.genedb.crawl.model.MappedOrganism;
import org.genedb.crawl.model.Resource;
import org.genedb.crawl.model.Service;
import org.genedb.crawl.annotations.ResourceDescription;
import org.gmod.cat.Organisms;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;


public abstract class BaseQueryController {
	
	private Logger logger = Logger.getLogger(BaseQueryController.class);
	
	@RequestMapping(method=RequestMethod.GET, value="/index")
	@ResourceDescription("lists the resources")
	public Service index() {
		
		logger.info("Returning index of " + this);
		
		Service service = new Service();
		
		Class<? extends BaseQueryController> cls = getClass();
		
		
		RequestMapping requestMapping = cls.getAnnotation(RequestMapping.class);
		if (requestMapping != null) {
			
			service.name = requestMapping.value()[0];
		}
		
		ResourceDescription description = cls.getAnnotation(ResourceDescription.class);
		
		if (description != null) {
			service.description = description.value();
		}
		
		Method[] methods = cls.getMethods();
		
		for (Method method : methods) {
			
			Resource resource = new Resource();
			boolean addMethod = false;
			
			resource.returnType = method.getReturnType().getSimpleName();
			
			ResourceDescription methodDescription = method.getAnnotation(ResourceDescription.class);
			if (methodDescription != null) {
				addMethod = true;
				resource.description = methodDescription.value();
			}
			
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation instanceof RequestMapping) {
					
					addMethod = true;
					
					RequestMapping methodRequestMapping = (RequestMapping)annotation;
					
					resource.name = methodRequestMapping.value()[0];
					
					Annotation[][] methodAnnotations = method.getParameterAnnotations();
					
					int index = 0;
					
					Class<?>[] types = method.getParameterTypes();
					
					Map<Integer, Argument> parameterMap = new LinkedHashMap<Integer, Argument>();
					
					for (Annotation[] paramAnnotations : methodAnnotations) {
						
						for (Annotation paramAnnotation : paramAnnotations) {
							
							if (paramAnnotation instanceof RequestParam) {
								
								Argument arg = getOrCreateArgument(parameterMap, index);
									
								RequestParam requestParamAnnotation = (RequestParam)paramAnnotation;
								arg.name = requestParamAnnotation.value();
								
								String defaultValue = requestParamAnnotation.defaultValue();
								if (! defaultValue.equals(ValueConstants.DEFAULT_NONE)) {
									arg.defaultValue = defaultValue;
								}
								
								arg.type = types[index].getSimpleName();
								
							} else if (paramAnnotation instanceof ResourceDescription) {
								
								Argument arg = getOrCreateArgument(parameterMap, index);
								ResourceDescription resourceDescriptionParamAnnotation = (ResourceDescription)paramAnnotation;
								arg.description = resourceDescriptionParamAnnotation.value();
								
							}
							
						}
						
						index++;
					}
					
					
					resource.args = new ArrayList<Argument> ( parameterMap.values() );
					
					
				} 
				
				
				
			}
			
			if (addMethod) {
				service.resources.add(resource);
			}
		}
		
		
		
		
		
		return service;
	}
	
	private Argument getOrCreateArgument(Map<Integer, Argument> arguments, int index) {
		if (! arguments.containsKey(index)) {
			arguments.put(index, new Argument());
		}
		return arguments.get(index);
	}
	
	protected MappedOrganism getOrganism(Organisms organisms, String organism) throws CrawlException {
		MappedOrganism mappedOrganism = null;
		
		if (organism.contains(":")) {
			String[] split = organism.split(":");
			
			if (split.length == 2) {
					
				String prefix = split[0];
				String orgDescriptor = split[1];
				
				if (prefix.equals("com")) {
					mappedOrganism = organisms.getByCommonName(orgDescriptor);
				} else if (prefix.equals("tax")) {
					mappedOrganism = organisms.getByTaxonID(orgDescriptor);
				} else if (prefix.equals("org")) {
					mappedOrganism = organisms.getByID(Integer.parseInt(orgDescriptor));
				}
				
			}
			
		} else {
			
			mappedOrganism = organisms.getByCommonName(organism);
			
		}
		
		return mappedOrganism;
	}
	
	
}