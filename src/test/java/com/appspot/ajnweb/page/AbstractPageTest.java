package com.appspot.ajnweb.page;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import appengine.test.util.AppEngineTestUtil;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

/**
 * AppEngineのローカル環境を起動する必要があるPageのテストのための抽象クラス。
 * @author shin1ogawa
 */
public class AbstractPageTest {

	/**
	 * AppEngineのローカル環境を開始する。
	 * @throws IOException
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		File testDir = new File("target/pageTest");
		if (testDir.exists() == false) {
			testDir.mkdirs();
		}
		FileUtils.copyDirectory(new File("src/test/resources/pagetest"), testDir);
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment("ajn-web", "pageTest"),
						"target/pageTest", "war", false);
	}


	static LocalServiceTestHelper helper;


	/**
	 * AppEngineのローカル環境を停止する。
	 */
	@AfterClass
	public static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}


	static class LocalEnvironment implements ApiProxy.Environment {

		final String appId;

		final String versionId;


		/**
		 * the constructor.
		 * @param appId
		 * @param versionId
		 * @category constructor
		 */
		LocalEnvironment(String appId, String versionId) {
			this.appId = appId;
			this.versionId = versionId;
		}

		/**
		 * sdkで起動した時に{@code ApiProxy.getCurrentEnvironment().getAppId()}で取得される値
		 */
		public String getAppId() {
			return appId;
		}

		/**
		 * sdkで起動した時に{@code ApiProxy.getCurrentEnvironment().getVersionId()}
		 * で取得される値
		 */
		public String getVersionId() {
			return versionId;
		}

		public String getRequestNamespace() {
			return "";
		}

		public String getAuthDomain() {
			return "gmail.com";
		}

		public boolean isLoggedIn() {
			return true;
		}

		public String getEmail() {
			return "pagetest@gmail.com";
		}

		public boolean isAdmin() {
			return true;
		}

		public Map<String, Object> getAttributes() {
			Map<String, Object> map = new HashMap<String, Object>();
			return map;
		}
	}
}
