package com.seveniu.pojo;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by seveniu on 6/19/16.
 * ClassInfo
 */
public class ClassInfo {
    private Class clazz;

    private Type all = new Type();
    private Type update = new Type();
    private Type insert = new Type();
    private Type unique = new Type();

    private ClassInfo(Class clazz) {
        this.clazz = clazz;
    }

    public Type getUpdate() {
        return update;
    }

    public Type getInsert() {
        return insert;
    }

    public Type getAll() {
        return all;
    }

    public Type getUnique() {
        return unique;
    }

    private void build() {
        all.build();
        update.build();
        insert.build();
        unique.build();
    }


    public static ClassInfo create(Class clazz) {


        ClassInfo classInfo = new ClassInfo(clazz);
        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            classInfo.all.addField(field);
            Annotation[] annotations = field.getAnnotations();
            for (Annotation annotation : annotations) {
                field.setAccessible(true);
                if (annotation.annotationType() == InsertAndUpdate.class) {
                    classInfo.update.addField(field);
                    classInfo.insert.addField(field);
                } else if (annotation.annotationType() == Insert.class) {
                    classInfo.insert.addField(field);
                } else if (annotation.annotationType() == Unique.class) {
                    classInfo.unique.addField(field);
                }
            }
        }

        classInfo.build();
        return classInfo;
    }

    private static Field[] getAllFields(Class clazz) {

        LinkedList<Field> allField = new LinkedList<>();
        Class temp = clazz;
        while (temp != null && temp != Object.class) {
            Field[] fields = temp.getDeclaredFields();
            Collections.addAll(allField, fields);
            temp = temp.getSuperclass();
        }

        Field[] fields = new Field[allField.size()];
        return allField.toArray(fields);
    }

    public class Type {
        private HashMap<String, Field> fieldMap = new HashMap<>(); // key db column name ,value field

        private String[] columns;
        private Field[] fields;

        void addField(Field field) {
            fieldMap.put(getColumnName(field), field);
        }

        private String getColumnName(Field field) {

            Name nameAnnot = field.getAnnotation(Name.class);
            String columnName;
            if (nameAnnot != null) {
                columnName = nameAnnot.value();
                if (columnName.length() > 0) {
                    columnName = nameAnnot.value();
                }
            } else {
                columnName = camelCaseToLowerUnderscore(field.getName());
            }
            return columnName;
        }

        public String[] getColumns() {
            return columns;
        }

        public String getColumn(String fieldName) {
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getName().equals(fieldName)) {
                    return columns[i];
                }
            }
            return fieldName;
        }

        public Object[] getValues(Object obj) {
            Object[] values = new Object[columns.length];
            try {
                for (int i = 0; i < values.length; i++) {
                    Field field = fields[i];
                    values[i] = field.get(obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return values;
        }

        private void build() {
            columns = new String[fieldMap.size()];
            fields = new Field[fieldMap.size()];
            int i = 0;
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                columns[i] = entry.getKey();
                fields[i] = entry.getValue();
                i++;
            }
        }

    }

    private static String camelCaseToLowerUnderscore(String word) {
        StringBuilder result = new StringBuilder();
        if (word != null && word.length() > 0) {
            // 将第一个字符处理成大写
            result.append(Character.toLowerCase(word.charAt(0)));
            // 循环处理其余字符
            for (int i = 1; i < word.length(); i++) {
                char c = word.charAt(i);
                if ((c >= 65) && (c <= 90)) {
                    result.append("_").append(Character.toLowerCase(c));
                } else {
                    result.append(c);
                }
            }
        }
        return result.toString();
    }

}

