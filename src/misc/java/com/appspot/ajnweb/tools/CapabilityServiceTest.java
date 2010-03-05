package com.appspot.ajnweb.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import appengine.test.util.AppEngineTestUtil;
import appengine.util.MakeSyncCallServletDelegate;

import com.appspot.ajnweb.tools.Capability.IsEnabledRequest;
import com.appspot.ajnweb.tools.Capability.IsEnabledResponse;
import com.appspot.ajnweb.tools.Capability.IsEnabledRequest.Builder;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @author shin1ogawa
 */
public class CapabilityServiceTest {

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
					checkCall("capability_service", "IsEnabled");
					checkCapability("datastore_v3", "write");
					checkCall("datastore_v3", "Get");
					checkCall("datastore_v3", "Put");
					checkCall("datastore_v3", "Delete");
					checkCall("datastore_v3", "AllocateIds");
				}

				IsEnabledResponse checkCapability(String serviceName, String capability) {
					Builder builder = Capability.IsEnabledRequest.newBuilder();
					builder.setPacakge(serviceName);
					if (StringUtils.isNotEmpty(capability)) {
						builder.addCapability(capability);
					}
					IsEnabledRequest requestPb = builder.build();
					byte[] responseBytes =
							ApiProxy.makeSyncCall("capability_service", "IsEnabled", requestPb
								.toByteArray());
					try {
						IsEnabledResponse responsePb = IsEnabledResponse.parseFrom(responseBytes);
						System.out.println(serviceName + "#" + capability + "().summaryStatus="
								+ responsePb.getSummaryStatus() + "["
								+ responsePb.getTimeUntilScheduled() + "]");
						return responsePb;
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
						return null;
					}
				}

				IsEnabledResponse checkCall(String serviceName, String methodName) {
					Builder builder = Capability.IsEnabledRequest.newBuilder();
					builder.setPacakge(serviceName);
					if (StringUtils.isNotEmpty(methodName)) {
						builder.addCall(methodName);
					}
					IsEnabledRequest requestPb = builder.build();
					byte[] responseBytes =
							ApiProxy.makeSyncCall("capability_service", "IsEnabled", requestPb
								.toByteArray());
					try {
						IsEnabledResponse responsePb = IsEnabledResponse.parseFrom(responseBytes);
						System.out.println(serviceName + "#" + methodName + "().summaryStatus="
								+ responsePb.getSummaryStatus() + "["
								+ responsePb.getTimeUntilScheduled() + "]");
						return responsePb;
					} catch (InvalidProtocolBufferException e) {
						e.printStackTrace();
						return null;
					}
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
