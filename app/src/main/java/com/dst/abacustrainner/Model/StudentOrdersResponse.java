package com.dst.abacustrainner.Model;

import java.util.List;

public class StudentOrdersResponse {

    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result {
        private List<WorksheetOrder> worksheetOrders;

        public List<WorksheetOrder> getWorksheetOrders() {
            return worksheetOrders;
        }
    }
}
