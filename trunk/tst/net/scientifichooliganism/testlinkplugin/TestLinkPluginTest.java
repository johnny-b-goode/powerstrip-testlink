package net.scientifichooliganism.testlinkplugin;

import net.scientifichooliganism.javaplug.query.Query;

public class TestLinkPluginTest {

	public static void main (String [] args) {
		try {
			TestLinkPlugin tlp = TestLinkPlugin.getInstance();
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