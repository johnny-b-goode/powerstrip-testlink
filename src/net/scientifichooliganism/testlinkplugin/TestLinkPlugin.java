package net.scientifichooliganism.testlinkplugin;

import java.net.URL;
import java.net.MalformedURLException;

import java.util.Collection;
import java.util.Vector;

import net.scientifichooliganism.javaplug.interfaces.Plugin;
import net.scientifichooliganism.javaplug.interfaces.Store;
import net.scientifichooliganism.javaplug.vo.Configuration;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
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
		/*Vector configs<Configuration> = DataLayer.getInstance().query("Configuration WHERE Configuration.module = 'TestLinkPlugin'");

		for (Configuration config : configs) {
			if (config.getKey().equals("TESTLINK_URL")) {
				//find matching API key
				//instantiate TestLinkAPI object
				//add TestLinkAPI Object to collection
			}
		}
		*/
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

	public void remove (Object in) throws IllegalArgumentException {
		throw new RuntimeException("TestLinkPlugin.remove(Object) called");
	}

	public Collection query (String query) throws IllegalArgumentException {
		System.out.println("TestLinkPlugin.query(String)");

//START move elsewhere
		URL testlinkURL = null;
		TestLinkAPI testlinkAPI = null;

		try	{
			testlinkURL = new URL(server);
		}
		catch (MalformedURLException mue ) {
			mue.printStackTrace();
		}

		try	{
			testlinkAPI = new TestLinkAPI(testlinkURL, apiKey);
		}
		catch( TestLinkAPIException te) {
			te.printStackTrace();
		}
//END move elsewhere

//		System.out.println("===============================================================================");
//		System.out.println("About TestLink");
//		System.out.println("===============================================================================");
//		System.out.println(testlinkAPI.about());
//		System.out.println("===============================================================================");

//need code for persisting a unique ID mapping

/*I think what I will do here is look to see what objects are being requested,
then begin traversing the TestLink object hierarchy, transforming and then
filtering the list of objects to be returned.

MetaData is going to be a pain...*/

		try {
			TestProject projects[] = testlinkAPI.getProjects();
			Vector ret = new Vector();

			for (TestProject project : projects) {
				System.out.println("Project: " + project.getName());
				TestPlan plans[] = testlinkAPI.getProjectTestPlans(project.getId());

				if (false) {
					Application app = Transformer.applicationFromProject(project);

					if (ret.contains(app) == false) {
						ret.add(app);
					}
				}

				for (TestPlan plan : plans) {
					System.out.println("	Test Plan: " + plan.getName());
					Build builds[] = testlinkAPI.getBuildsForTestPlan(plan.getId());
					TestSuite suites[] = testlinkAPI.getTestSuitesForTestPlan(plan.getId());

					for (Build build : builds) {
						System.out.println("		Build: " + build.getName());

						if (false) {
							Release rel = Transformer.releaseFromBuild(build);

							if (ret.contains(rel) == false) {
								ret.add(rel);
							}
						}
					}

					/*I am not sure that there are any guarantees within TestLink that Test
					Cases are associated with a Test Suite. It seems to me that Test Cases
					are directly associated with Test Plans, which makes me think the relationship
					between Test Plans and Test Suites is handled automatically in the background.

					I suspect though, that test cases can be created outside of Test Suites, so I
					think I will probably need to get everything associated with a Test Suite, then
					add any straglers using a call to TestLinkAPI.getTestCasesForTestPlan(), which
					looks like it will require some experimentation (I would guess that not all of
					the arguments in the method signature are required, allowing the query to be
					performed based on different criteria).*/
					for (TestSuite suite : suites) {
						System.out.println("		Test Suite: " + suite.getName());
						//I'm totally guessing on the last two parameters here:
//						TestCase cases[] = testlinkAPI.getTestCasesForTestSuite(suite.getId(), true, TestCaseDetails.SUMMARY);

//						for (TestCase testCase : cases) {
//							System.out.println("			Test Case: " + testCase.getName());
							/*According to the TestLink API it is possible to get the steps, represented as TestCaseStep objects
							comprising the TestCase using TestCase.getSteps(). I was thinking of possibly going to that level of
							detail to generate Task objects, but that seems excessive. I think it makes more sense, in the context
							of TestLink, for Tasks to represent what is performed as part of a TestPlan.
							*/
//						}

						if (false) {
							Task task = Transformer.taskFromTestSuite(suite);

							if (ret.contains(task) == false) {
								ret.add(task);
							}
						}
					}
				}
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}

		return ret;
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