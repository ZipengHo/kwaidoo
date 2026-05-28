package cell.cmn.jwt;

import bap.cells.Cells;
import cell.ServiceCellIntf;
import cmn.dto.jwt.JwtUserInfo;
import web.jwt.algorithms.Algorithm;

public interface IJwtService extends ServiceCellIntf {

	static IJwtService get() {
		return Cells.get(IJwtService.class);
	}

	/**
	 * 生成JWT
	 * 
	 * @param userInfo
	 * @param expireMilliSec
	 * @return
	 */
	public String generateJWT(JwtUserInfo userInfo, long expireMilliSec,Algorithm secret);

	/**
	 * 创建刷新令牌
	 * 
	 * @param userInfo
	 * @param expireMilliSec
	 * @return
	 */
	public String createRefreshToken(JwtUserInfo userInfo, long expireMilliSec) throws Exception;

	public <T extends JwtUserInfo> T getUserInfo(String refreshToken, Class<T> clazz) throws Exception;

	/**
	 * 更新刷新令牌过期时间
	 * 
	 * @param refreshToken
	 * @param expireMilliSec
	 */
	public void updateRefreshTokenExpireTime(String refreshToken, long expireMilliSec) throws Exception;

	/**
	 * 清理过期的令牌
	 * @throws Exception
	 */
	public void cleanupExpiredRefreshTokens() throws Exception;
	/**
	 * 解析JWT
	 * 
	 * @param jwt
	 * @param clazz
	 * @return
	 */
	<T extends JwtUserInfo> T parseJWT(String jwt, Class<T> clazz);

	/**
	 * 验证JWT
	 * 
	 * @param jwt
	 * @param secret
	 * @param clazz
	 * @return
	 */
	<T extends JwtUserInfo> T validateJWT(String jwt, Class<T> clazz,Algorithm secret);

}
