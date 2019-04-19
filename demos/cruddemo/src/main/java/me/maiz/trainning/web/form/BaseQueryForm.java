package me.maiz.trainning.web.form;

public class BaseQueryForm extends BaseForm {

    private int start=1;

    private int pageSize = 10;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
