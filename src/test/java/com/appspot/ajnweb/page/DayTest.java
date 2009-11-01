package com.appspot.ajnweb.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

import appengine.wicket.component.AppEngineAjaxPagingNavigator;

import com.appspot.ajnweb.WicketApplication;
import com.appspot.ajnweb.component.TweetDataView;

/**
 * {@link Day}のテストケース。
 * @author shin1ogawa
 */
public class DayTest extends AbstractPageTest {

	/**
	 * コンポーネントツリーの確認。
	 */
	@Test
	public void componentTree() {
		WicketTester tester = new WicketTester(new WicketApplication());
		tester.startPage(Day.class, new PageParameters("0=2009-10-28"));
		tester.assertRenderedPage(Day.class);
		tester.debugComponentTrees();

		tester.assertComponent("ymd", Label.class);
		tester.assertComponent("navi", AppEngineAjaxPagingNavigator.class);
		tester.assertComponent("container:list", TweetDataView.class);
		tester.assertComponent("container:list:1", Item.class); // 1件目の描画を確認
	}
}
