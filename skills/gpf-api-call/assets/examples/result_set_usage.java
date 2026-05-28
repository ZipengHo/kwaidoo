import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;

import java.util.List;

public class ResultSetUsageExample {

    public int totalCount(ResultSet<Form> resultSet) {
        return resultSet.getTotalCount();
    }

    public List<Form> currentPage(ResultSet<Form> resultSet) {
        return resultSet.getDataList();
    }

    public boolean hasData(ResultSet<Form> resultSet) {
        return resultSet != null
                && resultSet.getDataList() != null
                && !resultSet.getDataList().isEmpty();
    }
}
