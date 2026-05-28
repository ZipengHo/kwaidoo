package gpf.adur.data;

import java.io.Serializable;

import com.kwaidoo.ms.tool.CmnUtil;
import com.kwaidoo.ms.tool.ToolUtilities;

import cell.fileserver.http.IFileServerHttpClient;
import cell.gpf.adur.data.IFormMgr;
import cson.core.CsonPojo;

public class WebAttachData extends CsonPojo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3371149011392672095L;
	String uuid;
	String fileUuid;
	String name;
	Long size;
	String md5;
	transient byte[] content;
	
	public WebAttachData() {
		this.uuid = ToolUtilities.allockUUIDWithUnderline();
	}
	
	public WebAttachData(String fileUuid,String name) {
		this.fileUuid = fileUuid;
		this.name = name;
		this.uuid = ToolUtilities.allockUUIDWithUnderline();
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getFileUuid() {
		return fileUuid;
	}

	public String getName() {
		return name;
	}

	public Long getSize() {
		return size;
	}

	public String getMd5() {
		return md5;
	}

	public byte[] getContent() throws Exception {
		if(content == null) {
			if(!CmnUtil.isStringEmpty(fileUuid)) {
				content = IFormMgr.get().downloadWebAttach(fileUuid);
			}
		}
		return content;
	}

	public WebAttachData setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public WebAttachData setFileUuid(String fileUuid) {
		this.fileUuid = fileUuid;
		return this;
	}

	public WebAttachData setName(String name) {
		this.name = name;
		return this;
	}

	public WebAttachData setSize(Long size) {
		this.size = size;
		return this;
	}

	public WebAttachData setMd5(String md5) {
		this.md5 = md5;
		return this;
	}
	
	public WebAttachData copy() {
		WebAttachData cloneData = new WebAttachData(fileUuid, name);
		cloneData.setUuid(ToolUtilities.allockUUIDWithUnderline())
		.setSize(size).setMd5(md5);
		return cloneData;
	}
	/**
	 * 获取网络附加的分享链接
	 * @return
	 * @throws Exception
	 */
	public String getShareFileUrl() throws Exception {
		if(CmnUtil.isStringEmpty(fileUuid))
			return null;
		return IFormMgr.get().getWebAttachShareUrl(fileUuid);
	}

}
