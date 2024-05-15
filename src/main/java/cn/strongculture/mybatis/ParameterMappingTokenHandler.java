package cn.strongculture.mybatis;

import java.util.ArrayList;
import java.util.List;

public class ParameterMappingTokenHandler  implements TokenHandler {

    private List<ParameterMapping> parameterMappings = new ArrayList();

    public String handleToken(String content) {
        this.parameterMappings.add(new ParameterMapping(content));
        return "?";
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

}
