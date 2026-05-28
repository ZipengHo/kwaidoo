import cell.cdao.IDao;
import cell.gpf.adur.data.IFormMgr;
import com.cdao.dto.DataRow;
import gpf.adur.data.Form;
import gpf.adur.data.FormModel;
import gpf.adur.data.ResultSet;
import org.nutz.dao.Cnd;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FormQueryExample {

    /**
     * 使用条件查询表单数据（分页）
     */
    public ResultSet<Form> query(IDao dao, String modelId, String status) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode("状态");
        Cnd cnd = Cnd.NEW();
        cnd.where().andEquals(statusFieldCode, status);
        return IFormMgr.get().queryFormPage(dao, modelId, cnd, 1, 20, true, false);
    }

    /**
     * 使用自定义SQL查询表单数据（分页，带总数）
     * 适用场景：复杂查询、多表关联、聚合统计
     * 
     * 关键点：
     * - SQL中加入 ResultSet.TotalCount_Select 获取总记录数
     * - extFields.add(ResultSet.TotalCount) 标记结果包含总数字段
     */
    public ResultSet<Form> queryBySql(IDao dao, String modelId, String status) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode("状态");
        FormModel formModel = IFormMgr.get().queryFormModel(modelId);
        String tableName = formModel.getTableName();
        
        String sql = String.format(
            "SELECT *, %s FROM %s WHERE %s = '%s'",
            ResultSet.TotalCount_Select,
            tableName, statusFieldCode, status
        );
        
        Set<String> extFields = new LinkedHashSet<>();
        extFields.add(ResultSet.TotalCount);
        
        Cnd cnd = Cnd.NEW();
        return IFormMgr.get().queryFormPageBySql(dao, modelId, sql, extFields, cnd, 1, 20);
    }

    /**
     * 使用自定义SQL查询聚合结果（返回原始DataRow）
     * 适用场景：统计汇总、GROUP BY聚合、多表关联查询
     * 
     * 关键点：
     * - 返回 List<DataRow>，不做 Form 转换
     * - DataRow的字段名通常是小写（如 "status"、"total_count"）
     * - 需要总数时，可调用 queryLong 另行查询
     * 
     * 示例：查询各状态的数量统计
     */
    public List<StatusCountDto> queryAggregation(IDao dao, String modelId) throws Exception {
        String statusFieldCode = IFormMgr.get().getFieldCode("状态");
        FormModel formModel = IFormMgr.get().queryFormModel(modelId);
        String tableName = formModel.getTableName();
        
        String sql = String.format(
            "SELECT %s, COUNT(*) as total_count FROM %s GROUP BY %s",
            statusFieldCode, tableName, statusFieldCode
        );
        Cnd cnd = Cnd.NEW();
        List<DataRow> rows = IFormMgr.get().queryDataRowsBySql(dao, sql, cnd, 1, 1000);
        
        // DataRow → Dto 映射：字段名是小写
        List<StatusCountDto> result = new ArrayList<>();
        for (DataRow row : rows) {
            String statusValue = row.getString("status");      // 小写：SQL中的字段别名
            Long count = row.getLong("total_count");           // 小写：COUNT(*) as total_count
            result.add(new StatusCountDto(statusValue, count));
        }
        return result;
    }
    
    /**
     * 聚合查询结果DTO
     * 用于封装 DataRow 数据，显性体现字段名是小写的处理
     */
    public static class StatusCountDto {
        private String status;
        private Long count;
        
        public StatusCountDto(String status, Long count) {
            this.status = status;
            this.count = count;
        }
        
        public String getStatus() { return status; }
        public Long getCount() { return count; }
    }
}
