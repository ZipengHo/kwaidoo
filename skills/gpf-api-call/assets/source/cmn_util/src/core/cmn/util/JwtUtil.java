package cmn.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.kwaidoo.ms.tool.ToolUtilities;
import com.leavay.common.util.GsonUtil;
import com.leavay.ms.tool.CmnUtil;

import cell.cmn.IJson;
import cell.cmn.IJsonService;
import cmn.dto.jwt.JwtUserInfo;
import web.jwt.JWT;
import web.jwt.JWTCreator;
import web.jwt.algorithms.Algorithm;
import web.jwt.exceptions.TokenExpiredException;
import web.jwt.interfaces.Claim;
import web.jwt.interfaces.DecodedJWT;
import web.servlet.handler.SecretConstant;

public class JwtUtil {
	
	public final static String LOG = JwtUtil.class.getSimpleName();
	
	/**
     * 生成JWT字符串
     * 格式：A.B.C
     * A-header头信息
     * B-payload 有效负荷
     * C-signature 签名信息 是将header和payload进行加密生成的
     * @param userId - 用户编号
     * @param userName - 用户名
     * @param identities - 客户端信息（变长参数），目前包含浏览器信息，用于客户端拦截器校验，防止跨域非法访问
     * @Data: 2018/7/28 19:26
     * @Modified By:
     */
    public static <T extends JwtUserInfo> String generateJWT(String userId, T userInfo,long expireSec,Algorithm algorithm) {
        //签名算法，选择SHA-256
//    	String secret = "secret";
    	if(algorithm == null)
    		algorithm = SecretConstant.Algorithm_HMAC256;
        //获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        Date now = new Date(nowTimeMillis);
        //将BASE64SECRET常量字符串使用base64解码成字节数组
//        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecretConstant.BASE64SECRET);
        //使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
//        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>();
        /*
            Header
            {
              "alg": "HS256",
              "typ": "JWT"
            }
         */
        headMap.put("alg", algorithm.getName());
        headMap.put("typ", "JWT");
      //加密后的客户编号
//        userId = encodeToBase64(userId);
//        userInfo.put("userId", userId);
//        claim("userId", AESSecretUtil.encryptToStr(userId, SecretConstant.DATAKEY))
        Map<String,Object> userInfoMap = GsonUtil.convertObject2DstType(GsonUtil.getGson(), userInfo, Map.class);
        JWTCreator.Builder builder =
        		JWT.create().withHeader(headMap)
             /*
                Payload
                {
                  "userInfo" ：{
                  	"userId": "1234567890",
                  	"userName": "John Doe",
                  }
                }
             */
        		.withClaim("userInfo", userInfoMap);

                
        //添加Token过期时间
        long expMillis = nowTimeMillis;
        if (expireSec <= 0) {
            expMillis = expMillis + SecretConstant.EXPIRESSECOND;
        }else {
        	expMillis = expMillis + expireSec;
        }
        Date expDate = new Date(expMillis);
//        ToolUtilities.info("JWT", "token开始时间：" + now + ",token 失效时间：" + expDate + ",过期毫秒="+expireSec);
        builder.withExpiresAt(expDate).withNotBefore(now);
        String token = builder.sign(algorithm);
//        ToolUtilities.info("JWT", "token="+token);
        return token;
    }
	public static <T extends JwtUserInfo> T validateLogin(String jsonWebToken,Algorithm algorithm,Class<T> jwtUserClazz) {
		T retUserInfo = null;
        JWT jwt = new JWT();
        if(algorithm == null)
        	algorithm = SecretConstant.Algorithm_HMAC256;
        DecodedJWT decodeJwt = null;
        Claim claims = null;
        try {
        	decodeJwt = jwt.require(algorithm).build().verify(jsonWebToken);
        }catch (TokenExpiredException e) {
        	decodeJwt = jwt.require(algorithm).build().parser(jsonWebToken);
        	Long expriesAt = decodeJwt.getExpiresAt();
        	Long notBefore = decodeJwt.getNotBefore();
        	if(expriesAt != null && notBefore!= null && expriesAt > notBefore) {
	            claims = decodeJwt.getClaim("userInfo");
	            if(claims != null) {
		            T userInfo = claims.as(jwtUserClazz);
		        	long expireSec = userInfo.getExpireMilliSec();
		        	if(System.currentTimeMillis() - expriesAt > expireSec) {
		        		throw e;
		        	}
	            }else {
	            	throw e;
	            }
        	}else {
            	throw e;
            }
		}catch (Exception e) {
			CmnUtil.err("Token verify failed : " + jsonWebToken);
			e.printStackTrace();
			throw e;
		}
        claims = decodeJwt.getClaim("userInfo");
        Long expriesAt = decodeJwt.getExpiresAt();
        Long notBefore = decodeJwt.getNotBefore();
//        int expireSec = (int)(expriesAt.getTime() - notBefore.getTime());
        if (claims != null) {
        	T userInfo = claims.as(jwtUserClazz);
        	long expireSec = (long)userInfo.getExpireMilliSec();
            //解密客户编号
        	String decryptUserId = (String) userInfo.getUserId();
//        	decryptUserId = decodeFromBase64(decryptUserId);
//            retUserInfo = new UserInfo();
//            //加密后的客户编号
//            retUserInfo.setUserId(decryptUserId);
//            //設置
//            retUserInfo.setClientIp(userInfo.getClientIp());
//            //客户名称
//            retUserInfo.setUserName(userInfo.getUserName());
//            //客户端浏览器信息
//            retUserInfo.setUserAgent(userInfo.getUserAgent());
            //刷新JWT
        	retUserInfo = userInfo;	
//        	String refreshToken = generateJWT(decryptUserId, userInfo,expireSec,algorithm);
//        	retUserInfo.setFreshToken(refreshToken);
        }else {
        	ToolUtilities.warning(LOG, "JWT解析出claims为空");
        }
        return retUserInfo!=null?retUserInfo:null;
	}
	
	public static <T extends JwtUserInfo> T decodeToken(String jsonWebToken,Class<T> jwtUserClazz) {
		DecodedJWT decodeJwt = null;
        try {
        	decodeJwt = JWT.decode(jsonWebToken);
        	Claim claims = decodeJwt.getClaim("userInfo");
        	if (claims != null) {
            	T userInfo = claims.as(jwtUserClazz);
            	return userInfo;
            }
        }catch (Exception e) {
        	CmnUtil.err("Token verify failed : " + jsonWebToken);
			e.printStackTrace();
			throw e;
		}
        return null;
	}
	
	/**
     * 将token失效时间设置为过期
     * @param userId
     * @param userInfo
     * @return
     */
    public static <T extends JwtUserInfo> String expireToken(String userId, T userInfo,Algorithm algorithm) {
    	//签名算法，选择SHA-256
//    	String secret = "secret";
    	if(algorithm == null)
        	algorithm = SecretConstant.Algorithm_HMAC256;
    	Map<String,Object> userInfoMap = null;
    	try(IJson json = IJsonService.get().getJson()){
    		userInfoMap = json.forceCast(Map.class, userInfo);
    	}
        //获取当前系统时间
        long nowTimeMillis = System.currentTimeMillis();
        Date now = new Date(nowTimeMillis);
        //将BASE64SECRET常量字符串使用base64解码成字节数组
//        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SecretConstant.BASE64SECRET);
        //使用HmacSHA256签名算法生成一个HS256的签名秘钥Key
//        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //添加构成JWT的参数
        Map<String, Object> headMap = new HashMap<>();
        /*
            Header
            {
              "alg": "HS256",
              "typ": "JWT"
            }
         */
        headMap.put("alg", algorithm.getName());
        headMap.put("typ", "JWT");
      //加密后的客户编号
//        userId = encodeToBase64(userId);
//        userInfo.put("userId", userId);
//        claim("userId", AESSecretUtil.encryptToStr(userId, SecretConstant.DATAKEY))
        JWTCreator.Builder builder =
        		JWT.create().withHeader(headMap)
             /*
                Payload
                {
                  "userInfo" ：{
                  	"userId": "1234567890",
                  	"userName": "John Doe",
                  }
                }
             */
        		.withClaim("userInfo", userInfoMap);

                
        //添加Token过期时间
        long expMillis = nowTimeMillis - 1000;
        Date expDate = new Date(expMillis);
//        ToolUtilities.info("JWT", "token开始时间：" + now + ",token 失效时间：" + expDate + ",过期毫秒="+expireSec);
        builder.withExpiresAt(expDate).withNotBefore(now);
        String token = builder.sign(algorithm);
//        ToolUtilities.info("JWT", "token="+token);
        return token;
    }
}
