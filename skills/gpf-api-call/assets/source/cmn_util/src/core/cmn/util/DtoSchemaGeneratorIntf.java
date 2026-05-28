package cmn.util;

import cmn.dto.schema.DtoSchema;

/**
 * DTO 结构生成器接口。
 * 可用于生成 JsonSchema，也可扩展为其他结构定义。
 */
public interface DtoSchemaGeneratorIntf {

	/**
	 * 根据 DTO 类型生成结构定义。
	 *
	 * @param dtoClass DTO 类型
	 * @return 结构定义
	 * @throws Exception 生成失败
	 */
	DtoSchema generateSchema(Class<?> dtoClass) throws Exception;
}
