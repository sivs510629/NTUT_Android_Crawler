package edu.ntut.selab.testHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class TextXMLCopyHelper {
	protected static File backupConfigFile = new File("configuration/backup/text.xml");
	protected static Path originalConfigPath = new File("configuration/text.xml").toPath(),
			backupConfigPath = backupConfigFile.toPath(),
			testConfigPath = new File("testData/configuration/text.xml").toPath();
	protected static CopyOption copyOption = StandardCopyOption.REPLACE_EXISTING;
	
	public static void backupAndChangeToTestConfig() {
		try {
			new File(System.getProperty("user.dir") + "/configuration/backup").mkdir();
			Files.copy(originalConfigPath, backupConfigPath, copyOption);
			Files.copy(testConfigPath, originalConfigPath, copyOption);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void restoreConfigAndDeleteBackup() {
		try {
			Files.copy(backupConfigPath, originalConfigPath, copyOption);
			backupConfigFile.delete();
			new File("configuration/backup/").delete();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
