package cell.basic.cmn.vote;

import java.util.List;

import org.nutz.dao.Cnd;

import bap.cells.Cells;
import basic.cmn.dto.vote.UserVotePollDto;
import basic.cmn.dto.vote.VotePollDto;
import basic.cmn.dto.vote.VoteStatus;
import cell.ServiceCellIntf;
import cmn.dto.session.UserSession;
import gpf.adur.data.ResultSet;
import gpf.adur.user.User;

/**
 * 投票发布插件接口
 */
public interface IVoteService extends ServiceCellIntf {

    public static String VOTE_START_EVENT = "投票_"+ VoteStatus.已发起;
    public static String VOTE_END_EVENT = "投票_"+ VoteStatus.已结束;

    static IVoteService get(){
        return Cells.get(IVoteService.class);
    }
    /**
     * 草稿投票
     * @param user  用户
     * @param votePoll  投票信息
     * @return  投票id
     * @throws Exception
     */
    public VotePollDto draftVotePoll(User user, VotePollDto votePoll)throws Exception;
    /**
     * 更新投票
     * @param user  用户
     * @param votePoll  投票信息
     * @return  投票信息
     * @throws Exception
     */
    public VotePollDto saveVotePoll(User user,VotePollDto votePoll)throws Exception;
    /**
     * 发起投票
     * @param user  用户
     * @param votePoll  投票信息
     * @throws Exception
     */
    public void startVotePoll(User user ,VotePollDto votePoll)throws Exception;

    /**
     * 结束投票
     * @param userCode  用户
     * @param votePoll  投票信息
     * @throws Exception
     */
    public void endVotePoll(String userCode,VotePollDto votePoll)throws Exception;

    /**
     * 取消投票
     * @param userCode  用户
     * @param votePoll  投票信息
     * @throws Exception
     */
    public void cancelVotePoll(String userCode,String voteUuid)throws Exception;
    /**
     * 查询投票
     * @param uuid  投票id
     * @return  投票信息
     * @throws Exception
     */
    public VotePollDto queryVotePoll(String uuid)throws Exception;

    /**
     * 查询投票分页
     * @param cnd  查询条件
     * @param pageNo  页码
     * @param pageSize  每页数量
     * @return  投票信息列表
     * @throws Exception
     */
    public ResultSet<VotePollDto> queryVotePollPage(Cnd cnd, int pageNo, int pageSize)throws Exception;


    /**
     * 投票
     * @param userSession 用户会话
     * @param voteUuid 投票id
     * @param voteOption 投票选项
     * @param isAnonymous 是否匿名投票
     */
    public void vote(UserSession userSession, String voteUuid, String voteOption,boolean isAnonymous)throws Exception;
    /**
     * 查询用户投票分页
     * @param userCode 用户
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 投票信息列表
     * @throws Exception
     */
    public ResultSet<UserVotePollDto> queryUserVotePollPage(String userCode, Cnd cnd, int pageNo, int pageSize)throws Exception;

    /**
     * 查询用户发起的投票分页
     * @param userCode 用户
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 投票信息列表
     * @throws Exception
     */
    public ResultSet<UserVotePollDto> queryMyInitiatedVotePollPage(String userCode, Cnd cnd, int pageNo, int pageSize)throws Exception;

    public List<VotePollDto> getRunningVotePollList();
    /**
     * 查询用户的投票
     * @param userCode
     * @param voteUuid
     * @return
     * @throws Exception
     */
    public UserVotePollDto queryUserVotePoll(String userCode,String voteUuid)throws Exception;
    /**
     * 查询设备的投票
     * @param deviceId
     * @param voteUuid
     * @return
     * @throws Exception
     */
    public UserVotePollDto queryDeviceVotePoll(String deviceId,String voteUuid)throws Exception;
}
