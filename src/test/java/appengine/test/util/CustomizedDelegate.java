package appengine.test.util;

import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.Environment;
import com.google.apphosting.api.ApiProxy.LogRecord;

/**
 * 独自の{@link ApiProxy.Delegate}のためのインターフェース。
 * @author shin1ogawa
 */
public interface CustomizedDelegate extends ApiProxy.Delegate<Environment> {

	/**
	 * @return 元々設定されていた{@link ApiProxy.Delegate}
	 */
	ApiProxy.Delegate<Environment> getOriginal();

	/**
	 * 元々設定されていた{@link ApiProxy.Delegate}に戻す。
	 * @return 自分自身
	 */
	CustomizedDelegate delegateToOriginal();


	/**
	 * {@link CustomizedDelegate}の抽象クラス。
	 * @author shin1ogawa
	 */
	public static abstract class AbstractCustomizedDelegate implements CustomizedDelegate {

		@SuppressWarnings("unchecked")
		protected final ApiProxy.Delegate<Environment> original = ApiProxy.getDelegate();


		public void log(Environment env, LogRecord logRecord) {
			getOriginal().log(env, logRecord);
		}

		public CustomizedDelegate delegateToOriginal() {
			ApiProxy.setDelegate(original);
			return this;
		}

		/**
		 * @return the original
		 * @category accessor
		 */
		public ApiProxy.Delegate<Environment> getOriginal() {
			return original;
		}
	}
}
