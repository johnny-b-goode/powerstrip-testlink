package net.scientifichooliganism.testlinkplugin;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Collection;
import java.util.Vector;

import net.scientifichooliganism.javaplug.interfaces.Plugin;
import net.scientifichooliganism.javaplug.interfaces.Store;
import net.scientifichooliganism.javaplug.vo.Configuration;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

public class TestLinkPlugin implements Plugin, Store {
	private static TestLinkPlugin instance;
	/*The ideal thing is probably going to be to have a collection
	of TestLinkAPI objects.*/
	private String server;
	private String apiKey;

	private TestLinkPlugin (){
		server = null;
		apiKey = null;
	}

	public static TestLinkPlugin getInstance () {
		if (instance == null) {
			instance = new TestLinkPlugin();
		}

		return instance;
	}

	public void init () {
		server = "http://<server_base_URL>/lib/api/xmlrpc/v1/xmlrpc.php";
		apiKey = "<TestLink_API_Key>";
		/*What really needs to happen here is that when this method is called
		DataLayer needs to be queried for all sources, and then each source
		needs to be turned into a TestLinkAPI object and added to the collection.*/
	}

	public String[][] getActions() {
		throw new RuntimeException("TestLinkPlugin.getActions() called");
	}

	public static boolean isStore() {
		return true;
	}

	public void addResource (Object resource) throws IllegalArgumentException {
		throw new RuntimeException("TestLinkPlugin.addResource(Object) called");
	}

	public Collection getResources () throws IllegalArgumentException {
		throw new RuntimeException("TestLinkPlugin.getResources() called");
	}

	public void removeResource (Object resource) throws IllegalArgumentException {
		throw new RuntimeException("TestLinkPlugin.removeResource(Object) called");
	}

	public void persist (Object in) throws IllegalArgumentException {
		throw new RuntimeException("TestLinkPlugin.persist(Object) called");
	}

	public Collection query (String query) throws IllegalArgumentException {
		System.out.println("TestLinkPlugin.query(String)");

//START move elsewhere
		URL testlinkURL = null;

		try	{
			testlinkURL = new URL(server);
		}
		catch (MalformedURLException mue ) {
			mue.printStackTrace();
		}

		try	{
			TestLinkAPI api = new TestLinkAPI(testlinkURL, apiKey);
		}
		catch( TestLinkAPIException te) {
			te.printStackTrace();
		}
//END move elsewhere

		return new Vector();
	}

	public static void main (String [] args) {
		try {
			TestLinkPlugin tlp = TestLinkPlugin.getInstance();
			tlp.init();
			tlp.query("Release");
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}