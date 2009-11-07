package com.appspot.ajnweb.webhandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import appengine.servlet.MakeSyncCallServlet;

/**
 * 特定の操作（主に読み込み系）だけを実行できる{@link MakeSyncCallServlet}の拡張。
 * @author shin1ogawa
 */
@SuppressWarnings("serial")
public class ReadOnlyMakeSyncCallServlet extends MakeSyncCallServlet {

	private static final String METHOD_NAME = "methodName";

	private static final String SERVICE_NAME = "serviceName";

	private static Logger logger = Logger.getLogger(ReadOnlyMakeSyncCallServlet.class.getName());


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String serviceName = req.getHeader(SERVICE_NAME);
		String methodName = req.getHeader(METHOD_NAME);
		if (serviceName.equalsIgnoreCase("datastore_v3")) {
			if (methodName.equalsIgnoreCase("Get") || methodName.equalsIgnoreCase("RunQuery")) {
				super.doPost(req, resp);
				return;
			}
		}
		if (serviceName.equalsIgnoreCase("memcache") || methodName.equalsIgnoreCase("Get")) {
			super.doPost(req, resp);
			return;
		}
		logger.log(Level.WARNING, "許可されていない操作を実行しようとしました。service=Name" + serviceName
				+ ", methodName=" + methodName);
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

}
