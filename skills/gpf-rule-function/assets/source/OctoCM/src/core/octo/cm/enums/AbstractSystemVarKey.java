package octo.cm.enums;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cell.octo.cm.IContext;
import cmn.reflect.TypeToken;
import octo.cm.intf.SystemVarKeyIntf;

public abstract class AbstractSystemVarKey<T> implements SystemVarKeyIntf<T> {

	String varKey;
	Class<T> varClass;

	public AbstractSystemVarKey(String varKey, Class<T> varClass) {
		this.varKey = varKey;
		this.varClass = varClass;
	}
	
    public AbstractSystemVarKey(String varKey, TypeToken<T> typeToken) {
        this.varKey = varKey;
        Type type = typeToken.getType();
        
        // This cast is safe because TypeToken ensures T is a valid type at compile-time
        if (type instanceof ParameterizedType) {
            // This is for generic types like List<Form>
            this.varClass = (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            // This is for simple types like String or Integer
            this.varClass = (Class<T>) type;
        } else {
            throw new IllegalArgumentException("Unsupported TypeToken type: " + type.getTypeName());
        }
    }

	@Override
	public T getContextValue(IContext context) {
		Object value = context.getParam(getVarKey());

		// 在这里进行安全的类型检查和转换
		if (value == null) {
			return null;
		}
		if (!getVarClass().isInstance(value)) {
			// 类型不匹配时抛出异常，提供详细信息
			throw new ClassCastException(String.format("参数[%s]值不是类[%s]的实例对象,实际类型：[%s]", getVarKey(),
					getVarClass().getName(), value.getClass().getName()));
		}
		return getVarClass().cast(value);
	}
	
	@Override
	public String getVarKey() {
		return varKey;
	}
	
	@Override
	public Class<T> getVarClass() {
		return varClass;
	}
}