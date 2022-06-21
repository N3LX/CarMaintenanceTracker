package com.n3lx.service;

import com.n3lx.dao.GenericDao;
import com.n3lx.exception.CustomSQLException;
import com.n3lx.exception.InputValidationException;
import com.n3lx.model.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordService implements GenericService<Record> {

    @Autowired
    GenericDao<Record> recordDao;

    @Override
    public Record get(int id) throws CustomSQLException, InputValidationException {
        //Input validation
        if (id <= 0) {
            throw new InputValidationException("\"id\" parameter has to be greater than 0.");
        }

        //Process input
        return recordDao.get(id);
    }

    @Override
    public List<Record> getAll() throws CustomSQLException {
        //Process input
        return recordDao.getAll();
    }

    @Override
    public void save(Record record) throws CustomSQLException, InputValidationException {
        //Input validation
        if (record == null) {
            throw new InputValidationException("The record is null or it was not provided properly.");
        }
        if (record.getCreationDate() == null ||
                record.getShortDescription() == null ||
                record.getLongDescription() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (record.getCreationDate().isAfter(LocalDate.now())) {
            throw new InputValidationException("\"creation_date\" parameter cannot be a future date.");
        }
        if (record.getMileage() < 0) {
            throw new InputValidationException("\"mileage\" parameter cannot be negative.");
        }
        if (record.getShortDescription().length() > 32) {
            throw new InputValidationException("\"short_description\" parameter cannot exceed 32 characters.");
        }
        if (record.getLongDescription().length() > 4096) {
            throw new InputValidationException("\"long_description\" parameter cannot exceed 4096 characters.");
        }

        //Process input
        recordDao.save(record);
    }

    @Override
    public void update(Record record) throws CustomSQLException, InputValidationException {
        //Input validation
        if (record == null) {
            throw new InputValidationException("The record is null or it was not provided properly.");
        }
        if (record.getCreationDate() == null ||
                record.getShortDescription() == null ||
                record.getLongDescription() == null) {
            throw new InputValidationException("One of parameters is null.");
        }
        if (record.getCreationDate().isAfter(LocalDate.now())) {
            throw new InputValidationException("\"creation_date\" parameter cannot be a future date.");
        }
        if (record.getMileage() < 0) {
            throw new InputValidationException("\"mileage\" parameter cannot be negative.");
        }
        if (record.getShortDescription().length() > 32) {
            throw new InputValidationException("\"short_description\" parameter cannot exceed 32 characters.");
        }
        if (record.getLongDescription().length() > 4096) {
            throw new InputValidationException("\"long_description\" parameter cannot exceed 4096 characters.");
        }

        //Process input
        recordDao.update(record);
    }

    @Override
    public void delete(Record record) throws CustomSQLException, InputValidationException {
        //Input validation
        if (record == null) {
            throw new InputValidationException("The record is null or it was not provided properly.");
        }
        if (record.getCreationDate() == null ||
                record.getShortDescription() == null ||
                record.getLongDescription() == null) {
            throw new InputValidationException("One of parameters is null.");
        }

        //Process input
        recordDao.delete(record);
    }

}
