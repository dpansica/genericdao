package it.solutionsexmachina.genericdao.jpa;

public enum DBMSPersistenceUnit {

    DBMS1("dbms1"),
    DBMS2("dbms2"),
    DBMS3("dbms3");

    private String unitName;

    DBMSPersistenceUnit(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitName() {
        return unitName;
    }
}
