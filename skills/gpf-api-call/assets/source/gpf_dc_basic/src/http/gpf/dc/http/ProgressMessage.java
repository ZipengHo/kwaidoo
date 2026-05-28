package gpf.dc.http;

import gpf.dc.basic.fe.enums.EnumUtil;

import java.io.Serializable;
import java.time.LocalDate;

public class ProgressMessage implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1567133026741036872L;
	/**
	 * 进度通知对象uuid
	 */
	String uuid;
	//progress、complete、error
	/**
	 * 进度通知类型
	 */
	String type;
	/**
	 * 进度通知进度
	 */
	int progress;
	/**
	 * 进度通知消息
	 */
	String message;
	/**
	 * 进度通知异常信息
	 */
	String error;
	/**
	 * 进度通知时间戳
	 */
	Long timestamp;

	public String getUuid() {
		return uuid;
	}
	public String getType() {
		return type;
	}

	public ProgressTypeEnum getTypeEnum() {
		return EnumUtil.getEnumByName(ProgressTypeEnum.class, type);
	}

	public ProgressMessage getTypeEnum(ProgressTypeEnum type) {
		if(type == null) {
			this.type = null;
		}else {
			this.type = type.name();
		}
		return this;
	}

	public int getProgress() {
		return progress;
	}

	public String getMessage() {
		return message;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public ProgressMessage setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public ProgressMessage setType(String type) {
		this.type = type;
		return this;
	}

	public ProgressMessage setProgress(int progress) {
		this.progress = progress;
		return this;
	}

	public ProgressMessage setMessage(String message) {
		this.message = message;
		return this;
	}

	public ProgressMessage setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getError() {
		return error;
	}
	public ProgressMessage setError(String error) {
		this.error = error;
		return this;
	}
}

