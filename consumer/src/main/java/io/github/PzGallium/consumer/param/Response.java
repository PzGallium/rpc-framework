package io.github.PzGallium.consumer.param;

public class Response {
	private Long id;
	private Object result; 		   //
	private String code = "00000"; //00000 means success
	private String msg; 		   //the reason why it does not success
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	
}
