package com.uwindsor.accproject.controller;

import java.util.ArrayList;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uwindsor.accproject.CrawlerForURL;
import com.uwindsor.accproject.DeleteFiles;
import com.uwindsor.accproject.SearchWord;

@RestController
public class WebSearchController {

	@RequestMapping(
			  value = "/webCrawl", 
			  method = RequestMethod.POST,
			  headers = "Accept=application/json"
			 )
	@ResponseBody
	public String WebCrawl (@RequestBody String request) throws Exception {
		JSONObject jsonObj = (JSONObject) new JSONParser().parse(request);
		String url = (String) jsonObj.get("url");
		if (!url.startsWith("https")) {
			url = "https://" + url;
		}
		System.out.println(url);
		Set<String> crawledList = CrawlerForURL.startCrawlerForWeb(url, 0);
		jsonObj = new JSONObject();
		jsonObj.put("links", crawledList);
		return jsonObj.toJSONString();
	}
	
	@RequestMapping(
			  value = "/emptyFiles", 
			  method = RequestMethod.GET,
			  headers = "Accept=application/json"
			 )
	@ResponseBody
	public String EmptyFiles() throws Exception {
		JSONObject jsonObj = new JSONObject();
		String response = "";
		try {
			DeleteFiles.deleteFiles();
			response = "Files deleted successfully!";
		} catch (Exception e) {
			System.out.println("Exception at EmptyFiles: " + e);
			response = "Unable to delete files. Please try again later.";
		}
		jsonObj.put("status", response);
		return jsonObj.toJSONString();
	}
	
	@RequestMapping(
			  value = "/wordSearch", 
			  method = RequestMethod.POST,
			  headers = "Accept=application/json"
			 )
	@ResponseBody
	public String WordSearch(@RequestBody String request) throws Exception {
		JSONObject jsonObj = (JSONObject) new JSONParser().parse(request);
		String wordToSearch = (String) jsonObj.get("wordToSearch");
		int maxFilesList = 0;
		try {
			String maxFiles = jsonObj.get("maxFiles").toString();
			maxFilesList = Integer.parseInt(maxFiles);	
		} catch(Exception e) {
			System.out.println("Exception for parseInt: " + e);
		}
		String response = SearchWord.searchWord(wordToSearch, maxFilesList);
		return response;
	}
	
	@RequestMapping(
			  value = "/suggestWords", 
			  method = RequestMethod.POST,
			  headers = "Accept=application/json"
			 )
	@ResponseBody
	public String SuggestWords(@RequestBody String request) throws Exception {
		JSONObject jsonObj = (JSONObject) new JSONParser().parse(request);
		String word = (String) jsonObj.get("word");
		
		String response = SearchWord.suggestAltWord(word);
		return response;
	}

}
