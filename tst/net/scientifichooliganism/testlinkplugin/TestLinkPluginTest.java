package net.scientifichooliganism.testlinkplugin;

public class TestLinkPluginTest {

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