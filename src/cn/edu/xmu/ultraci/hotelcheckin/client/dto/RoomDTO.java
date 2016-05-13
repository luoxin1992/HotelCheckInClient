package cn.edu.xmu.ultraci.hotelcheckin.client.dto;

import java.io.Serializable;

public class RoomDTO extends BaseDTO implements Serializable {
	private static final long serialVersionUID = 6044279825051508518L;

	private Integer id;
	private String name;
	private String type;
	private Double price;
	private String mobile;
	private String checkin;
	private String checkout;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCheckin() {
		return checkin;
	}

	public void setCheckin(String checkin) {
		this.checkin = checkin;
	}

	public String getCheckout() {
		return checkout;
	}

	public void setCheckout(String checkout) {
		this.checkout = checkout;
	}

}
