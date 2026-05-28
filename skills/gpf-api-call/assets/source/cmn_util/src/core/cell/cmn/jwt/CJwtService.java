package cell.cmn.jwt;

import org.nutz.dao.Cnd;
import org.nutz.dao.util.cri.SqlExpressionGroup;

import com.cdao.model.CDoBasic;
import com.kwaidoo.ms.tool.ToolUtilities;

import bap.cells.BasicServiceCell;
import bap.cells.exception.ClassLoaderConflictException;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.cmn.model.IDataService;
import cmn.dto.Progress;
import cmn.dto.jwt.JwtUserInfo;
import cmn.enums.NestingTableUpdateMode;
import cmn.md.RefreshToken;
import cmn.util.JsonUtil;
import cmn.util.JwtUtil;
import cmn.util.TraceUtil;
import cmn.util.Tracer;
import web.jwt.algorithms.Algorithm;

public class CJwtService extends BasicServiceCell implements IJwtService {
	// 清理间隔（秒）- 定期检查过期项
	private long cleanupIntervalSeconds = 300L;
	Thread mainThread;

	Class<? extends CDoBasic> refreshTokenClazz = RefreshToken.class;

	@Override
	protected void doStartService() throws Exception {
		if (mainThread == null) {
			mainThread = new Thread("RefreshToken Cleanup Thread") {
				@Override
				public void run() {
					while (true) {
						try {
							cleanupExpiredRefreshTokens();
						} catch (Exception e) {
							if (e instanceof ClassLoaderConflictException) {
								break;
							}
							e.printStackTrace();
						}
						ToolUtilities.sleep(cleanupIntervalSeconds * 1000L);
					}
				}

			};
			mainThread.start();
		}
	}

	@Override
	protected void doStopService() {
		if (mainThread != null) {
			mainThread.interrupt();
		}
	}

	@Override
	public String generateJWT(JwtUserInfo userInfo, long expireMilliSec,Algorithm secret) {
		return JwtUtil.generateJWT(userInfo.getUserId(), userInfo, expireMilliSec, secret);
	}

	@Override
	public String createRefreshToken(JwtUserInfo userInfo, long expireMilliSec) throws Exception {
		String json = JsonUtil.toJson(userInfo);
		try (IDao dao = IDaoService.newIDao()) {
			// 创建刷新令牌
			CDoBasic cdo = refreshTokenClazz.newInstance();
			String refreshToken =ToolUtilities.allockUUIDWithUnderline();
			cdo.setUuid(refreshToken);
			cdo.set("code", refreshToken);
			cdo.set(RefreshToken.ExpireTime, System.currentTimeMillis()+expireMilliSec);
			cdo.set(RefreshToken.UserInfo, json);
			cdo = IDataService.get().createTransCtrl(dao, cdo);
			dao.commit();
			return cdo.getUuid();
		}
	}

	@Override
	public <T extends JwtUserInfo> T getUserInfo(String refreshToken, Class<T> clazz) throws Exception {
		try (IDao dao = IDaoService.newIDao()) {
			// 查询刷新令牌
			CDoBasic cdo = IDataService.get().queryTransCtrl(dao, refreshTokenClazz, refreshToken, false);
			if (cdo == null) {
				return null;
			}
			String userInfoJson = (String) cdo.get(RefreshToken.UserInfo);
			try {
				return JsonUtil.fromJson(userInfoJson, clazz);
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public void updateRefreshTokenExpireTime(String refreshToken, long expireMilliSec) throws Exception {
		try (IDao dao = IDaoService.newIDao()) {
			// 查询刷新令牌
			CDoBasic cdo = IDataService.get().queryTransCtrl(dao, refreshTokenClazz, refreshToken, false);
			if (cdo == null) {
				return;
			}
			// 更新刷新令牌过期时间
			cdo.set(RefreshToken.ExpireTime, System.currentTimeMillis()+expireMilliSec);
			IDataService.get().updateTransCtrl(dao, cdo, NestingTableUpdateMode.Nothing, new String[] { "expireTime" },
					null);
			dao.commit();
		}
	}
	@Override
	public void cleanupExpiredRefreshTokens() throws Exception {
		try (IDao dao = IDaoService.newIDao()) {
			// 清理过期的刷新令牌
			Tracer tracer = TraceUtil.getCurrentTracer();
			Cnd cnd = Cnd.where(new SqlExpressionGroup().andLTE(RefreshToken.ExpireTime, System.currentTimeMillis()));
			tracer.info("清理过期的刷新令牌,Cnd = " + cnd);
			Progress prog = Progress.newTracer();
			IDataService.get().deleteByCndTransCtrl(prog, dao, refreshTokenClazz, cnd, 1000);
			dao.commit();
		}
	}

	@Override
	public <T extends JwtUserInfo> T parseJWT(String jwt, Class<T> clazz) {
		return JwtUtil.decodeToken(jwt, clazz);
	}

	@Override
	public <T extends JwtUserInfo> T validateJWT(String jwt, Class<T> clazz,Algorithm secret) {
		return JwtUtil.validateLogin(jwt, secret, clazz);
	}
}
