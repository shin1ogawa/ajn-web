package com.appspot.ajnweb;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.target.coding.IndexedHybridUrlCodingStrategy;
import org.apache.wicket.session.ISessionStore;

import appengine.wicket.AppEngineWebApplicationHelper;

import com.appspot.ajnweb.page.Day;
import com.appspot.ajnweb.page.Recent;

/**
 * @author shin1ogawa
 */
public class WicketApplication extends WebApplication {

	static final Logger LOGGER = Logger.getLogger(WicketApplication.class.getName());

	AppEngineWebApplicationHelper helper = new AppEngineWebApplicationHelper(this);


	@Override
	public Class<Recent> getHomePage() {
		return Recent.class;
	}

	private void mountAll() {
		mountBookmarkablePage("recent", Recent.class);
		mount(new IndexedHybridUrlCodingStrategy("day", Day.class));
	}

	@Override
	protected void init() {
		super.init();
		helper.init();
		mountAll();
		getPageSettings().setAutomaticMultiWindowSupport(false);
		addComponentInstantiationListener(new IComponentInstantiationListener() {

			public void onInstantiation(Component component) {
				if (component instanceof FormComponent<?>) {
					component.add(new OnFormComponentValidateErrorBehavior(component));
				}
			}
		});
	}

	@Override
	public String getConfigurationType() {
		return helper.getConfigurationType();
	}

	@Override
	protected ISessionStore newSessionStore() {
		return helper.newSessionStore();
	}

	@Override
	protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
		return helper.newWebRequest(servletRequest);
	}


	/**
	 * バリデートでエラーになったコンポーネントのスタイルを変更するBehavior.
	 * 
	 * @author shin1ogawa
	 */
	static class OnFormComponentValidateErrorBehavior extends AttributeModifier {

		private static final long serialVersionUID = -1673405116562894949L;


		public OnFormComponentValidateErrorBehavior(final Component related) {
			super("class", true, new AbstractReadOnlyModel<String>() {

				private static final long serialVersionUID = 5526756608214398159L;


				@Override
				public String getObject() {
					if (Session.get() == null || Session.get().isSessionInvalidated()) {
						return null;
					}
					if (Session.get().getFeedbackMessages().hasErrorMessageFor(related)) {
						return "validateError";
					}
					return null;
				}
			});
		}
	}
}
