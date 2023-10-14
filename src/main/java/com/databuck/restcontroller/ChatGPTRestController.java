package com.databuck.restcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.databuck.service.ChatGPTIntegrationService;

@CrossOrigin(origins = "*")
@RestController
public class ChatGPTRestController {

    @Autowired
    private ChatGPTIntegrationService chatGPTservice;

    @RequestMapping(value = "/dbconsole/chatGPTExp", method = RequestMethod.POST)
    public ResponseEntity<Object> getExpression(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	Map<String, Object> response = new HashMap<>();
	try {
	    response.put("message", "Got Details");
	    response.put("status", "success");
	    response.put("result", chatGPTservice.getRuleExpression(requestBody.get("inputDefination")));
	} catch (Exception e) {
	    response.put("message", "failed");
	    response.put("status", "failed");
	    e.printStackTrace();
	}
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/assessTable", method = RequestMethod.POST)
    public ResponseEntity<Object> assessTable(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, String> requestBody) {
	Map<String, Object> response = new HashMap<>();
	Map<String, Object> result = new HashMap<>();

	try {
	    result = chatGPTservice.assessTable(requestBody.get("inputDefination"));
	    response.put("message", "Got Details");
	    response.put("status", "success");
	    if (result != null && !result.isEmpty()) {
		response.put("result", result);
	    } else {
		response.put("result", "Table not found,Please check your connection details");
	    }

	} catch (Exception e) {
	    response.put("message", "failed");
	    response.put("status", "failed");
	    e.printStackTrace();
	}
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/dbconsole/getValidationDetails", method = RequestMethod.POST)
    public ResponseEntity<Object> getValidationDetails(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, Integer> requestBody) {
	Map<String, Object> response = new HashMap<>();
	Map<String, Object> result = new HashMap<>();

	try {
	    result = chatGPTservice.getValidationDetails(requestBody.get("templateId"));
	    response.put("message", "Got Details");
	    response.put("status", "success");
	    response.put("result", result);

	} catch (Exception e) {
	    response.put("message", "failed");
	    response.put("status", "failed");
	    e.printStackTrace();
	}
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/dbconsole/getValidationFromIdApp", method = RequestMethod.POST)
    public ResponseEntity<Object> getValidationFromIdApp(@RequestHeader HttpHeaders headers,
	    @RequestBody Map<String, Integer> requestBody) {
	Map<String, Object> response = new HashMap<>();
	Map<String, Object> result = new HashMap<>();

	try {
	    result = chatGPTservice.getValidationFromIdApp(requestBody.get("idApp"));
	    response.put("message", "Got Details");
	    response.put("status", "success");
	    response.put("result", result);

	} catch (Exception e) {
	    response.put("message", "failed");
	    response.put("status", "failed");
	    e.printStackTrace();
	}
	return new ResponseEntity<Object>(response, HttpStatus.OK);
    }
    
    

}