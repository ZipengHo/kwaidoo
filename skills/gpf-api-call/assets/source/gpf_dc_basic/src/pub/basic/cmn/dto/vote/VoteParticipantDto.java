package basic.cmn.dto.vote;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.dto.NestingDto;

import java.io.Serializable;

/**
 * 投票参与人
 *
 */
public class VoteParticipantDto extends NestingDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3794040653929794768L;
	public final static String FormModelId = "gpf.md.slave.VoteParticipant";
	public final static String FieldCode_UserID = "yong4Hu4ID";
	public final static String sUserID = "用户ID";
	public final static String FieldCode_UserFullName = "yong4Hu4Sing4Ming2";
	public final static String sUserFullName = "用户姓名";
	public final static String FieldCode_HasVoted = "shih4Fou3Yi3Tou2";
	public final static String sHasVoted = "是否已投";
	public final static String FieldCode_VoteTime = "tou2Piao4Shih2Jian1";
	public final static String sVoteTime = "投票时间";
	public final static String FieldCode_CreateTime = "chuang4Jian4Shih2Jian1";
	public final static String sCreateTime = "创建时间";
	public final static String FieldCode_Weight = "tou2Piao4Cyuan2Jhong4";
	public final static String sWeight = "投票权重";
	public final static String FieldCode_DeviceId = "she4Bei4ID";
	public final static String sDeviceId = "设备ID";
	@FieldMeta(code = FieldCode_UserID, name = sUserID, dataType = DataType.Text)
	String userID;
	@FieldMeta(code = FieldCode_UserFullName, name = sUserFullName, dataType = DataType.Text)
	String userFullName;
	@FieldMeta(code = FieldCode_HasVoted, name = sHasVoted, dataType = DataType.Boolean)
	Boolean hasVoted;
	@FieldMeta(code = FieldCode_VoteTime, name = sVoteTime, dataType = DataType.Date)
	Long voteTime;
	@FieldMeta(code = FieldCode_CreateTime, name = sCreateTime, dataType = DataType.Date)
	Long createTime;
	@FieldMeta(code = FieldCode_Weight, name = sWeight, dataType = DataType.Text)
	String weight;
	@FieldMeta(code = FieldCode_DeviceId, name = sDeviceId, dataType = DataType.Text)
	String deviceId;

	public String getUserID() {
		return userID;
	}

	public VoteParticipantDto setUserID(String userID) {
		this.userID = userID;
		return this;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public VoteParticipantDto setUserFullName(String userFullName) {
		this.userFullName = userFullName;
		return this;
	}

	public Boolean getHasVoted() {
		return hasVoted;
	}

	public VoteParticipantDto setHasVoted(Boolean hasVoted) {
		this.hasVoted = hasVoted;
		return this;
	}

	public Long getVoteTime() {
		return voteTime;
	}

	public VoteParticipantDto setVoteTime(Long voteTime) {
		this.voteTime = voteTime;
		return this;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public VoteParticipantDto setCreateTime(Long createTime) {
		this.createTime = createTime;
		return this;
	}

	public String getWeight() {
		return weight;
	}

	public VoteParticipantDto setWeight(String weight) {
		this.weight = weight;
		return this;
	}

	public String getDeviceId() {
		return deviceId;
	}
	
	public VoteParticipantDto setDeviceId(String deviceId) {
		this.deviceId = deviceId;
		return this;
	}
}
