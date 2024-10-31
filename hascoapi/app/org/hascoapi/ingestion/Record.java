package org.hascoapi.ingestion;

public interface Record {

    public String getValueByColumnName(String colomnName);

    public String getValueByColumnIndex(int index);

    public int size();
}