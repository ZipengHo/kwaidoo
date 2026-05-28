package cell.gpf.dc.basic.qrcode;

import java.io.File;
import java.io.OutputStream;

import bap.cells.Cells;
import cell.CellIntf;
import gpf.dc.basic.dto.util.QrConfigDto;

public interface IQrCodeService extends CellIntf {
	static IQrCodeService get() {
		return Cells.get(IQrCodeService.class);
	}

	String decode(File qrCode) throws Exception;

	/**
	 * 根据内容在指定文件路径生成对应二维码图片文件
	 *
	 * @param content
	 * @param qrConfig
	 * @param parentPath 二维码文件存储路径
	 * @param fileName   二维码文件名称
	 * @return
	 * @throws Exception
	 */
	File generate2File(String content, QrConfigDto qrConfig, String parentPath, String fileName) throws Exception;

	/**
	 * 根据内容生成二维码图片的二进制内容设置到对应流对象上
	 *
	 * @param content
	 * @param qrConfig
	 * @param outputStream
	 * @throws Exception
	 */
	void generate2Stream(String content, QrConfigDto qrConfig, OutputStream outputStream) throws Exception;
	/**
	 * 根据内容生成二维码图片的二进制内容
	 * @param content
	 * @param qrConfig
	 * @return
	 * @throws Exception
	 */
	byte[] getQrCodeImage(String content, QrConfigDto qrConfig)throws Exception;
}
