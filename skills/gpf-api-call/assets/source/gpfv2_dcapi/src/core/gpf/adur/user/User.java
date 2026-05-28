package gpf.adur.user;

import gpf.adur.data.Form;
import gpf.md.user.BasicUser;

/**
 * 用户
 * @author chenxb
 *
 */
public class User extends Form{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7727455409760552991L;
	public final static String Code = "code";
	public final static String UserName = "userName";
	public final static String Alias = "alias";
	public final static String FullName = Alias;
	public final static String Password = "passwd";
//	public final static String Locked = "locked";
	public final static String Email = "mail";
	public final static String Phone = "phone";
	public final static String ProfilePhoto = "profilePhoto";
	public final static String Status = "status";
	public final static String Gender = "gender";
	
	/**
	 * token有效时间，单位(分钟)
	 */
	public final static String TokenExpireTime = BasicUser.TokenExpireTime;
	
	public final static String[] BasicFields = new String[] {UUID,Code,UserName,FullName,Password,Email,Phone,Status,Gender,TokenExpireTime};
	
	public User() {
		super();
		getData().put(Status, UserStatus.UnLocked.name());
	}
	
	public User(String formModelID) {
		super(formModelID);
		getData().put(Status, UserStatus.UnLocked.name());
	}
	
	public User(String formModelID,String uuid) {
		super(formModelID,uuid);
		getData().put(Status, UserStatus.UnLocked.name());
	}

	public String getCode() throws Exception {
		return getStringByCode(Code);
	}
	
	public User setCode(String code) {
		setAttrValueByCode(Code, code);
		return this;
	}
	
	public String getUserName() throws Exception {
		return getStringByCode(UserName);
	}
	
	public User setUserName(String userName) {
		setAttrValueByCode(UserName, userName);
		return this;
	}
	
	public String getFullName() throws Exception {
		return getStringByCode(FullName);
	}
	
	public User setFullName(String fullName) {
		setAttrValueByCode(FullName, fullName);
		return this;
	}
	
	public gpf.adur.data.Password getPassword() throws Exception {
		return getPasswordByCode(Password);
	}
	
	public User setPassword(gpf.adur.data.Password password) {
		setAttrValueByCode(Password, password);
		return this;
	}
	
	public String getStatus() throws Exception {
		return getStringByCode(Status);
	}
	
	public UserStatus getStatusEnum() throws Exception {
		String status = getStatus();
		return UserStatus.formValue(status);
	}
	
	public User setStatus(String status) {
		setAttrValueByCode(Status, status);
		return this;
	}
	
	public User setStatus(UserStatus status) {
		setAttrValueByCode(Status, status.name());
		return this;
	}
	
	public String getGender() throws Exception {
		return getStringByCode(Gender);
	}
	
	public User setGender(String gender) {
		setAttrValueByCode(Gender, gender);
		return this;
	}
	
	public User setGender(UserGender gender) {
		setAttrValueByCode(Gender, gender.name());
		return this;
	} 
	
	
	public boolean isLocked() throws Exception {
		return getStatusEnum() == UserStatus.Locked;
	}
	
	public String getEmail() throws Exception {
		return getStringByCode(Email);
	}
	
	public User setEmail(String email) {
		setAttrValueByCode(Email, email);
		return this;
	}
	
	public String getPhone() throws Exception {
		return getStringByCode(Phone);
	}
	
	public User setPhone(String phone) {
		setAttrValueByCode(Phone, phone);
		return this;
	}
	
	public byte[] getProfilePhoto() throws Exception {
		return getByteArrayByCode(ProfilePhoto);
	}
	public User setProfilePhoto(byte[] profilePhoto) {
		setAttrValueByCode(ProfilePhoto, profilePhoto);
		return this;
	}
	
	public Long getTokenExpireTime() throws Exception {
		return getLongByCode(TokenExpireTime);
	}
	public User setTokenExpireTime(Long tokenExpireTime) {
		setAttrValueByCode(TokenExpireTime, tokenExpireTime);
		return this;
	}
}
