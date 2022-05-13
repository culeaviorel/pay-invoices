package org.fasttrackit.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParameterTypes {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer(replaceWithEmptyString = "[blank]")
    @DefaultDataTableCellTransformer(replaceWithEmptyString = "[blank]")
    public Object transformer(Object fromValue, Type toValueType) {
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return objectMapper.convertValue(fromValue, objectMapper.constructType(toValueType));
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public String listOfStringListsType(String cell) {
        return cell;
    }

    @ParameterType(name = "list", value = "\"[\\d_,;:.{}+=~#$%`\\\\&\\-\\pL\\pM'!()<>\\s™®©?@^\\p{Sc}\\/؟]+\"")
    public List<String> list(String strings) {
        String s = CucumberTypeUtils.removeDoubleQuotes(strings);
        String first = s.substring(0,1);
        String splitChar = ",";
        if(first.equals(";")){
            splitChar = ";";
            s = s.substring(1);
        }
        return Arrays.stream(s.split(splitChar)).map(String::trim).collect(Collectors.toList());
    }

    @ParameterType(name = "intList", value = "\"[\\d(,)]+\"")
    public List<Integer> intList(String strings) {
        return Arrays.stream(CucumberTypeUtils.removeDoubleQuotes(strings).split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    @ParameterType(name = "date", value = "((yesterday|today|tomorrow|nextWeek|nextMonth)|(.*))")
    public String date(String string) {
        return CucumberTypeUtils.transformToDate(string);
    }

    @ParameterType(name = "stringLong", value = "((s_\\d)|(.*))")
    public String stringLong(String string) {
        return CucumberTypeUtils.generateLongString(string);
    }

    @ParameterType(name = "specialCharacter", value = "(.*)")
    public String specialCharacter(String string) {
        return CucumberTypeUtils.replaceSpecialChar(string);
    }

    @ParameterType(value = "(true|false)")
    public Boolean bool(String string) {
        return (Boolean) transformer(string, Boolean.class);
    }
//    @ParameterType("red|blue|yellow")  // regexp
//    public Color color(String color){  // type, name (from method)
//        return new Color(color);       // transformer function
//    }

//    @ParameterType(name = "variable", value = "(.*)")
//    public String variable(String variable) {
//        variable = CucumberTypeUtils.removeDoubleQuotes(variable);
//        return TestStorage.replaceVariable(variable);
//    }
}
