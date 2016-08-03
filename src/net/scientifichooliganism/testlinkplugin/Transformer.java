package net.scientifichooliganism.testlinkplugin;

import java.util.Collection;
import java.util.Vector;

import net.scientifichooliganism.javaplug.vo.Application;
import net.scientifichooliganism.javaplug.vo.Configuration;
import net.scientifichooliganism.javaplug.vo.MetaData;
import net.scientifichooliganism.javaplug.vo.Release;
import net.scientifichooliganism.javaplug.vo.Task;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.TestCaseDetails;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestProject;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.model.TestSuite;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;


public class Transformer {

	private Transformer () {
		//
	}

	public static Application applicationFromProject (TestProject tp) {
		if (bld == null) {
			throw new IllegalArgumentException("releaseFromBuild(Build): Build is null");
		}

		Application ret = new Application();

		//

		return ret;
	}

	public static Release releaseFromBuild (Build bld) throws IllegalArgumentException {
		if (bld == null) {
			throw new IllegalArgumentException("releaseFromBuild(Build): Build is null");
		}

		Release ret = new Release();

		//

		return ret;
	}

	public static Task taskFromTestSuite(TestSuite ts) throws IllegalArgumentException {
		if (ts == null) {
			throw new IllegalArgumentException("taskFromTestSuite(TestSuite): TestSuite is null");
		}

		Task ret = new Task();

		//

		return ret;
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}