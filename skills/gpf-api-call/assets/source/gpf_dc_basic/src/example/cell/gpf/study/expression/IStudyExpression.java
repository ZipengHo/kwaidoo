package cell.gpf.study.expression;

import java.util.Map;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorRuntimeJavaType;
import com.googlecode.aviator.utils.Env;

import cell.CellIntf;

public interface IStudyExpression extends CellIntf{

	public static void main(String[] args) {
		Env env = new Env();
		env.put("$1", 3L);
		env.put("$2", 1L);
		env.put("add", new Add());
		Object value= AviatorEvaluator.execute("add($1,$2)", env);
		System.out.println(value);
		Env env2 = new Env();
		env2.put("$1", 3L);
		env2.put("$2", 1L);
		env2.put("add", new Add2());
		value= AviatorEvaluator.execute("add($1,$2)", env2);
		System.out.println(value);
	}
	
	public static class Add extends AbstractFunction{

		/**
		 * 
		 */
		private static final long serialVersionUID = 5876276011120567684L;

		@Override
		public String getName() {
			return "add";
		}
		
		@Override
		public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
			Long v1 = (Long) arg1.getValue(env);
			Long v2 = (Long) arg2.getValue(env);
			return AviatorRuntimeJavaType.valueOf(v1+v2);
		}
		
	}
	
	public static class Add2 extends AbstractFunction{

		/**
		 * 
		 */
		private static final long serialVersionUID = -1107422936224769644L;

		@Override
		public String getName() {
			return "add";
		}
		
		@Override
		public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
			Long v1 = (Long) arg1.getValue(env);
			Long v2 = (Long) arg2.getValue(env);
			return AviatorRuntimeJavaType.valueOf(v1-v2);
		}
		
	}
}
