package cn.edu.xmu.ultraci.hotelcheckin.client.dto;

import java.io.Serializable;

public class SmsDTO implements Serializable {
	private static final long serialVersionUID = -7159640640584137045L;

	private SmsReq smsReq;
	private SmsResp smsResp;

	public SmsReq getSmsReq() {
		return smsReq;
	}

	public void setSmsReq(SmsReq smsReq) {
		this.smsReq = smsReq;
	}

	public SmsResp getSmsResp() {
		return smsResp;
	}

	public void setSmsResp(SmsResp smsResp) {
		this.smsResp = smsResp;
	}

	public class SmsReq {
		private String appId;
		private String templateId;
		private String to;
		private String param;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getParam() {
			return param;
		}

		public void setParam(String param) {
			this.param = param;
		}
	}

	public class SmsResp {
		private String respCode;
		private String failure;
		private String smsId;
		private String createDate;

		public String getRespCode() {
			return respCode;
		}

		public void setRespCode(String respCode) {
			this.respCode = respCode;
		}

		public String getFailure() {
			return failure;
		}

		public void setFailure(String failure) {
			this.failure = failure;
		}

		public String getSmsId() {
			return smsId;
		}

		public void setSmsId(String smsId) {
			this.smsId = smsId;
		}

		public String getCreateDate() {
			return createDate;
		}

		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
	}
}
