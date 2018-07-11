package me.maiz.trainning.common;

public class Result {

    private boolean success;

    private String resultCode;

    public Result() {
    }

    public Result(boolean success, String resultCode) {
        this.success = success;
        this.resultCode = resultCode;
    }

    public static Result success(){
        return new Result(true,"SUCCESS");
    }
    public static Result fail(String resultCode){
        return new Result(false,resultCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success +
                ", resultCode='" + resultCode + '\'' +
                '}';
    }
}
