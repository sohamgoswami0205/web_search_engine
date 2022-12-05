package com.uwindsor.accproject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SearchWord {
	static ArrayList<String> key = new ArrayList<String>();
	static HashMap<String, Integer> numbers = new HashMap<String, Integer>();
	static Hashtable<String, Integer> fileList = new Hashtable<String, Integer>();
	
	public static String searchWord(String wordToSearch, int maxFilesList) {
		int wordFrequency = 0;
		int totalFiles = 0;
		JSONObject jsonObj = new JSONObject();
		try {
			fileList.clear();
			System.out.println("Searching...");
			File files = new File(Path.txtDirectoryPath);

			File[] ArrayofFiles = files.listFiles();
			ArrayList<Object> wordFiles = new ArrayList();

			for (int i = 0; i < ArrayofFiles.length; i++) {

				In data = new In(ArrayofFiles[i].getAbsolutePath());
				String txt = data.readAll();
				data.close();
				Pattern p = Pattern.compile("::");
				String[] file_name = p.split(txt);
				wordFrequency = wordSearch(txt, wordToSearch.toLowerCase(), file_name[0]); // search word in txt files
				if (wordFrequency != 0) {
					fileList.put(file_name[0], wordFrequency);
					totalFiles++;
				}
			}

			if(totalFiles > 0) {
				System.out.println("Total Number of Files containing word : " + wordToSearch + " is : " + totalFiles);
				jsonObj.put("total_files", "" + totalFiles);
			} else {
				System.out.println(" File not found! containing word : "+ wordToSearch);
				jsonObj.put("total_files", "" + totalFiles);
				suggestAltWord(wordToSearch.toLowerCase()); // suggest another word if entered word not found
			}

			wordFiles = rankFiles(fileList, totalFiles, maxFilesList); 				   //rank the files based on frequency of word count
			jsonObj.put("list", wordFiles);

		} catch (Exception e) {
			System.out.println("Exception:" + e);
			jsonObj.put("list", null);
			jsonObj.put("total_files", "" + totalFiles);
		}
		return jsonObj.toJSONString();
	}
	
	// Positions and Occurrences of the words
		@SuppressWarnings("finally")
		public static int wordSearch (String data, String word, String fileName)
				throws ArrayIndexOutOfBoundsException {
			int cntr = 0;
			try {

				int offset = 0;
				BoyerMoore bm = new BoyerMoore(word);

				for (int location = 0; location <= data.length(); location += offset + word.length()) {
					offset = bm.search(data.substring(location));
					if ((offset + location) < data.length()) {
						cntr++;
					}
				}
				if (cntr != 0) {
					System.out.println("Found in --> " + fileName +" --> "+ cntr +" times");
					System.out.println("-------------------------------------------------------------------------");
																												
				}	
			}
			catch (Exception e) {
				System.out.println("Exception in Search Word: " + e);
			}
			finally {
				return cntr;
			}
		}

		// Merge-sort for ranking of the pages
		public static ArrayList<Object> rankFiles(Hashtable<String, Integer> files, int occur, int maxFilesList) {

			ArrayList<Object> wordFiles = new ArrayList();
			if (occur < 1) {
				return wordFiles;
			}
			
			// Transfer as List and sort it
			ArrayList<Map.Entry<String, Integer>> fileList = new ArrayList<Map.Entry<String, Integer>>(files.entrySet());

			Collections.sort(fileList, new Comparator<Map.Entry<String, Integer>>() {

				public int compare(Map.Entry<String, Integer> obj1, Map.Entry<String, Integer> obj2) {
					return obj1.getValue().compareTo(obj2.getValue());
				}
			});

			Collections.reverse(fileList);
			int filesToIterate = maxFilesList < 1 ? fileList.size() : Math.min(maxFilesList, fileList.size());
			for (int i = 0; i < filesToIterate; i++) {
				if (fileList.get(i).getKey() != null) {
					JSONObject newFilesObject = new JSONObject();
					System.out.println("Key ==> " + fileList.get(i).getKey());
					if (fileList.get(i).getKey() != null) {
						newFilesObject.put("name", fileList.get(i).getKey().toString());
						newFilesObject.put("frequency", fileList.get(i).getValue());
						wordFiles.add(newFilesObject);
					}
				}
			}
			
			return wordFiles;

		}

		public static String suggestAltWord(String wordToSearch) {
			ArrayList<String> suggestedWords = new ArrayList();
			String line = " ";
			String regex = "[a-z0-9]+";

			// Create a Pattern object
			Pattern pattern = Pattern.compile(regex);
			// Now create matcher object.
			Matcher matcher = pattern.matcher(line);
			int fileNumber = 0;

			File dir = new File(Path.txtDirectoryPath);
			File[] fileArray = dir.listFiles();
			for (int i = 0; i < fileArray.length; i++) {
				try {
					findWord(fileArray[i], fileNumber, matcher, wordToSearch);
					fileNumber++;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			Integer allowedDistance = 1; // Edit distance allowed
			boolean matchFound = false; // set to true if word found with edit distance equal to allowedDistance

			
			int i = 0;
			for (Map.Entry entry : numbers.entrySet()) {
				if (allowedDistance == entry.getValue()) {
					
					i++;
					
					if(i==1)
					System.out.println("Did you mean? ");
					
					System.out.print("(" + i + ") " + entry.getKey() + "\n");
					suggestedWords.add(entry.getKey().toString());
					matchFound = true;
				}
			}
			if (!matchFound) {
				System.out.println("Entered word cannot be resolved....");
				suggestedWords = null;
			}
			JSONObject jsonObj = new JSONObject();
			if (suggestedWords != null) {
				jsonObj.put("status", "Matches Found");
				jsonObj.put("suggestions", suggestedWords);
			} else {
				jsonObj.put("status", "No Matches Found");
				jsonObj.put("suggestions", null);
			}
			return jsonObj.toJSONString();
		}

		// finds strings with similar pattern and calls edit distance() on those strings
		public static void findWord(File sourceFile, int fileNumber, Matcher match, String str)
				throws FileNotFoundException, ArrayIndexOutOfBoundsException {
			try {
				BufferedReader br = new BufferedReader(new FileReader(sourceFile));
				String line = null;

				while ((line = br.readLine()) != null) {
					match.reset(line);
					while (match.find()) {
						key.add(match.group());
					}
				}

				br.close();
				if (!key.isEmpty()) {
					for (int p = 0; p < key.size(); p++) {
						numbers.put(key.get(p), editDistance(str.toLowerCase(), key.get(p).toLowerCase()));
					}
				}
			} catch (Exception e) {
				System.out.println("Exception  :" + e);
			}

		}

		public static int editDistance(String str1, String str2) {
			int len1 = str1.length();
			int len2 = str2.length();

			int[][] my_array = new int[len1 + 1][len2 + 1];

			for (int i = 0; i <= len1; i++) {
				my_array[i][0] = i;
			}

			for (int j = 0; j <= len2; j++) {
				my_array[0][j] = j;
			}

			// iterate though, and check last char
			for (int i = 0; i < len1; i++) {
				char c1 = str1.charAt(i);
				for (int j = 0; j < len2; j++) {
					char c2 = str2.charAt(j);

					if (c1 == c2) {
						my_array[i + 1][j + 1] = my_array[i][j];
					} else {
						int replace = my_array[i][j] + 1;
						int insert = my_array[i][j + 1] + 1;
						int delete = my_array[i + 1][j] + 1;

						int min = replace > insert ? insert : replace;
						min = delete > min ? min : delete;
						my_array[i + 1][j + 1] = min;
					}
				}
			}

			return my_array[len1][len2];
		}

}
