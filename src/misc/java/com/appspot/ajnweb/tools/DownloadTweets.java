package com.appspot.ajnweb.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.IOUtils;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.ModelMeta;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import appengine.servlet.MakeSyncCallServlet;
import appengine.test.util.AppEngineTestUtil;
import appengine.util.DatastoreXmlUtil;
import appengine.util.MakeSyncCallServletDelegate;

import com.appspot.ajnweb.meta.TweetMeta;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.common.util.Base64DecoderException;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

import static org.hamcrest.Matchers.*;

import static org.junit.Assert.*;

/**
 * {@link MakeSyncCallServlet}と{@link MakeSyncCallServletDelegate}を使って
 * デプロイ環境のデータをローカル環境のデータストアにダウンロードする。
 * <p>テストデータを作ったり。データの移行等のバッチ処理をしたり。</p>
 * @author shin1ogawa
 */
public class DownloadTweets {

	static final Logger logger = Logger.getLogger(DownloadTweets.class.getName());

	static final String SERVER = "http://ajn-web.appspot.com/";

	static final String SERVLET = "sys/makesynccall";

	static final String exportFolderName = "target/download/";


	/**
	 * デプロイ環境からデータをダウンロードし、ローカル環境のデータストアに保存する。
	 * @param args 
	 * @throws IOException
	 * @throws Base64DecoderException 
	 * @throws ClassNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws IOException, SAXException,
			ParserConfigurationException, ClassNotFoundException, Base64DecoderException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("google email:");
		String email = input.readLine();
		System.out.println("google password:");
		String password = input.readLine();
		ModelMeta<?>[] metaList = {
			new TweetMeta()
		};
		setUpBeforeClass();
		try {
			for (ModelMeta<?> modelMeta : metaList) {
				getRemote(modelMeta, email, password);
				// 危ないので、念のためmakeSyncCallが外れている事を確認する。
				assertThat(ApiProxy.getDelegate(),
						is(not(instanceOf(MakeSyncCallServletDelegate.class))));
				// ここからはローカルにリクエストされる
				importKindFromXml(new File(exportFolderName + modelMeta.getKind()));
			}
		} finally {
			tearDownAfterClass();
		}
	}

	static void getRemote(final ModelMeta<?> meta, String email, String password)
			throws URIException, HttpException, IOException, FileNotFoundException {
		// すべてデプロイ環境側へリクエストされる
		MakeSyncCallServletDelegate.runInDelegateWithAuth(new Runnable() {

			@Override
			public void run() {
				try {
					downloadKindAndExportToXml(meta, 100, new File(exportFolderName
							+ meta.getKind()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, email, password, SERVER, SERVLET);
	}

	static int importKindFromXml(File folder) throws FileNotFoundException, SAXException,
			IOException, ParserConfigurationException, ClassNotFoundException,
			Base64DecoderException {
		File[] listFiles = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return (pathname.isFile() && pathname.getAbsolutePath().endsWith(".xml"));
			}
		});
		if (listFiles.length == 0) {
			return 0;
		}
		int size = 0;
		for (File file : listFiles) {
			try {
				List<Entity> entities = DatastoreXmlUtil.readFromXml(new FileInputStream(file));
				List<Key> put = Datastore.put(entities);
				size += put.size();
			} catch (SAXParseException e) {
				logger.severe(" failure to readFromXml, skipped:" + file);
			}
		}
		return size;
	}

	static int downloadKindAndExportToXml(ModelMeta<?> meta, int buffer, File folder)
			throws FileNotFoundException, IOException {
		logger.info("keyの一覧を取得");
		List<Key> keyList = Datastore.query(meta).asKeyList();
		int size = keyList.size();
		logger.info(meta.getKind() + "の件数=" + size);
		for (int i = 0; i < size; i += buffer) {
			int toIndex = i + buffer;
			if (toIndex > size) {
				toIndex = size - 1;
			}
			logger.info((toIndex - i) + "件クエリ開始");
			List<Entity> list = Datastore.get(keyList.subList(i, toIndex));
			logger.info((toIndex - i) + "件クエリ終了");

			if (folder.exists() == false) {
				folder.mkdirs();
			}
			String folderName = folder.getAbsolutePath();
			if (folderName.endsWith(File.separator) == false) {
				folderName = folderName + File.separator;
			}
			for (Entity entity : list) {
				String fileName = folderName + KeyFactory.keyToString(entity.getKey()) + ".xml";
				logger.info("  export:" + fileName);
				PrintWriter writer = new PrintWriter(new FileOutputStream(fileName));
				writer.println("<entities>");
				DatastoreXmlUtil.writeToXml(writer, entity);
				writer.println("</entities>");
				IOUtils.closeQuietly(writer);
			}
		}
		return size;
	}


	static LocalServiceTestHelper helper;


	static void setUpBeforeClass() throws IOException {
		helper =
				AppEngineTestUtil.setUpAppEngine(new LocalEnvironment(), "target/download", "war",
						false);
	}

	static void tearDownAfterClass() {
		AppEngineTestUtil.tearDownAppEngine(helper);
	}
}
