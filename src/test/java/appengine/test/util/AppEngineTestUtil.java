package appengine.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.appengine.tools.development.ApiProxyLocal;
import com.google.appengine.tools.development.LocalServerEnvironment;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMailServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalURLFetchServiceTestConfig;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;

/**
 * AppEngine用のモジュールの自動テストを行うためのユーティリティクラス。
 * @author shin1ogawa
 */
public class AppEngineTestUtil {

	private AppEngineTestUtil() {
	}

	/**
	 * queue.xmlを有効にして、AppEngineの自動テスト用の環境を準備する。
	 * <p>{@code warPath}を指定した場合は、そのフォルダの配下にあるAppEngine用のxml定義ファイルを
	 * {@code folderName}で指定されたテスト環境フォルダへコピーする。</p>
	 * @param environment 
	 * @param testFolderName
	 * @param warFolderName warフォルダのパス
	 * @param noStrage {@code true}ならファイルへの書き込みは行わない。
	 * @return {@link LocalServiceTestHelper}
	 * @throws IOException
	 */
	public static LocalServiceTestHelper setUpAppEngine(final ApiProxy.Environment environment,
			final String testFolderName, String warFolderName, boolean noStrage) throws IOException {
		LocalServiceTestHelper helper =
				new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
					.setNoStorage(false), new LocalMemcacheServiceTestConfig(),
						new LocalMailServiceTestConfig(), new LocalURLFetchServiceTestConfig()) {

					@Override
					protected Environment newEnvironment() {
						return environment;
					}

					@Override
					protected LocalServerEnvironment newLocalServerEnvironment() {
						final LocalServerEnvironment localServerEnvironment =
								super.newLocalServerEnvironment();
						return new LocalServerEnvironment() {

							@Override
							public void waitForServerToStart() throws InterruptedException {
								localServerEnvironment.waitForServerToStart();
							}

							@Override
							public int getPort() {
								return localServerEnvironment.getPort();
							}

							@Override
							public File getAppDir() {
								return new File(testFolderName);
							}

							@Override
							public String getAddress() {
								return localServerEnvironment.getAddress();
							}
						};
					}

				};
		if (StringUtils.isNotEmpty(warFolderName) && warFolderName.equals(testFolderName) == false) {
			copyAppEngineXmlFile(testFolderName, warFolderName, "queue.xml");
			copyAppEngineXmlFile(testFolderName, warFolderName, "cron.xml");
			copyAppEngineXmlFile(testFolderName, warFolderName, "datastore-indexes.xml");
		}
		helper.setUp();
		return helper;
	}

	/**
	 * AppEngineの自動テスト用の環境を終了する。
	 * @param helper 
	 */
	public static void tearDownAppEngine(LocalServiceTestHelper helper) {
		helper.tearDown();
		if (ApiProxy.getDelegate() != null) {
			((ApiProxyLocal) ApiProxy.getDelegate()).stop();
			ApiProxy.setDelegate(null);
		}
		ApiProxy.setEnvironmentForCurrentThread(null);
	}

	static void copyAppEngineXmlFile(String testFolderName, String warFolderName, String fileName)
			throws IOException {
		String srcPath =
				warFolderName + (warFolderName.endsWith(File.separator) ? "" : File.separator)
						+ "WEB-INF" + File.separator + fileName;
		File srcFile = new File(srcPath);
		if (srcFile.exists() == false) {
			return;
		}
		File dstFile =
				new File(testFolderName
						+ (testFolderName.endsWith(File.separator) ? "" : File.separator)
						+ "WEB-INF" + File.separator + fileName);
		FileUtils.copyFile(new File(srcPath), dstFile);
	}

	/**
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return {@link Date}
	 */
	public static Date newDate(int year, int month, int date, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date, hour, minute, second);
		return calendar.getTime();
	}

	/**
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @param milliSecond
	 * @return {@link Date}
	 */
	public static Date newDate(int year, int month, int date, int hour, int minute, int second,
			int milliSecond) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, date, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, milliSecond);
		return calendar.getTime();
	}
}
