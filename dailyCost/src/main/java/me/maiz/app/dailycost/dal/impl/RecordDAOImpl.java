package me.maiz.app.dailycost.dal.impl;

import me.maiz.app.dailycost.dal.RecordDAO;
import me.maiz.app.dailycost.dal.model.Record;
import org.springframework.stereotype.Repository;

@Repository("recordDAO")
public class RecordDAOImpl extends BaseDAO<Record> implements RecordDAO {
    @Override
    public void save(Record record) {
        getSession().save(record);
    }
}
