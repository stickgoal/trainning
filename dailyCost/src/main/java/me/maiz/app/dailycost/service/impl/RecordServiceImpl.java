package me.maiz.app.dailycost.service.impl;

import me.maiz.app.dailycost.dal.RecordDAO;
import me.maiz.app.dailycost.dal.model.Record;
import me.maiz.app.dailycost.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDAO recordDAO;

    @Override
    public void save(Record record) {
        recordDAO.save(record);
    }

    @Override
    public List<Record> queryList(String begin, String end, String direction) {
        return null;
    }
}
