package gpf.dc.basic.dto.util;

import java.io.File;
import java.io.Serializable;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;

public class QrConfigDto extends QrConfig implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4664487601128505098L;

	public static void main(String[] args) {
		QrConfigDto config = new QrConfigDto();
		config.setWidth(300).setHeight(300);
		// 高纠错级别
		config.setErrorCorrection(ErrorCorrectionLevel.H);
		// 设置边距，既二维码和背景之间的边距
		config.setMargin(3);
		// 设置前景色，既二维码颜色（青色）
//		config.setForeColor(Color.CYAN);
		// 设置背景色（灰色）
//		config.setBackColor(Color.GRAY);
		config.setImg(new File("D://Digital-MS(橙色).png"));
		QrCodeUtil.generate("#小程序://跬笃DMS/ZFOzJiBVOP1JNWI", config, FileUtil.file("D:/qrcodeCustom.jpg"));	
	}
}
