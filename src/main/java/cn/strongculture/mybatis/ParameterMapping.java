package cn.strongculture.mybatis;

public class ParameterMapping {

    private String property;  //#{name}


    public ParameterMapping(String property) {
        this.property = property;
    }


    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
