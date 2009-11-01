package com.appspot.ajnweb.page;

import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.WicketApplication;
import com.appspot.ajnweb.component.TweetDataView;

/**
 * {@link Recent}のテストケース。
 * @author shin1ogawa
 */
public class RecentTest extends AbstractPageTest {

	/**
	 * コンポーネントツリーの確認だけ。
	 */
	@Test
	public void componentTree() {
		WicketTester tester = new WicketTester(new WicketApplication());
		tester.startPage(Recent.class);
		tester.assertRenderedPage(Recent.class);

		tester.assertComponent("navi", AppEngineAjaxPagingNavigator.class);
		tester.assertComponent("container:list", TweetDataView.class);
		tester.assertComponent("container:list:1", Item.class); // 1件目の描画を確認
	}

	/**
	 * エラー無くAjaxでページングできる事を確認。
	 */
	@Test
	public void paging() {
		WicketTester tester = new WicketTester(new WicketApplication());
		tester.startPage(Recent.class);
		tester.assertRenderedPage(Recent.class);
		tester.setCreateAjaxRequest(true);
		tester.executeAjaxEvent("navi:next", "onclick");
		tester.assertComponent("container:list:26", Item.class); // 26件目(2page目)の描画を確認
	}
}
