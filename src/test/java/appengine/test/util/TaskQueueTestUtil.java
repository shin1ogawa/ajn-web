package appengine.test.util;

import java.util.concurrent.Future;

import appengine.test.util.CustomizedDelegate.AbstractCustomizedDelegate;

import com.google.appengine.api.labs.taskqueue.TaskQueuePb;
import com.google.appengine.api.labs.taskqueue.TaskQueuePb.TaskQueueAddRequest;
import com.google.appengine.api.labs.taskqueue.TaskQueuePb.TaskQueueAddResponse;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiConfig;
import com.google.apphosting.api.ApiProxy.ApiProxyException;
import com.google.apphosting.api.ApiProxy.Environment;

/**
 * TaskQueueサービスに関する自動テストを行うためのユーティリティ。
 * @author shin1ogawa
 */
public class TaskQueueTestUtil {

	private TaskQueueTestUtil() {
	}


	static final String SERVICE_NAME = "taskqueue";


	/**
	 * TaskQueueサービスへのアクセスをフックして
	 * {@link HookTaskQueueDelegateHandler}経由でハンドルするための{@link ApiProxy.Delegate}.
	 * @author shin1ogawa
	 */
	public static class HookTaskQueueDelegate extends AbstractCustomizedDelegate {

		final HookTaskQueueDelegateHandler handler;


		/**
		 * @param runnable
		 * @param handler
		 */
		public static void runInDelegate(Runnable runnable, HookTaskQueueDelegateHandler handler) {
			HookTaskQueueDelegate delegate = new HookTaskQueueDelegate(handler);
			ApiProxy.setDelegate(delegate);
			runnable.run();
			delegate.delegateToOriginal();
		}

		/**
		 * the constructor.
		 * @param handler
		 * @category constructor
		 */
		public HookTaskQueueDelegate(HookTaskQueueDelegateHandler handler) {
			this.handler = handler;
		}

		public byte[] makeSyncCall(Environment env, String service, String method, byte[] request)
				throws ApiProxyException {
			if (service.equals(SERVICE_NAME) == false) {
				return getOriginal().makeSyncCall(env, service, method, request);
			}
			TaskQueuePb.TaskQueueAddRequest requestPb = new TaskQueuePb.TaskQueueAddRequest();
			requestPb.mergeFrom(request);
			handler.addRequest(requestPb);
			byte[] response = getOriginal().makeSyncCall(env, service, method, request);
			TaskQueuePb.TaskQueueAddResponse responsePb = new TaskQueuePb.TaskQueueAddResponse();
			handler.addResponse(responsePb);
			return response;
		}

		@Override
		public Future<byte[]> makeAsyncCall(Environment env, String service, String method,
				byte[] request, ApiConfig config) {
			return getOriginal().makeAsyncCall(env, service, method, request, config);
		}
	}

	/**
	 * taskqueueサービスへの送受信をハンドルする.
	 * @author shin1ogawa
	 */
	public static interface HookTaskQueueDelegateHandler {

		/**
		 * taskqueue#addへの送信をハンドルする。
		 * @param requestPb
		 */
		void addRequest(TaskQueuePb.TaskQueueAddRequest requestPb);

		/**
		 * taskqueue#addからの受信をハンドルする。
		 * @param responsePb
		 */
		void addResponse(TaskQueuePb.TaskQueueAddResponse responsePb);
	}

	/**
	 * {@link HookTaskQueueDelegateHandler}の空の実装。
	 * @author shin1ogawa
	 */
	public static class HookTaskQueueDelegateHandlerAdapter implements HookTaskQueueDelegateHandler {

		public void addRequest(TaskQueueAddRequest requestPb) {
		}

		public void addResponse(TaskQueueAddResponse responsePb) {
		}
	}
}
