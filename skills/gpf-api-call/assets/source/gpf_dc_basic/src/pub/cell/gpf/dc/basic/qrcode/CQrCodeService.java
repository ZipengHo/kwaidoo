package cell.gpf.dc.basic.qrcode;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import bap.cells.BasicCell;
import cn.hutool.extra.qrcode.QrCodeUtil;
import gpf.dc.basic.dto.util.QrConfigDto;

public class CQrCodeService extends BasicCell implements IQrCodeService {

	@Override
	public String decode(File qrCode) throws Exception {
		return QrCodeUtil.decode(qrCode);
	}

	@Override
	public File generate2File(String content, QrConfigDto qrConfig, String parentPath, String fileName) throws Exception {
		return QrCodeUtil.generate(content, qrConfig, new File(parentPath + File.separator + fileName + ".png"));
	}

	@Override
	public void generate2Stream(String content, QrConfigDto qrConfig, OutputStream outputStream) throws Exception {
		QrCodeUtil.generate(content, qrConfig, "png", outputStream);
	}
	
	@Override
	public byte[] getQrCodeImage(String content, QrConfigDto qrConfig) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		generate2Stream(content, qrConfig, bos);
		return bos.toByteArray();
	}
}
