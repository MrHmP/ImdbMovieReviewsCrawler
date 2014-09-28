package Crawl;

import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.lang.model.element.Element;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Uses JSOUP to extract the webpage details.
 * Uses OMDAPI to get the moview details.
 * */
public class Crawler {

	public static void main(String args[]) {
		Crawler imdbMoviewCrawler = new Crawler();
		String url = imdbMoviewCrawler.generateURL("inception");
		
		imdbMoviewCrawler.retreiveReviewPage(url,"inception");
	}

	/**
	 * Calls the omdapi for getting the movie details. try:-
	 * http://www.omdbapi.com/?t=inception
	 * then it checks for imdbID field, which is the imdb id for that movie.
	 * so the url of the review page will be:-
	 * "http://www.imdb.com/title/" + id + "/reviews"
	 * */
	public String generateURL(String movieName) {
		try {
			String url = "http://www.omdbapi.com/?t=" + movieName;
			String id = "";
			
			System.out.println("Generating URL");
			movieName = movieName.replaceAll(" ", "%20");

			//1. Get the imdb JSON
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			//2. Parse the JSON to get the imdbID
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(result.toString());
			JSONObject imdbMovieInfoJsonObject = (JSONObject) obj;
			id = (String) imdbMovieInfoJsonObject.get("imdbID");

			//3. Get the imdb url and return it.
			String imdbUrl = "http://www.imdb.com/title/" + id + "/reviews";
			System.out.println("Generated URL: " + imdbUrl);
			return imdbUrl;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Gets the reviews from IMDB from the url.
	 * Fetch the total number of review page.
	 * then fetch the reviews from each page
	 * */
	public void retreiveReviewPage(String imdbMovieURL, String movieName) {
		try {

			if (imdbMovieURL.equals("Error")) {
				System.out.println("Some problem accured");
				return;
			}
			System.out.println("Getting the total number of review pages");

			Document webpageDocument = null;

			webpageDocument = Jsoup
					.connect(imdbMovieURL)
					.timeout(10 * 1000)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
					.get();

			// to get total pages...each page has 10 reviews
			// hard coded method to get the particular element where the reviews are stored.
			//1. Getting the total number of review pages.
			//          -View the imdb website for the DOM node which has the required information
			org.jsoup.nodes.Element table = webpageDocument.getElementsByTag(
					"table").get(1);
			Iterator<org.jsoup.nodes.Element> itr = table.select("td")
					.iterator();
			int totalPages = Integer.parseInt(itr.next().text().split(" ")[3]
					.replace(":", ""));
			System.out.println(totalPages);

			//2. Loop through each review page to get the reviews.
			//   		-Every review page has same url but a trailing query paramter of start=<some number>
			//			-Fetch the required DOM node which has the review in it.
			for (int start = 0; start < totalPages; start++) {

				System.out.println("Getting page: " + (start + 1));
				int counter = 0;
				List<String> reviewList = new ArrayList<>();
				List<String> headingList = new ArrayList<>();

				String modifiedUrl = imdbMovieURL + "?start=" + (start * 10);
				webpageDocument = Jsoup
						.connect(modifiedUrl)
						.timeout(100 * 1000)
						.userAgent(
								"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
						.get();

				org.jsoup.nodes.Element subDocument = webpageDocument
						.getElementById("tn15content");

				Elements review = subDocument.getElementsByTag("p");
				for (org.jsoup.nodes.Element para1 : review) {
					if (!(para1.text().equals("Add another review"))
							&& !(para1.text()
									.equals("*** This review may contain spoilers ***")))
						reviewList.add(counter++, para1.text());
				}
				counter = 0;

				// extracting headings of these reveiws
				Elements heading = subDocument.getElementsByTag("h2");
				for (org.jsoup.nodes.Element para1 : heading) {
					headingList.add(counter++, para1.text());
				}

				for (int i = 0; i < counter; i++) {
					System.out.println((headingList.get(i));
					System.out.println(reviewList.get(i));
					System.out.println();
				}

			}
			System.out.println("Done :D");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
