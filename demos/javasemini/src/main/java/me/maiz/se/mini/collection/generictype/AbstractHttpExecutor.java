package me.maiz.se.mini.collection.generictype;

public abstract class AbstractHttpExecutor<Req,Resp> {

    public abstract void process(Req request,Resp response);

}
