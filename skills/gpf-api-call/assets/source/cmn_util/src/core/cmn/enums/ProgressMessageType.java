package cmn.enums;

import javax.swing.JOptionPane;
/**
* 进度通知消息类型
*
 */
public enum ProgressMessageType {
	info(JOptionPane.INFORMATION_MESSAGE),warning(JOptionPane.WARNING_MESSAGE),error(JOptionPane.ERROR_MESSAGE),success(JOptionPane.PLAIN_MESSAGE);
	int value;
	private ProgressMessageType(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}