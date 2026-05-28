package basic.cmn.dto.vote;

import java.io.Serializable;
import java.util.*;

import gpf.anotation.FieldMeta;
import gpf.adur.data.DataType;
import gpf.dc.dto.BusinessModelDto;

/**
 * 投票记录
 *
 */
public class VoteRecordDto extends BusinessModelDto implements Serializable{
    public final static String FormModelId = "gpf.md.basic.VoteRecord";
    public final static String FieldCode_VoteID = "tou2Piao4ID";
    public final static String sVoteID = "投票ID";
    public final static String FieldCode_UserID = "tou2Piao4Ren2ID";
    public final static String sUserID = "投票人ID";
    public final static String FieldCode_UserFullName = "yong4Hu4Sing4Ming2";
    public final static String sUserFullName = "用户姓名";
    public final static String FieldCode_VoteOptionID = "tou2Piao4Syuan3Siang4ID";
    public final static String sVoteOptionID = "投票选项ID";
    public final static String FieldCode_VoteTime = "tou2Piao4Shih2Jian1";
    public final static String sVoteTime = "投票时间";
    public final static String FieldCode_Score = "fen1Jhih2";
    public final static String sScore = "分值";
    public final static String FieldCode_VoteIP = "tou2Piao4IP";
    public final static String sVoteIP = "投票IP";
    public final static String FieldCode_DeviceId = "tou2Piao4She4Bei4";
    public final static String sDeviceId = "投票设备";
    public final static String FieldCode_IsAnonymous = "shih4Fou3Ni4Ming2";
    public final static String sIsAnonymous = "是否匿名";
    @FieldMeta(code = FieldCode_VoteID,name = sVoteID, dataType = DataType.Depend)
    String voteID;
    @FieldMeta(code = FieldCode_UserID,name = sUserID, dataType = DataType.Text)
    String userID;
    @FieldMeta(code = FieldCode_UserFullName,name = sUserFullName, dataType = DataType.Text)
    String userFullName;
    @FieldMeta(code = FieldCode_VoteOptionID,name = sVoteOptionID, dataType = DataType.Text)
    String voteOptionID;
    @FieldMeta(code = FieldCode_VoteTime,name = sVoteTime, dataType = DataType.Date)
    Long voteTime;
    @FieldMeta(code = FieldCode_Score,name = sScore, dataType = DataType.Decimal)
    Double score;
    @FieldMeta(code = FieldCode_VoteIP,name = sVoteIP, dataType = DataType.Text)
    String voteIP;
    @FieldMeta(code = FieldCode_DeviceId,name = sDeviceId, dataType = DataType.Text)
    String deviceId;
    @FieldMeta(code = FieldCode_IsAnonymous,name = sIsAnonymous, dataType = DataType.Boolean)
    Boolean isAnonymous;

    public String getVoteID() {
        return voteID;
    }

    public VoteRecordDto setVoteID(String voteID) {
        this.voteID = voteID;
        return this;
    }

    public String getUserID() {
        return userID;
    }

    public VoteRecordDto setUserID(String userID) {
        this.userID = userID;
        return this;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public VoteRecordDto setUserFullName(String userFullName) {
        this.userFullName = userFullName;
        return this;
    }

    public String getVoteOptionID() {
        return voteOptionID;
    }

    public VoteRecordDto setVoteOptionID(String voteOptionID) {
        this.voteOptionID = voteOptionID;
        return this;
    }

    public Long getVoteTime() {
        return voteTime;
    }

    public VoteRecordDto setVoteTime(Long voteTime) {
        this.voteTime = voteTime;
        return this;
    }

    public Double getScore() {
        return score;
    }

    public VoteRecordDto setScore(Double score) {
        this.score = score;
        return this;
    }

    public String getVoteIP() {
        return voteIP;
    }

    public VoteRecordDto setVoteIP(String voteIP) {
        this.voteIP = voteIP;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public VoteRecordDto setDeviceId(String userAgent) {
        this.deviceId = userAgent;
        return this;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public VoteRecordDto setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
        return this;
    }

    public boolean isAnonymous() {
        return isAnonymous != null &&  isAnonymous;
    }
}
