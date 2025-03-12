package com.dst.abacustrainner.Model;

public class TableRowModel {
    private String date;
    private String time;
    private String status;

    public TableRowModel(String date, String time, String status) {
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
}
