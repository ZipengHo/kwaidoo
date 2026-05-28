import gpf.adur.data.AssociationData;
import gpf.adur.data.Form;

import java.util.ArrayList;
import java.util.List;

public class AssociationDataUsageExample {

    public void fillSingleAssociation(Form form, String modelId, String code) throws Exception {
        AssociationData associationData = new AssociationData(modelId, code);
        form.setAttrValue("负责人", associationData);
    }

    public void fillMultiAssociation(Form form, String modelId, List<String> codes)
            throws Exception {
        List<AssociationData> list = new ArrayList<>();
        for (String code : codes) {
            list.add(new AssociationData(modelId, code));
        }
        form.setAttrValue("关联商品", list);
    }
}
