package basic.cmn.dto.vote;

import gpf.dc.basic.fe.enums.EnumUtil;

import java.io.Serializable;
import java.util.List;
/**
 * 用户参与的投票 poll
 */
public class UserVotePollDto implements Serializable {

    String uuid;
    String code;
    String voteSubject;
    String voteDesc;
    String voteType;
    List<VoteOptionDto> voteOption;
    Boolean isAnonymous;
    String creator;
    String creatorID;
    Long startTime;
    Long endTime;
    String status;
    Long createTime;
    Long updateTime;
    String selectedOption;
//    boolean isVoted;
//    int votedCount;
    int totalCount;

    public String getUuid() {
        return uuid;
    }

    public UserVotePollDto setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UserVotePollDto setCode(String code) {
        this.code = code;
        return this;
    }

    public String getVoteSubject() {
        return voteSubject;
    }

    public UserVotePollDto setVoteSubject(String voteSubject) {
        this.voteSubject = voteSubject;
        return this;
    }

    public String getVoteDesc() {
        return voteDesc;
    }

    public UserVotePollDto setVoteDesc(String voteDesc) {
        this.voteDesc = voteDesc;
        return this;
    }

    public String getVoteType() {
        return voteType;
    }

    public UserVotePollDto setVoteType(String voteType) {
        this.voteType = voteType;
        return this;
    }

    public List<VoteOptionDto> getVoteOption() {
        return voteOption;
    }

    public UserVotePollDto setVoteOption(List<VoteOptionDto> voteOption) {
        this.voteOption = voteOption;
        return this;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public UserVotePollDto setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public UserVotePollDto setCreator(String creator) {
        this.creator = creator;
        return this;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public UserVotePollDto setCreatorID(String creatorID) {
        this.creatorID = creatorID;
        return this;
    }

    public Long getStartTime() {
        return startTime;
    }

    public UserVotePollDto setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getEndTime() {
        return endTime;
    }

    public UserVotePollDto setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserVotePollDto setStatus(String status) {
        this.status = status;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public UserVotePollDto setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public UserVotePollDto setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
        return this;
    }

//    public boolean isVoted() {
//        return isVoted;
//    }
//
//    public UserVotePollDto setVoted(boolean voted) {
//        isVoted = voted;
//        return this;
//    }
//
//    public int getVotedCount() {
//        return votedCount;
//    }
//
//    public UserVotePollDto setVotedCount(int votedCount) {
//        this.votedCount = votedCount;
//        return this;
//    }

    public int getTotalCount() {
        return totalCount;
    }

    public UserVotePollDto setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public UserVotePollDto setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
        return this;
    }

    public VoteStatus getStatusEnum() {
        return EnumUtil.getEnumByName(VoteStatus.class, status);
    }

    public VoteType getVoteTypeEnum() {
        return EnumUtil.getEnumByName(VoteType.class, voteType);
    }


}
