package gpf.study;

import bap.java.project.CJavaCenter;
import bap.md.java.CJavaProject;
import bap.md.yun.ArtifactDefine;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.IJson;
import cell.gpf.adur.action.IActionMgr;
import cell.gpf.adur.data.IFormMgr;
import cell.gpf.adur.role.IRoleMgr;
import cell.gpf.adur.user.IUserMgr;
import cell.gpf.dc.backup.IBackupService;
import cell.gpf.dc.concrete.ICDCMgr;
import cell.gpf.dc.concrete.IJavaProjectMgr;
import cell.gpf.dc.config.IPDFMgr;
import cell.web.IModelService;
import com.cdao.impl.entity.field.SlaveTable;
import com.cdao.model.CDoBasic;
import com.cdao.model.CDoModel;
import com.leavay.common.util.SJsonList;
import gpf.adur.data.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FormModelExport {

    public static void test() throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            CJavaProject javaProject = IJavaProjectMgr.get().queryProjectByName("OceanBaseImportTest");
            String projectUuid = javaProject.getUuid();
            ArtifactDefine artifactDef = CJavaCenter.getMe().getArtifactInfo(projectUuid);
            if (artifactDef == null)
            {
                artifactDef = new ArtifactDefine();
                artifactDef.setGroupId("free.open");
                artifactDef.setArtifactId(javaProject.getName());
                artifactDef.setVersion("1.0.0");
                artifactDef.setForeignGid(javaProject.getGid());
            }
            artifactDef.setGroupId("free.open");
            artifactDef.setArtifactId("OceanBaseImportTest");
            artifactDef.setVersion("1.0.0");
            artifactDef.setDescription("");
            SlaveTable<ArtifactDefine> dependTo = new SlaveTable<>();
//			artifactDef.setMainPage(xml.getBytes());
            SJsonList exclusiveModelList = new SJsonList();
            List<CDoModel> models = dao.queryDoList(CDoModel.class,null);
            for(CDoModel model : models){
                exclusiveModelList.add(model.getFullClassName());
            }
            artifactDef.setExclusiveModelList(exclusiveModelList);
            CJavaCenter.getMe().saveArtifactInfo(artifactDef);
        }
    }
    //查询模型列表
    public static ResultSet<FormModel> queryFormModelList() throws Exception {
        IFormMgr formMgr = IFormMgr.get();
        List<String> parentIds = new ArrayList<>();
        parentIds.add(formMgr.getRootBusinessEntityModelId());
        parentIds.add(formMgr.getRootNestingEntityModelId());
        parentIds.add(formMgr.getRootProcessEntityModelId());
        parentIds.add(IActionMgr.get().getRootActionModelId());
        parentIds.add(ICDCMgr.get().getRootCdcId());
        parentIds.add(IPDFMgr.get().getRootPDFId());
        parentIds.add(IUserMgr.get().getRootBasicUserModelId());
        parentIds.add(IRoleMgr.get().getRootOrgModelId());
        ResultSet<FormModel> rs = formMgr.queryFormModelPage(parentIds,null, null,1,Integer.MAX_VALUE);
        return rs;
    }
    //获取表单模型的属性信息
    public static void getFormModelAttribute(ResultSet<FormModel> rs) throws Exception {
        for(FormModel formModel : rs.getDataList()){
            List<FormField> fields = formModel.getFieldList();
            for(FormField field : fields){
                String name = field.getName();
                String description = field.getDescription();
                Boolean isNotNull = field.isNotNull();
                DataType type = field.getDataTypeEnum();
                //当type == DataType.Realte时，assocFormModel为关联的表单模型
                String assocFormModel = field.getAssocFormModel();
                Boolean isAssocMultiSelect = field.isAssocMultiSelect();
                //当type == DataType.Depend时，dependModels为依赖的表单模型
                List<String> dependModels = field.getDependFormModel();
                //当type == DataType.NestingModel时，tableModel为关联的表模型
                String tableModel = field.getTableFormModel();
                //属性扩展为BaseFormFieldExtend，需要比对两个对象内容是否一致
                BaseFormFieldExtend extendInfo = field.getExtendInfo();
            }
            List<TableIndex> indexList = formModel.getIndexList();
            for(TableIndex index : indexList){
                //索引名称
                String name = index.getName();
                //索引包含的字段列表
                List<String> lstFields = index.getLstFields();
                //索引是否唯一
                Boolean isUnique = index.isUnique();
            }
        }
    }
    //序列化表单模型为JSON字符串
    public static String serializeFormModel(FormModel formModel) throws Exception {
        try(IJson json = IBackupService.get().getIJson()){
            return json.toJson(formModel);
        }
    }
}
