package com.databuck.bean;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

public class ParseLocalDate extends CellProcessorAdaptor {

    public ParseLocalDate() {
        super();
    }

    public ParseLocalDate(CellProcessor next) {
        super(next);
    }

    @Override
    public Object execute(Object value, CsvContext context) {
        validateInputNotNull(value, context);    
        String dd=" "+value;
        return dd;
    }
}
