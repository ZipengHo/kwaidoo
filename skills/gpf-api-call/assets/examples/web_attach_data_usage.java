import cell.gpf.adur.data.IFormMgr;
import gpf.adur.data.Form;
import gpf.adur.data.WebAttachData;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WebAttachDataUsageExample {

    public void uploadAndBind(Form form) throws Exception {
        byte[] content = "large-file-content".getBytes();
        WebAttachData attachByBytes = IFormMgr.get().uploadWebAttach("video.mp4", content);

        List<WebAttachData> webAttachments = new ArrayList<>();
        webAttachments.add(attachByBytes);

        try (InputStream in = new ByteArrayInputStream("stream-file-content".getBytes())) {
            WebAttachData attachByStream = IFormMgr.get().uploadWebAttach("manual.pdf", in);
            webAttachments.add(attachByStream);
        }

        form.setAttrValue("网络附件", webAttachments);
    }

    public List<WebAttachData> read(Form form) throws Exception {
        return form.getWebAttachs("网络附件");
    }
}
