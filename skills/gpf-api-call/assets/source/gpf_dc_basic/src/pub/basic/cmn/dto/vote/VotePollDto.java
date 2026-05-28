package basic.cmn.dto.vote;

import java.io.Serializable;
import java.util.List;

import com.leavay.ms.tool.CmnUtil;

import cmn.util.NullUtil;
import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.basic.fe.enums.EnumUtil;
import gpf.dc.dto.BusinessModelDto;

/**
 * 投票主题表
 *
 */
public class VotePollDto extends BusinessModelDto implements Serializable {
	public final static String FormModelId = "gpf.md.basic.VotePoll";
	public final static String FieldCode_VoteSubject = "tou2Piao4Jhu3Ti2";
	public final static String sVoteSubject = "投票主题";
	public final static String FieldCode_VoteDesc = "tou2Piao4Shuo1Ming2";
	public final static String sVoteDesc = "投票说明";
	public final static String FieldCode_VoteMode = "tou2Piao4Mo2Shih4";
	public final static String sVoteMode = "投票模式";
	public final static String FieldCode_VoteType = "tou2Piao4Lei4Sing2";
	public final static String sVoteType = "投票类型";
	public final static String FieldCode_RuleType = "guei1Ze2Lei4Sing2";
	public final static String sRuleType = "规则类型";
	public final static String FieldCode_PassRatio = "tong1Guo4Bi3Li4Yu4Jhih2";
	public final static String sPassRatio = "通过比例阈值";
	public final static String FieldCode_VoteOption = "tou2Piao4Syuan3Siang4";
	public final static String sVoteOption = "投票选项";
	public final static String FieldCode_IsAnonymous = "shih4Fou3Ni4Ming2";
	public final static String sIsAnonymous = "是否匿名";
	public final static String FieldCode_Creator = "chuang4Jian4Ren2";
	public final static String sCreator = "创建人";
	public final static String FieldCode_CreatorID = "chuang4Jian4Ren2ID";
	public final static String sCreatorID = "创建人ID";
	public final static String FieldCode_StartTime = "kai1Shih3Shih2Jian1";
	public final static String sStartTime = "开始时间";
	public final static String FieldCode_EndTime = "jie2Jhih3Shih2Jian1";
	public final static String sEndTime = "截止时间";
	public final static String FieldCode_VoteParticipant = "tou2Piao4Can1Yu3Ren2";
	public final static String sVoteParticipant = "投票参与人";
	public final static String FieldCode_Status = "jhuang4Tai4";
	public final static String sStatus = "状态";
	public final static String FieldCode_Result = "jie2Guo3";
	public final static String sResult = "结果";
	public final static String FieldCode_CreateTime = "chuang4Jian4Shih2Jian1";
	public final static String sCreateTime = "创建时间";
	public final static String FieldCode_UpdateTime = "geng4Sin1Shih2Jian1";
	public final static String sUpdateTime = "更新时间";
	@FieldMeta(code = FieldCode_VoteSubject, name = sVoteSubject, dataType = DataType.Text)
	String voteSubject;
	@FieldMeta(code = FieldCode_VoteDesc, name = sVoteDesc, dataType = DataType.Text)
	String voteDesc;
	@FieldMeta(code = FieldCode_VoteMode, name = sVoteMode, dataType = DataType.Text)
	String voteMode;
	@FieldMeta(code = FieldCode_VoteType, name = sVoteType, dataType = DataType.Text)
	String voteType;
	@FieldMeta(code = FieldCode_RuleType, name = sRuleType, dataType = DataType.Text)
	String ruleType;
	@FieldMeta(code = FieldCode_PassRatio, name = sPassRatio, dataType = DataType.Decimal)
	Double passRatio;
	@FieldMeta(code = FieldCode_VoteOption, name = sVoteOption, dataType = DataType.NestingModel, tableModel = VoteOptionDto.class)
	List<VoteOptionDto> voteOption;
	@FieldMeta(code = FieldCode_IsAnonymous, name = sIsAnonymous, dataType = DataType.Boolean)
	Boolean isAnonymous;
	@FieldMeta(code = FieldCode_Creator, name = sCreator, dataType = DataType.Text)
	String creator;
	@FieldMeta(code = FieldCode_CreatorID, name = sCreatorID, dataType = DataType.Text)
	String creatorID;
	@FieldMeta(code = FieldCode_StartTime, name = sStartTime, dataType = DataType.Date)
	Long startTime;
	@FieldMeta(code = FieldCode_EndTime, name = sEndTime, dataType = DataType.Date)
	Long endTime;
	@FieldMeta(code = FieldCode_VoteParticipant, name = sVoteParticipant, dataType = DataType.NestingModel, tableModel = VoteParticipantDto.class)
	List<VoteParticipantDto> voteParticipant;
	@FieldMeta(code = FieldCode_Status, name = sStatus, dataType = DataType.Text)
	String status;
	@FieldMeta(code = FieldCode_Result, name = sResult, dataType = DataType.Text)
	String result;
	@FieldMeta(code = FieldCode_CreateTime, name = sCreateTime, dataType = DataType.Date)
	Long createTime;
	@FieldMeta(code = FieldCode_UpdateTime, name = sUpdateTime, dataType = DataType.Date)
	Long updateTime;

	public String getVoteSubject() {
		return voteSubject;
	}

	public VotePollDto setVoteSubject(String voteSubject) {
		this.voteSubject = voteSubject;
		return this;
	}

	public String getVoteDesc() {
		return voteDesc;
	}

	public VotePollDto setVoteDesc(String voteDesc) {
		this.voteDesc = voteDesc;
		return this;
	}
	
	public String getVoteMode() {
		return voteMode;
	}
	
	public VotePollDto setVoteMode(String voteMode) {
		this.voteMode = voteMode;
		return this;
	}

	public String getVoteType() {
		return voteType;
	}

	public VotePollDto setVoteType(String voteType) {
		this.voteType = voteType;
		return this;
	}

	public String getRuleType() {
		return ruleType;
	}

	public VotePollDto setRuleType(String ruleType) {
		this.ruleType = ruleType;
		return this;
	}

	public Double getPassRatio() {
		return passRatio;
	}

	public VotePollDto setPassRatio(Double passRatio) {
		this.passRatio = passRatio;
		return this;
	}

	public List<VoteOptionDto> getVoteOption() {
		return voteOption;
	}

	public VotePollDto setVoteOption(List<VoteOptionDto> voteOption) {
		this.voteOption = voteOption;
		return this;
	}

	public Boolean getAnonymous() {
		return isAnonymous;
	}

	public boolean isAnonymous() {
		return isAnonymous != null && isAnonymous;
	}

	public VotePollDto setAnonymous(Boolean anonymous) {
		isAnonymous = anonymous;
		return this;
	}

	public String getCreator() {
		return creator;
	}

	public VotePollDto setCreator(String creator) {
		this.creator = creator;
		return this;
	}

	public String getCreatorID() {
		return creatorID;
	}

	public VotePollDto setCreatorID(String creatorID) {
		this.creatorID = creatorID;
		return this;
	}

	public Long getStartTime() {
		return startTime;
	}

	public VotePollDto setStartTime(Long startTime) {
		this.startTime = startTime;
		return this;
	}

	public Long getEndTime() {
		return endTime;
	}

	public VotePollDto setEndTime(Long endTime) {
		this.endTime = endTime;
		return this;
	}

	public List<VoteParticipantDto> getVoteParticipant() {
		return voteParticipant;
	}

	public VotePollDto setVoteParticipant(List<VoteParticipantDto> voteParticipant) {
		this.voteParticipant = voteParticipant;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public VotePollDto setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getResult() {
		return result;
	}

	public VotePollDto setResult(String result) {
		this.result = result;
		return this;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public VotePollDto setCreateTime(Long createTime) {
		this.createTime = createTime;
		return this;
	}

	public Long getUpdateTime() {
		return updateTime;
	}

	public VotePollDto setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
		return this;
	}

	public VotePollDto setStatus(VoteStatus status) {
		this.status = status.name();
		return this;
	}

	public VoteStatus getStatusEnum() {
		return EnumUtil.getEnumByName(VoteStatus.class, status);
	}

	public VoteRuleType getRuleTypeEnum() {
		return EnumUtil.getEnumByName(VoteRuleType.class, ruleType);
	}

	public VotePollDto setRuerType(VoteRuleType ruleType) {
		this.ruleType = ruleType.name();
		return this;
	}

	public VoteType getVoteTypeEnum() {
		return EnumUtil.getEnumByName(VoteType.class, voteType);
	}

	public VotePollDto setVoteType(VoteType voteType) {
		this.voteType = voteType.name();
		return this;
	}
	
	public VoteMode getVoteModeEnum() {
		return EnumUtil.getEnumByName(VoteMode.class, voteMode);
	}

	public VotePollDto setVoteMode(VoteMode voteMode) {
		this.voteMode = voteMode.name();
		return this;
	}

	public VoteOptionDto getVoteOption(String voteOptionNamme) {
		if (voteOptionNamme == null) {
			return null;
		}
		for (VoteOptionDto voteOptionDto : NullUtil.get(voteOption)) {
			if (CmnUtil.isStringEqual(voteOptionDto.getOption(), voteOptionNamme)) {
				return voteOptionDto;
			}
		}
		return null;
	}
}
