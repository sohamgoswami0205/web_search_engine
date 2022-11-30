package com.uwindsor.acc.project;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class URLParser {

	/**
	 * this method will save the html file into system
	 * 
	 * @param document
	 * @param url
	 */
	public static void saveDocument(Document document, String url) {
		try {
			PrintWriter htmlFile = new PrintWriter(
					new FileWriter(Path.htmlDirectoryPath + document.title().replace('/', '_') + ".html"));
			htmlFile.write(document.toString());
			htmlFile.close();
			htmlToTextConversion(Path.htmlDirectoryPath + document.title().replace('/', '_') + ".html", url,
					document.title().replace('/', '_') + ".txt");

		} catch (Exception e) {

		}

	}

	/**
	 * this method will save html file content to .txt file
	 * 
	 * @param htmlfile
	 * @param url
	 * @param filename
	 * @throws Exception
	 */
	public static void htmlToTextConversion(String htmlfile, String url, String filename) throws Exception {
		File f = new File(htmlfile);
		Document document = Jsoup.parse(f, "UTF-8");
		String data = document.text().toLowerCase();
		data = url + "::" + data;
		PrintWriter writer = new PrintWriter(Path.txtDirectoryPath + filename);
		writer.println(data);
		writer.close();
	}
}
