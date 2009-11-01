package appengine.test.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocal;
import com.google.appengine.tools.development.ApiProxyLocalImpl;
import com.google.apphosting.api.ApiProxy;

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
	 * @throws IOException
	 */
	public static void setUpAppEngine(ApiProxy.Environment environment, String testFolderName,
			String warFolderName, boolean noStrage) throws IOException {
		if (StringUtils.isNotEmpty(warFolderName) && warFolderName.equals(testFolderName) == false) {
			copyAppEngineXmlFile(testFolderName, warFolderName, "queue.xml");
			copyAppEngineXmlFile(testFolderName, warFolderName, "cron.xml");
			copyAppEngineXmlFile(testFolderName, warFolderName, "datastore-indexes.xml");
		}
		ApiProxy.setEnvironmentForCurrentThread(environment);
		ApiProxy.setDelegate(new ApiProxyLocalImpl(new File(testFolderName)) {
		});
		if (noStrage) {
			((ApiProxyLocalImpl) ApiProxy.getDelegate()).setProperty(
					LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());
		}
	}

	/**
	 * AppEngineの自動テスト用の環境を終了する。
	 */
	public static void tearDownAppEngine() {
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
