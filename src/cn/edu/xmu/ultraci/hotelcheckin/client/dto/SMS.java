package cn.edu.xmu.ultraci.hotelcheckin.client.dto;

import java.io.Serializable;

public class SMS implements Serializable {
	private static final long serialVersionUID = -7159640640584137045L;

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
