package com.uwindsor.acc.project;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Main class for running the code
 * and interacting with the end user
 * 
 * @author 
 * Soham Goswami
 * Shalin Shah
 * Harsh Prajapati
 * Aman Chauhan
 * Rajan Yadav
 *
 */
public class WebSearchEngine {
	
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		String choice = "y";
		do {
			System.out.println("----------------- Welcome to the Web Search Engine ----------------------");
			System.out.println("*************************************************************************");
			System.out.println("     Enter the URL you would like us to Crawl (starting with 'www')      ");
			System.out.println("*************************************************************************");
			String url = sc.next();
			if (!url.startsWith("https")) {
				url = "https://" + url;
			}
			System.out.println(url);
			choice = (WebSearchEngine.findWordInWebPage(url)).toLowerCase();	
		}	
		while (choice.equals("y"));
		
		System.out.println("*************************************************************************");
		System.out.println("                 You have reached the end of application                 ");
		System.out.println("*************************************************************************");
		
	}

	private static String findWordInWebPage(String URL) {
		
		if(!isValid(URL)) {
			 System.out.println("Invalid URL entered: " + URL);
			 System.out.println("Please try again.");
			 return "";
		}
		
		System.out.println("---- Validation successful ----");
		
		System.out.println("Starting Crawling on: " + URL);
		// Crawling URL
		CrawlerForURL.startCrawlerForWeb(URL, 0); 	
		System.out.println("Crawling ended...");

		// Hash table is used instead of Hash Map as it don't allow null value in insertion
		Hashtable<String, Integer> fileList = new Hashtable<String, Integer>();
		
		String choice = "y";
		do {
			System.out.println("*************************************************************************");
			System.out.println("                     Enter a word you want to search                     ");
			System.out.println("*************************************************************************");
			String wordToSearch = sc.next();
			int wordFrequency = 0;
			int totalFiles = 0;
			fileList.clear();
			try {
				System.out.println("Searching...");
				File files = new File(Path.txtDirectoryPath);

				File[] ArrayofFiles = files.listFiles();

				for (int i = 0; i < ArrayofFiles.length; i++) {

					In data = new In(ArrayofFiles[i].getAbsolutePath());
					String txt = data.readAll();
					data.close();
					Pattern p = Pattern.compile("::");
					String[] file_name = p.split(txt);
					wordFrequency = SearchWord.wordSearch(txt, wordToSearch.toLowerCase(), file_name[0]); // search word in txt files

					if (wordFrequency != 0) {
						fileList.put(file_name[0], wordFrequency);
						totalFiles++;
					}
				}

				if(totalFiles>0) {
				System.out.println("Total Number of Files containing word : " + wordToSearch + " is : " + totalFiles);
				}else {
					System.out.println(" File not found! containing word : "+ wordToSearch);
					SearchWord.suggestAltWord(wordToSearch.toLowerCase()); // suggest another word if entered word not found
				}

				SearchWord.rankFiles(fileList, totalFiles); 				   //rank the files based on frequency of word count
				

			} catch (Exception e) {
				System.out.println("Exception:" + e);
			}
			System.out.println(" Do you want return to search another word(y/n)?");
			choice = sc.next().toLowerCase();
		} while (choice.equals("y"));
		
		deleteFiles();					// delete the files created if the user do not want to search any other words and want to start with new URL
		
		System.out.println(" Do you want return to main menu(y/n)?");   // returns to the main menu to choose from new/static URL or exit the code. 
		return sc.next();
	}

	// deletes all files created while crawling and word search.
	private static void deleteFiles() {
		File files = new File(Path.txtDirectoryPath);
		File[] ArrayofFiles = files.listFiles();

		for (int i = 0; i < ArrayofFiles.length; i++) {
			ArrayofFiles[i].delete();
		}
		
		File HTMLFiles= new File(Path.htmlDirectoryPath);
		File[] fileArrayhtml = HTMLFiles.listFiles();

		for (int i = 0; i < fileArrayhtml.length; i++) {
			
			fileArrayhtml[i].delete();
		}
	}
	
	/**
	 * It will validate URL entered by user with DNS
	 * @param url
	 * @return
	 */
	public static boolean isValid(String URL)
    {
        /* Try creating a valid URL */
        try {
        	System.out.println("Validating URL...");
        	URL obj = new URL(URL);
            HttpURLConnection CON = (HttpURLConnection) obj.openConnection();
            //Sending the request
            CON.setRequestMethod("GET");
            int response = CON.getResponseCode();
            if(response==200) {
            	 return true;
            }else {
            	return false;
            }
           
        }
          
        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

}
