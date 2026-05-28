package cmn.enums;

import web.constant.ModelConst;
/**
 * 嵌套表数据更新方式
 * @author chenxb
 *
 */
public enum NestingTableUpdateMode {
	//不保存嵌套表数据
	Nothing(ModelConst.SLAVE_SAVE_NOTHING)
	//删除所有后重建
	,DeleteAndCreate(ModelConst.SLAVE_SAVE_DELETE_AND_CREATE)
	//增量更新删除
	,IncrementUpdate(ModelConst.SLAVE_SAVE_INCREMENT_UPDATE)
	//删除所有后重建，使用新的uuid
	,DeleteAndCreateWithNewUuid(ModelConst.SLAVE_SAVE_DELETE_AND_CREATE_WITH_NEW_UUID);
	int value;
	private NestingTableUpdateMode(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}
