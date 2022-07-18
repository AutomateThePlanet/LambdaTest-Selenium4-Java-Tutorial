package models.emails;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class EmailsResponse{

	@SerializedName("result")
	private String result;

	@SerializedName("emails")
	private List<EmailsItem> emails;

	@SerializedName("offset")
	private int offset;

	@SerializedName("count")
	private int count;

	@SerializedName("limit")
	private int limit;

	@SerializedName("message")
	private Object message;

	public String getResult(){
		return result;
	}

	public List<EmailsItem> getEmails(){
		return emails;
	}

	public int getOffset(){
		return offset;
	}

	public int getCount(){
		return count;
	}

	public int getLimit(){
		return limit;
	}

	public Object getMessage(){
		return message;
	}
}