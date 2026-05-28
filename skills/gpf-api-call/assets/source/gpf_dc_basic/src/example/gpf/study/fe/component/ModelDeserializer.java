package gpf.study.fe.component;

import cell.cmn.IJson;
import cell.gpf.dc.backup.IBackupService;
import gpf.adur.data.FormModel;

public class ModelDeserializer {
    /**
     * 反序列化表单模型
     * @param json 表单模型的JSON字符串
     * @return 反序列化后的表单模型
     */
    public static FormModel deserialize(String json) {
        try(IJson jsonObj = IBackupService.get().getIJson()){
            return jsonObj.fromJson(json, FormModel.class);
        }
    }
}
