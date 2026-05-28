package cmn.enums;

import javax.swing.JOptionPane;
/**
* 进度通知消息类型
*
 */
public enum ProgressConfirmOperation {
	YES(JOptionPane.YES_OPTION),NO(JOptionPane.NO_OPTION);
	int value;
	private ProgressConfirmOperation(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
}