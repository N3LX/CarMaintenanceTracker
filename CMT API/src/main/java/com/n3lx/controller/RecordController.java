package com.n3lx.controller;

import com.n3lx.model.Record;
import com.n3lx.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class RecordController {

    @Autowired
    GenericService<Record> recordService;

    @RequestMapping(value = "/records/{id}", method = RequestMethod.GET)
    public Record getRecord(@PathVariable Integer id) throws Exception {
        return recordService.get(id);
    }

    @RequestMapping(value = "/records", method = RequestMethod.GET)
    public List<Record> getAllRecords() throws Exception {
        return recordService.getAll();
    }

    @RequestMapping(value = "/records", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void saveRecord(@RequestBody Record record) throws Exception {
        recordService.save(record);
    }

    @RequestMapping(value = "/records", method = RequestMethod.PUT)
    public void updateRecord(@RequestBody Record record) throws Exception {
        recordService.update(record);
    }

    @RequestMapping(value = "/records", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@RequestBody Record record) throws Exception {
        recordService.delete(record);
    }

}
