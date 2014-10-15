package de.uni_freiburg.informatik.ultimateregressiontest.translation;

import java.io.File;
import java.util.Collection;

import org.junit.AfterClass;

import de.uni_freiburg.informatik.ultimateregressiontest.AbstractRegressionTestSuite;
import de.uni_freiburg.informatik.ultimatetest.UltimateRunDefinition;
import de.uni_freiburg.informatik.ultimatetest.decider.ITestResultDecider;
import de.uni_freiburg.informatik.ultimatetest.decider.TranslationTestResultDecider;
import de.uni_freiburg.informatik.ultimatetest.util.Util;

public class C2BoogieRegressionTestSuite extends AbstractRegressionTestSuite {

	private static String sRootFolder = Util.getPathFromTrunk("examples/CToBoogieTranslation");

	public C2BoogieRegressionTestSuite() {
		super();
		mTimeout = 5000;
		mRootFolder = sRootFolder;
		mFiletypesToConsider = new String[] { ".c" };
	}


	@Override
	protected ITestResultDecider getTestResultDecider(UltimateRunDefinition runDefinition) {
		return new TranslationTestResultDecider(runDefinition.getInput().getAbsolutePath());
	}

	@AfterClass
	public static void cleanupBoogiePrinterFiles() {

		File root = getRootFolder(sRootFolder);

		Collection<File> files = Util.getFiles(root, new String[] { ".bpl" });
		files = Util.filterFiles(files, ".*regression.*BoogiePrinter_.*UID.*");

		System.out.println("---");
		System.out.println(String.format("Begin cleanup of %s", sRootFolder));

		for (File f : files) {
			try {
				if (f.delete()) {
					System.out.println(String.format("Sucessfully deleted %s", f.getAbsolutePath()));
				} else {
					System.out.println(String.format("Deleteing %s failed", f.getAbsolutePath()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("---");
	}
}
