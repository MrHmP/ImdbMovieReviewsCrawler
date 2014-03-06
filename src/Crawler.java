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

public class Crawler {

	public static void main(String args[]) {
		// new posTagger().justDoItAlready();
		Crawler crawl = new Crawler();
		System.out.print("Movie name: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String name = null;
		try {
			name = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String url = crawl.generateURL(name);
		crawl.retreiveReviewPage(url);
	}

	public String generateURL(String name) {
		try {
			System.out.println("Generating URL");
			name = name.replaceAll(" ", "+");
			String url = "http://deanclatworthy.com/imdb/?q=" + name;
			System.out.println(url);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);

			HttpResponse response = client.execute(request);

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			int c = 0;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONParser jsonParser = new JSONParser();

			Object obj = jsonParser.parse(result.toString());

			JSONObject imdbMovieInfoJsonObject = (JSONObject) obj;

			String imdbUrl = (String) imdbMovieInfoJsonObject.get("imdburl");
			imdbUrl = imdbUrl.concat("reviews");

			System.out.println("Generated URL: " + imdbUrl);
			return imdbUrl;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void retreiveReviewPage(String url) {
		System.out.println("Getting total number of review pages");

		Document webpageDocument = null;
		try {
			webpageDocument = Jsoup
					.connect(url)
					.timeout(10 * 1000)
					.userAgent(
							"Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1667.0 Safari/537.36")
					.get();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// to get total pages...each page has 10 reviews
		org.jsoup.nodes.Element table = webpageDocument.getElementsByTag(
				"table").get(1);
		Iterator<org.jsoup.nodes.Element> itr = table.select("td").iterator();
		int totalPages = Integer.parseInt(itr.next().text().split(" ")[3]
				.replace(":", ""));
		System.out.println(totalPages);
		List<String> finalList = new ArrayList<>();
		FileOutputStream fout;
		PrintWriter pw = null;
		try {
			fout = new FileOutputStream("D:\\rev.txt");
			pw = new PrintWriter(fout);

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		for (int start = 0; start < totalPages; start++) {
			// extracting reviews
			try {
				System.out.println("Getting page: " + (start + 1));
				int counter = 0;
				List<String> reviewList = new ArrayList<>();
				List<String> headingList = new ArrayList<>();

				String modifiedUrl = url + "?start=" + (start * 10);
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
					pw.println(headingList.get(i));
					pw.println(reviewList.get(i));
					pw.println();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done :D");

	}
}
