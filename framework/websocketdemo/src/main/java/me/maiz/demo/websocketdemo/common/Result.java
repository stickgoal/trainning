package me.maiz.demo.websocketdemo.common;



import java.util.List;
import java.util.Set;

/**
 * 
 * <p>
 * 结果集对象
 * </p>
 * @author liwei
 * @param <T> 响应数据对象类型
 */
public class Result<T> {
	
	/*
	 * 响应编码
	 */
	private Integer code;
	
	/*
	 * 响应消息
	 */
	private String msg;
	
	/*
	 * 响应数据
	 */
	private T data;
	
	public static Result<?> getInstance() {
		Result<?> re = new Result<>();
		re.setCode(200);
		re.setMsg("处理成功");
		return re;
	}


	public static <S> Result<S> getInstance(Class<S> type) {
		Result<S> re = new Result<S>();
		re.setCode(200);
		re.setMsg("处理成功");
		return re;
	}


	public static <S> Result<Set<S>> getInstanceSet(Class<S> type) {
		Result<Set<S>> re = new Result<Set<S>>();
		re.setCode(200);
		re.setMsg("处理成功");
		return re;
	}

	public boolean isSuccess(){
		return code==200;
	}

	public Integer getCode() {
		return code;
	}

	public Result<T> setCode(Integer code) {
		this.code = code;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public Result<T> setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public T getData() {
		return data;
	}

	public Result<T> setData(T data) {
		this.data = data;
		return this;
	}

	@Override
	public String toString() {
		return "Result{" +
				"code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
