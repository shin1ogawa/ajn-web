package com.appspot.ajnweb.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import appengine.test.util.AppEngineTestUtil;
import appengine.util.MakeSyncCallServletDelegate;

import com.appspot.ajnweb.model.ApplicationSetting;
import com.appspot.ajnweb.service.ApplicationSettingService;
import com.appspot.ajnweb.service.ApplicationSettingService.SettingKey;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * デプロイ環境へ接続して、Applicationの初期状態を作成する。
 * <p>{@link ApplicationSetting}に初期データを投入するとか。</p>
 * @author shin1ogawa
 */
public class SetUpApplication {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		setUpBeforeClass();
		try {
			SettingKey[] keys = ApplicationSettingService.SettingKey.values();
			final Map<SettingKey, String> values = new HashMap<SettingKey, String>(keys.length);
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			do {
				input(keys, values, input);
			} while (confirm(values, input) == false);
			System.out.println("google email:");
			String email = input.readLine();
			System.out.println("google password:");
			String password = input.readLine();
			MakeSyncCallServletDelegate.runInDelegateWithAuth(new Runnable() {

				@Override
				public void run() {
					Iterator<Entry<SettingKey, String>> i = values.entrySet().iterator();
					while (i.hasNext()) {
						Entry<SettingKey, String> next = i.next();
						ApplicationSettingService.put(next.getKey(), next.getValue());
					}
				}
			}, email, password, "http://ajn-web.appspot.com/", "sys/makesynccall");
		} finally {
			tearDownAfterClass();
		}
	}

	static boolean confirm(Map<SettingKey, String> values, BufferedReader input) throws IOException {
		System.out.println("以下の設定値をデプロイ環境に投入します。続行するなら[Y]を入力してくだしあ。[Y/N]");
		Iterator<Entry<SettingKey, String>> i = values.entrySet().iterator();
		while (i.hasNext()) {
			Entry<SettingKey, String> next = i.next();
			System.out.println(next.getKey().name() + "=" + next.getValue());
		}
		return "y".equalsIgnoreCase(input.readLine());
	}

	static void input(SettingKey[] keys, Map<SettingKey, String> values, BufferedReader input)
			throws IOException {
		for (SettingKey settingKey : keys) {
			System.out.println(settingKey + "の設定値を入力してください。");
			String value = input.readLine();
			values.put(settingKey, value);
		}
	}


	static LocalServiceTestHelper helper;


	static void setUpBeforeClass() throws IOException {
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment(), "target/setup", "war",
						false);
	}

	static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}
}
