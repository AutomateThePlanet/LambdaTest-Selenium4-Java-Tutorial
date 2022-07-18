package models.emails;

import com.google.gson.annotations.SerializedName;

public class ToParsedItem{

	@SerializedName("address")
	private String address;

	@SerializedName("name")
	private String name;

	public String getAddress(){
		return address;
	}

	public String getName(){
		return name;
	}
}