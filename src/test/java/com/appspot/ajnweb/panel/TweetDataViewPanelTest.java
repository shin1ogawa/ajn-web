package com.appspot.ajnweb.panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.DummyPanelPage;
import org.apache.wicket.util.tester.TestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.model.Tweet;
import com.appspot.ajnweb.page.AbstractPageTest;
import com.appspot.ajnweb.service.TweetService;

import static org.apache.wicket.util.tester.DummyPanelPage.*;

/**
 * {@link TweetDataViewPanel}のテストケース。
 * @author shin1ogawa
 */
public class TweetDataViewPanelTest extends AbstractPageTest {

	/**
	 * コンポーネントツリーの確認。
	 */
	@SuppressWarnings("serial")
	@Test
	public void componentTree() {
		WicketTester tester = new WicketTester();
		tester.startPanel(new TestPanelSource() {

			public Panel getTestPanel(String panelId) {
				return new TweetDataViewPanel(panelId, new DataProvider(), 10);
			}
		});
		tester.assertRenderedPage(DummyPanelPage.class);
		tester.assertComponent(TEST_PANEL_ID, TweetDataViewPanel.class);
		tester.assertComponent(TEST_PANEL_ID + ":container:list", DataView.class);
		tester.assertComponent(TEST_PANEL_ID + ":container:list:1:createdAt", Label.class);
		tester.assertComponent(TEST_PANEL_ID + ":container:list:1:userName", ExternalLink.class);
		tester
			.assertComponent(TEST_PANEL_ID + ":container:list:1:userProfileImageUrl", Image.class);
		tester.assertComponent(TEST_PANEL_ID + ":container:list:1:text", Label.class);
		tester.assertComponent(TEST_PANEL_ID + ":navi", AppEngineAjaxPagingNavigator.class);
		tester.debugComponentTrees(TEST_PANEL_ID);
		tester.dumpPage();
	}


	static class DataProvider implements IDataProvider<Tweet> {

		private static final long serialVersionUID = -4589123994384616956L;

		List<Tweet> list;


		public Iterator<? extends Tweet> iterator(int first, int count) {
			int toIndex = first + count;
			if (toIndex > list.size()) {
				toIndex = list.size();
			}
			return new ArrayList<Tweet>(list.subList(first, toIndex)).iterator();
		}

		public IModel<Tweet> model(Tweet object) {
			return new Model<Tweet>(object);
		}

		public int size() {
			list = TweetService.getRecent();
			return list.size();
		}

		public void detach() {
			list = null;
		}
	}
}
