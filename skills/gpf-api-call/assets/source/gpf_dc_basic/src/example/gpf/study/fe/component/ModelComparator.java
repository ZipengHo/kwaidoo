package gpf.study.fe.component;

import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.FormModel;

public class ModelComparator {

    /**
     * 比较两个模型的差异，输出差异
     * @param deserializedFormModel 反序列化后的表单模型
     */
    public static void main(FormModel deserializedFormModel) throws Exception {
        String formModelId = deserializedFormModel.getId();
        FormModel formModel = IFormMgr.get().queryFormModel(formModelId);
        // 比较模型差异
        // ...
    }
}
