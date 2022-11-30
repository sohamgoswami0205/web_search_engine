package com.uwindsor.acc.project;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerForURL {

	// For avoiding duplicate value entry, using HashSet 
	private static Set<String> crawledLst = new HashSet<String>();
	// For performance and volume of values - setting maxDepth as 3
	private static int maxDepth = 3;
	private static String regExp = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

	/**
	 * 
	 * @param url: URL to crawl
	 * @param depth: Depth at which the crawler should crawl
	 */
	public static void startCrawlerForWeb(String url, int depth) {
		Pattern patternObj = Pattern.compile(regExp);
		if (depth <= maxDepth) {
			try {
				Document doc = Jsoup.connect(url).get();
				URLParser.saveDocument(doc, url);
				depth++;
				if (depth < maxDepth) {
					Elements links = doc.select("a[href]");
					for (Element page : links) {

						if (verifyURL(page.attr("abs:href")) && patternObj.matcher(page.attr("href")).find()) {
							
							System.out.println(page.attr("abs:href"));
							startCrawlerForWeb(page.attr("abs:href"), depth);
							crawledLst.add(page.attr("abs:href"));
						}
					}
				}
			} catch (Exception e) {
				System.out.println("Error in crawling: \n" + e);
			}
		}
	}

	/**
	 * Verify the format of extracted URL
	 * @param newUrl: Extracted URL under verification
	 * @return boolean: Correct URL = true, Incorrect URL = false
	 */
	private static boolean verifyURL(String newUrl) {
		if (crawledLst.contains(newUrl)) {
			return false;
		}
		if (newUrl.endsWith(".jpeg") || newUrl.endsWith(".jpg") || newUrl.endsWith(".png")
				|| newUrl.endsWith(".pdf") || newUrl.contains("#") || newUrl.contains("?")
				|| newUrl.contains("mailto:") || newUrl.startsWith("javascript:") || newUrl.endsWith(".gif")
				||newUrl.endsWith(".xml")) {
			return false;
		}
		return true;
	}
}
