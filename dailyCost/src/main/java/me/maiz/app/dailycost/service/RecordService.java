package me.maiz.app.dailycost.service;

import me.maiz.app.dailycost.dal.model.Record;

import java.util.List;

public interface RecordService {

    void save(Record record);


    List<Record> queryList(String begin, String end, String direction);

}
