package com.databuck.service;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import org.apache.log4j.Logger;
@Service
public class RBACController {
	
	private static final Logger LOG = Logger.getLogger(RBACController.class);
	
public static boolean rbac(String AccessCOntrol,String Permission,HttpSession session) {
	try{
	Map<String,String> module =(Map<String,String>) session.getAttribute("module");
	//System.out.println("module="+module);
	boolean containsKey = module.containsKey(AccessCOntrol);
	//System.out.println("containsKey="+containsKey);
	boolean containsValue = false;
	//module.containsValue(AccessCOntrol);
	if(containsKey) {
	for(String s:module.get(AccessCOntrol).split("\\-"))
	{
		//System.out.println("s:"+s);
		if(s.equalsIgnoreCase(Permission)){
			containsValue=true;
		}
	}
	}
	//}
	/*for (Map.Entry m : module.entrySet()) {
		System.out.println("idTask="+m.getKey()+"accessControl="+m.getValue());
		 containsValue = Arrays.asList(m.getValue().toString().split("\\-")).contains(AccessCOntrol);
		 for(String s:m.getValue().toString().split("\\-"))
		 {
			System.out.println("s:"+s); 
		 }
	}*/
	//System.out.println("containsValue="+containsValue);
	if(containsKey&&containsValue){
		return true;
	}else
	return false;
	}catch (Exception e) {
		LOG.error(e.getMessage());
		e.printStackTrace();
	}
	return false;
}

public static boolean rbac(String AccessCOntrol,String Permission,Map<String,String> module) {
	try{
	//System.out.println("module="+module);
	boolean containsKey = module.containsKey(AccessCOntrol);
	//System.out.println("containsKey="+containsKey);
	boolean containsValue = false;
	//module.containsValue(AccessCOntrol);
	if(containsKey) {
	for(String s:module.get(AccessCOntrol).split("\\-"))
	{
		//System.out.println("s:"+s);
		if(s.equalsIgnoreCase(Permission)){
			containsValue=true;
		}
	}
	}
	//}
	/*for (Map.Entry m : module.entrySet()) {
		System.out.println("idTask="+m.getKey()+"accessControl="+m.getValue());
		 containsValue = Arrays.asList(m.getValue().toString().split("\\-")).contains(AccessCOntrol);
		 for(String s:m.getValue().toString().split("\\-"))
		 {
			System.out.println("s:"+s); 
		 }
	}*/
	//System.out.println("containsValue="+containsValue);
	if(containsKey&&containsValue){
		return true;
	}else
	return false;
	}catch (Exception e) {
		LOG.error(e.getMessage());
		e.printStackTrace();
	}
	return false;
}
}
