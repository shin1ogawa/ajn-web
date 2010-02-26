package com.appspot.ajnweb.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import org.slim3.datastore.Datastore;

import appengine.test.util.AppEngineTestUtil;
import appengine.util.MakeSyncCallServletDelegate;

import com.appspot.ajnweb.meta.EnvInfoMeta;
import com.appspot.ajnweb.model.EnvInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

/**
 * @author shin1ogawa
 */
public class CheckEnvSetup {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		setUpBeforeClass();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("google email:");
			String email = input.readLine();
			System.out.println("google password:");
			String password = input.readLine();
			MakeSyncCallServletDelegate.runInDelegateWithAuth(new Runnable() {

				@Override
				public void run() {
					// プラットフォームのバージョンの値
					EnvInfo info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(), "serverinfo"));
					info.setValue("Google App Engine/1.3.1"); // 現在の値
					info.setValues(new HashSet<String>(Arrays.asList("Google App Engine/1.3.0",
							"Google App Engine/1.3.1"))); // 履歴
					Datastore.put(info);

					info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(),
							"com.google.apphosting.api.ApiProxy.datacenter"));
					info.setValues(new HashSet<String>(Arrays.asList("na1", "na2"))); // 履歴
					Datastore.put(info);

					info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(),
							"com.google.apphosting.api.ApiStats"));
					Datastore.put(info);

					info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(),
							"com.google.appengine.api.users.UserService.user_organization"));
					Datastore.put(info);

					info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(),
							"com.google.appengine.api.users.UserService.user_id_key"));
					Datastore.put(info);

					info = new EnvInfo();
					info.setKey(Datastore.createKey(EnvInfoMeta.get(),
							"com.google.appengine.api.NamespaceManager.default_api_namespace_key"));
					Datastore.put(info);
				}
			}, email, password, "http://ajn-web.appspot.com/", "sys/makesynccall");
		} finally {
			tearDownAfterClass();
			System.exit(0);
		}
	}


	static LocalServiceTestHelper helper;


	static void setUpBeforeClass() throws IOException {
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment(), "target/setup", "war",
						false);
		System.out.println("appid=" + ApiProxy.getCurrentEnvironment().getAppId());
	}

	static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}
}
