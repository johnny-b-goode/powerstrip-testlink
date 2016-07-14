package net.scientifichooliganism.testlinkplugin;

import net.scientifichooliganism.javaplug.interfaces.Plugin;
import net.scientifichooliganism.javaplug.interfaces.Store;

public class TestLinkPlugin {
	private static TestLinkPlugin instance;

	private TestLinkPlugin(){

	}

	public static TestLinkPlugin getInstance() {
		if (instance == null) {
			instance = new TestLinkPlugin();
		}

		return instance;
	}
}