package IMDBdataCollector;

import Crawl.*;

public class CollectData {
	String top100movie[] = { "Rashomon (1950)", "Snatch (2000)",
			"L.A. Confidential (1997)", "The Apartment (1960)",
			"The Treasure of the Sierra Madre (1948)", "The Third Man (1949)",
			"For a Few Dollars More (1965)", "Inglourious Basterds (2009)" };

	String bottom100movie[] = { "Disaster Movie",
			" Keloglan vs. the Black Prince (2008)", "Final Just (1985)",
			"Yes Sir (2007)", "Birdemic: Shock and Terror (2010)",
			"Space Mutiny (1988)", "Manos: The Hands of Fate (1966)",
			"Turks in Space (2006)", "Superbabies: baby Geniuses 2 (2004)",
			"Invasion of the Neptune Men (1961)", "Who's Your Caddy (2007)",
			"House of the Dead (2003)", "Crossover (2006)",
			"Pledge This! (2006)", "Daniel del Zaunberer (2004)",
			"Die Hard Dracula (1998)", "The Pumaman (1980)",
			"The Touch of Satan (1971)", "From justin to Kelly (2003)",
			"Anne B. Real (2003)", "Himmatwala (2013)",
			"Prince of Space (1959)", "Glitter (2001)",
			"The Pod People (1983)", "The Creeping Terror (1964)",
			"Zombie Nightmare (1987)", "The Wild World of Batwoman (1966)",
			"Son of the Mask (2005)", "Track of the Moon Beast (1976)",
			"The Blade Master (1984)", "Girl in Gold Biits (1968)",
			"Eegah (1962)", "Ram Gopal Varma's Indian Flames (2007)",
			"The Maize: The Movie (2004)", "Surf School (2006)",
			"Boggy Creek II: And the Legend Continues (1985)",
			"Ghosts Can't Do It (1989)", "Soultaker (1990)" };

	public void collectRevies() {
		for (int i = 0; i < bottom100movie.length; i++)
			downloadData(bottom100movie[i]);
	}

	public void downloadData(String movie) {
		Crawler crawler = new Crawler();
		System.out.println("Movie: " + movie);
		crawler.retreiveReviewPage(crawler.generateURL(movie), movie);
	}
}
