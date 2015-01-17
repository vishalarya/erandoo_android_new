package erandoo.app.config;

public class DeviceInfo {
	public static final String FLD_DEVICE_ID = "device_id";
	public static final String FLD_SIM_OPERATOR = "sim_operator";
	public static final String FLD_SIM_COUNTRY_ISO = "sim_country_iso";
	public static final String FLD_SIM_OPERATOR_NAME = "sim_operator_nm";
	public static final String FLD_SIM_SERIAL_NUMBER = "sim_serial_number";
	public static final String FLD_SIM_NETWORK_OPERATOR = "sim_network_operator";
	public static final String FLD_DEVICE_MAC_ADDRESS = "device_mac_addr";
	public static final String FLD_EMEI_MEID_ESN = "emei_meid_esn";
	public static final String FLD_DEVICE_MODEL = "device_model";
	public static final String FLD_APP_TOKEN = "app_token";//Server key
	public static final String FLD_APP_VERSION = "app_ver";
	public static final String FLD_DEVICE_DENSITY = "device_density";
	public static final String FLD_DEVICE_RESOLUTION = "device_resolution";
	public static final String FLD_OS_TYPE = "os_type";
	public static final String FLD_OS_VERSION = "os_ver";
	public static final String FLD_OS_BUILD_NUMBER = "os_build_num";
	public static final String FLD_CURRENT_APP_CLIENT_VERSION = "current_app_client_ver";
	public static final String FLD_CLOUD_KEY_TO_NOTIFY = "cloud_key_to_notify";

	private String deviceId;
	private String simOperator;
	private String simCountryIso;
	private String simOperatorName;
	private String simSerialNumber;
	private String simNetworkOperator;
	private String deviceMacAddress;
	private String emeiMeidEsn;
	private String deviceModel;
	private String appToken;
	private String appVersion;
	private String deviceDensity;
	private String deviceResolution;
	private String osType;
	private String osVersion;
	private String osBuildNumber;
	private Integer currAppClientVersion;
	private String cloudKeyToNotify;
	
	public DeviceInfo(){
		
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getSimOperator() {
		return simOperator;
	}
	public void setSimOperator(String simOperator) {
		this.simOperator = simOperator;
	}
	public String getSimCountryIso() {
		return simCountryIso;
	}
	public void setSimCountryIso(String simCountryIso) {
		this.simCountryIso = simCountryIso;
	}
	public String getSimOperatorName() {
		return simOperatorName;
	}
	public void setSimOperatorName(String simOperatorName) {
		this.simOperatorName = simOperatorName;
	}
	public String getSimSerialNumber() {
		return simSerialNumber;
	}
	public void setSimSerialNumber(String simSerialNumber) {
		this.simSerialNumber = simSerialNumber;
	}
	public String getSimNetworkOperator() {
		return simNetworkOperator;
	}
	public void setSimNetworkOperator(String simNetworkOperator) {
		this.simNetworkOperator = simNetworkOperator;
	}
	public String getDeviceMacAddress() {
		return deviceMacAddress;
	}
	public void setDeviceMacAddress(String deviceMacAddress) {
		this.deviceMacAddress = deviceMacAddress;
	}
	public String getEmeiMeidEsn() {
		return emeiMeidEsn;
	}
	public void setEmeiMeidEsn(String emeiMeidEsn) {
		this.emeiMeidEsn = emeiMeidEsn;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public String getAppToken() {
		return appToken;
	}
	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getDeviceDensity() {
		return deviceDensity;
	}
	public void setDeviceDensity(String deviceDensity) {
		this.deviceDensity = deviceDensity;
	}
	public String getDeviceResolution() {
		return deviceResolution;
	}
	public void setDeviceResolution(String deviceResolution) {
		this.deviceResolution = deviceResolution;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	
	public String getOsBuildNumber() {
		return osBuildNumber;
	}
	public void setOsBuildNumber(String osBuildNumber) {
		this.osBuildNumber = osBuildNumber;
	}
	public Integer getCurrAppClientVersion() {
		return currAppClientVersion;
	}
	public void setCurrAppClientVersion(Integer currAppClientVersion) {
		this.currAppClientVersion = currAppClientVersion;
	}
	
	public String getCloudKeyToNotify() {
		return cloudKeyToNotify;
	}
	public void setCloudKeyToNotify(String cloudKeyToNotify) {
		this.cloudKeyToNotify = cloudKeyToNotify;
	}
}
