import gpf.adur.data.AttachData;
import gpf.adur.data.Form;

import java.util.ArrayList;
import java.util.List;

public class AttachDataUsageExample {

    public void fillAttachments(Form form) throws Exception {
        List<AttachData> attachments = new ArrayList<>();
        attachments.add(new AttachData("readme.txt", "示例内容".getBytes()));
        attachments.add(new AttachData("order.json", "{\"id\":\"O001\"}".getBytes()));
        form.setAttrValue("附件", attachments);
    }

    public List<String> readFileNames(Form form) throws Exception {
        List<AttachData> attachments = form.getAttachments("附件");
        List<String> fileNames = new ArrayList<>();
        if (attachments == null) {
            return fileNames;
        }
        for (AttachData attach : attachments) {
            fileNames.add(attach.getFileName());
        }
        return fileNames;
    }
}
