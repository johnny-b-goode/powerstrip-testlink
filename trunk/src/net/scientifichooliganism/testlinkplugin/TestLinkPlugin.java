package net.scientifichooliganism.testlinkplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import net.scientifichooliganism.javaplug.DataLayer;
import net.scientifichooliganism.javaplug.interfaces.Application;
import net.scientifichooliganism.javaplug.interfaces.Configuration;
import net.scientifichooliganism.javaplug.interfaces.Plugin;
import net.scientifichooliganism.javaplug.interfaces.Release;
import net.scientifichooliganism.javaplug.interfaces.Store;
import net.scientifichooliganism.javaplug.interfaces.Task;
import net.scientifichooliganism.javaplug.interfaces.ValueObject;
import net.scientifichooliganism.javaplug.query.Query;
import net.scientifichooliganism.javaplug.vo.BaseApplication;
import net.scientifichooliganism.javaplug.vo.BaseConfiguration;
import net.scientifichooliganism.javaplug.vo.BaseRelease;
import net.scientifichooliganism.javaplug.vo.BaseTask;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

public class TestLinkPlugin implements Plugin, Store {
	private static final String idMapFile = "idmap.ser";
	private static TestLinkPlugin instance;
	/*The ideal thing is probably going to be to have a collection
	of TestLinkAPI objects.*/
	private String server;
	private String apiKey;
	private Collection<Configuration> configs;
	private ConcurrentHashMap<String, String> idMap;

	private TestLinkPlugin () {
		server = null;
		apiKey = null;
		configs = null;
		idMap = null;
	}

	public static TestLinkPlugin getInstance () {
		if (instance == null) {
			instance = new TestLinkPlugin();
			instance.init();
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

		if (idMap == null) {
			File f = new File(TestLinkPlugin.idMapFile);

			if (f.exists()) {
				try {
					ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
					idMap = (ConcurrentHashMap<String, String>)ois.readObject();
					ois.close();
				}
				catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}

		if (idMap == null) {
			idMap = new ConcurrentHashMap<String, String>();
		}
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

	public Collection<ValueObject> query (Query query) throws IllegalArgumentException {
//		System.out.println("TestLinkPlugin.query(String)");
		Vector<ValueObject> ret = new Vector<ValueObject>();

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

			for (TestProject project : projects) {
//				System.out.println("Project: " + project.getName());
				TestPlan plans[] = testlinkAPI.getProjectTestPlans(project.getId());

				if (selectIncludesObject(query, "Application")) {
					Application app = applicationFromProject(project);

					if (ret.contains(app) == false) {
						ret.add(app);
					}
				}

				for (TestPlan plan : plans) {
//					System.out.println("	Test Plan: " + plan.getName());
					Build builds[] = testlinkAPI.getBuildsForTestPlan(plan.getId());
					TestSuite suites[] = testlinkAPI.getTestSuitesForTestPlan(plan.getId());

					for (Build build : builds) {
//						System.out.println("		Build: " + build.getName());

						if (selectIncludesObject(query, "Release")) {
							Release rel = releaseFromBuild(build);

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
//						System.out.println("		Test Suite: " + suite.getName());
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

						if (selectIncludesObject(query, "Task")) {
							Task task = taskFromTestSuite(suite);

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

	private String getNextID () {
		BigInteger biggest = BigInteger.ZERO;

		for (String str : idMap.values()) {
			BigInteger thisInt = new BigInteger(str);

			if (thisInt.compareTo(biggest) == 1) {
				biggest = thisInt;
			}
		}

		return biggest.add(BigInteger.ONE).toString();
	}

	private String getMappedID (String className, int objID) {
		String key = className + "," + String.valueOf(objID);
		String value = null;

		if (idMap.containsKey(key)) {
			value = idMap.get(key);
		}

		if (value == null) {
//			value = dl.getUniqueID();
			value = getNextID();
//			System.out.println("getNextID() returned " + value);
		}

		if (value == null ) {
//			throw new RuntimeException ("query(Query) unable to retrieve unique id from DataLayer");
			System.out.println("ERROR: unable to provide unique ID. Removing object from results...");
		}

		setMappedID(key, value);
		return value;
	}

	private void setMappedID (String key, String value) {
		idMap.put(key, value);
		File f = new File(idMapFile);

		if (f.exists()) {
			try {
				f.delete();
			}
			catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(idMapFile)));
			oos.writeObject(idMap);
			oos.close();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	private boolean selectIncludesObject (Query qIn, String type) {
		boolean ret = false;

		for (String object: qIn.getSelectValues()) {
//			System.out.println("	object: " + object);

			if (type.equals(object)) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	private Application applicationFromProject (TestProject tp) {
		if (tp == null) {
			throw new IllegalArgumentException("releaseFromBuild(Build): Build is null");
		}

		BaseApplication ret = new BaseApplication();
		ret.setID(getMappedID(ret.getClass().getName(), tp.getId()));
		ret.setName(tp.getName());
		ret.setDescription(tp.getNotes());
		//ret.setActive(tp.isActive());
		return ret;
	}

	private Release releaseFromBuild (Build bld) throws IllegalArgumentException {
		if (bld == null) {
			throw new IllegalArgumentException("releaseFromBuild(Build): Build is null");
		}

		BaseRelease ret = new BaseRelease();
		ret.setID(getMappedID(ret.getClass().getName(), bld.getId()));
		//ret.setApplication();
		ret.setName(bld.getName());
		ret.setDescription(bld.getNotes());
		//ret.setDueDate();
		//ret.setReleaseDate();
		return ret;
	}

	private Task taskFromTestSuite(TestSuite ts) throws IllegalArgumentException {
		if (ts == null) {
			throw new IllegalArgumentException("taskFromTestSuite(TestSuite): TestSuite is null");
		}

		BaseTask ret = new BaseTask();
		ret.setID(getMappedID(ret.getClass().getName(), ts.getId()));
		ret.setName(ts.getName());
		ret.setDescription(ts.getDetails());
		//ret.setScheduledDuration();
		//ret.setStartDate();
		//ret.setCompletedDate();
		return ret;
	}

	private void dumpIDMap () {
		System.out.println("Contents of idMap:");

		for (String key : idMap.keySet()) {
			System.out.println("	key: " + key + ", value: " + idMap.get(key));
		}
	}
}