package net.scientifichooliganism.testlinkplugin;

import net.scientifichooliganism.javaplug.query.Query;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLinkPluginTest {
	private TestLinkPlugin tlp;

	@BeforeClass
	public void init() {
		tlp = TestLinkPlugin.getInstance();
	}

	@Test
	public void queryTest(){
		try {
			tlp.init();
			tlp.query(new Query("Application"));
			tlp.query(new Query("Release"));
			tlp.query(new Query("Task"));
//			tlp.dumpIDMap();
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}