package be.lechtitseb.google.reader.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import be.lechtitseb.google.reader.api.core.GoogleReader;
import be.lechtitseb.google.reader.api.model.exception.AuthenticationException;
import be.lechtitseb.google.reader.api.model.exception.GoogleReaderException;
import be.lechtitseb.google.reader.api.model.feed.Label;
import be.lechtitseb.google.reader.api.model.item.Item;
import be.lechtitseb.google.reader.api.model.user.UserInformation;

// @author gizatullinm
// @author abalogh
 
public class TestGoogleReader {

	private GoogleReader googleReader;

	@Before
	public void init() throws IOException, FileNotFoundException,
			AuthenticationException {
		Properties auth = new Properties();
		auth.load(new FileInputStream("auth.properties"));
		googleReader = new GoogleReader(auth.getProperty("auth.key"),
				auth.getProperty("auth.secret"));
		googleReader.login();
	}

	@Test
	public void testGetUserInformation() throws GoogleReaderException,
			FileNotFoundException, IOException, AuthenticationException {

		UserInformation ui = googleReader.getUserInformation();
		StringBuilder sb = new StringBuilder();
		sb.append(ui.getEmail()).append(" - ").append(ui.getUserId());
		System.out.println(sb.toString());
	}

	@Test
	public void testGetLabels() throws FileNotFoundException, IOException,
			AuthenticationException, GoogleReaderException {

		List<Label> labels = googleReader.getLabels();
		for (Label l : labels) {
			System.out.println(l.getName());
		}

	}
	
	@Test
	public void testSearch() throws GoogleReaderException
	{
		List<Item> searchResults = googleReader.search("google", 100);
		for (Item i: searchResults)
		{
			System.out.println(i.getTitle() + " - " + i.getPublishedOn());
		}
	}

}
