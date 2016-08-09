package com.seveniu.data.jdbc;

import com.seveniu.pojo.ClassInfo;
import com.seveniu.pojo.Pojo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 */
public class BaseDao<T extends Pojo> {

    protected String tableName;
    protected JdbcTemplate jdbcTemplate;
    protected RowMapper<T> rowMapper;
    private ClassInfo classInfo;

    public BaseDao(String tableName, DataSource dataSource, Class<T> tClass, RowMapper<T> rowMapper) {
        this(tableName, dataSource, tClass);
        this.rowMapper = rowMapper;
    }

    public BaseDao(String tableName, DataSource dataSource, Class<T> tClass) {
        this.tableName = tableName;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.rowMapper = new BeanPropertyRowMapper<>(tClass);
        this.classInfo = ClassInfo.create(tClass);
    }

    public int count() {
        return jdbcTemplate.queryForObject("select count(*) from " + tableName, Integer.class);
    }

    public int filterCount(String[] filterColumns, Object[] filterValues) {
        filterColumns = fieldsToColumns(filterColumns);
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) from ")
                .append(tableName);
        buildMultiWhere(sb, filterColumns);
        List<Object> both = new ArrayList<>(filterValues.length);
        Collections.addAll(both, filterValues);
        Object[] values = both.toArray();
        return jdbcTemplate.queryForObject(sb.toString(), values, Integer.class);
    }

    public T getByOther(String[] columnNames, Object[] values) {
        columnNames = fieldsToColumns(columnNames);
        String andWhereStr = generateAndWhere(columnNames);
        try {
            return jdbcTemplate.queryForObject("select * from " + tableName + " where " + andWhereStr, rowMapper, values);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public T getById(int id) {
        try {
            return jdbcTemplate.queryForObject("select * from " + tableName + " where id = ?", rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public <K> K getSpecialFieldById(int id, String field, Class<K> clazz) {
        String column = fieldToColumn(field);
        return jdbcTemplate.queryForObject("select " + column + " from " + tableName + " where id = ?", clazz, id);
    }

    public List<Map<String, Object>> getSpecialFieldMap(String[] selectFields, String[] filterFields, Object[] filterValues) {
        String[] columns = fieldsToColumns(filterFields);
        String[] selectColumns = fieldsToColumns(selectFields);
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (int i = 0; i < selectColumns.length; i++) {
            String columnName = selectColumns[i];
            sb.append(columnName).append(" as ").append(selectFields[i]).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ").append(tableName);
        buildMultiWhere(sb, columns);
        return jdbcTemplate.queryForList(sb.toString(), filterValues);
    }

    public <K> List<K> getSpecialFieldList(String selectField, String[] filterFields, Object[] filterValues, Class<K> clazz) {
        String[] columns = fieldsToColumns(filterFields);
        String selectColumn = fieldToColumn(selectField);
        StringBuilder sb = new StringBuilder();
        sb.append("select ").append(selectColumn).append(" as ").append(selectColumn)
                .append(" from ").append(tableName);
        buildMultiWhere(sb, columns);
        return jdbcTemplate.queryForList(sb.toString(), filterValues, clazz);
    }

    public Map<String, Object> getSpecialFieldsById(int id, String[] fields) {
        try {
            if (fields.length == 0) {
                return null;
            }
            String[] columnNames = fieldsToColumns(fields);
            StringBuilder sb = new StringBuilder("select ");
            for (int i = 0; i < columnNames.length; i++) {
                String columnName = columnNames[i];
                sb.append(columnName).append(" as ").append(fields[i]).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" from ").append(tableName).append(" where id = ?");
            return jdbcTemplate.queryForMap(sb.toString(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public boolean existByColumn(String[] columnNames, Object[] values) {
        columnNames = fieldsToColumns(columnNames);
        String andStr = generateAndWhere(columnNames);
        return jdbcTemplate.queryForObject("SELECT EXISTS (select * from " + tableName + " where " + andStr + ")", values, Boolean.class);
    }

    private String generateAndWhere(String[] columnNames) {
        columnNames = fieldsToColumns(columnNames);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            sb.append(" ").append(columnName).append("=").append("?");
            if (i + 1 < columnNames.length) {
                sb.append(" and ");
            }
        }
        return sb.toString();
    }

    public boolean exist(T pojo) {
        ClassInfo.Type unique = classInfo.getUnique();

        String[] column = unique.getColumns();
        Object[] values = unique.getValues(pojo);
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            boolean exist = existByColumn(column[i], value);
            if (exist) {
                return true;
            }
        }
        return false;
    }

    public boolean existByColumn(String column, Object value) {
        column = fieldToColumn(column);
        return jdbcTemplate.queryForObject("SELECT EXISTS (select * from " + tableName + " where " + column + " = ?)", new Object[]{value}, Boolean.class);
    }

    public void del(List<Integer> ids) {
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = Collections.singletonMap("ids", ids);
        namedTemplate.update("DELETE FROM " + tableName + " where id in (:ids) ", params);
    }

    public void del(int id) {
        jdbcTemplate.update("DELETE  FROM " + tableName + " where id = ?", id);
    }

    public void delByField(String field, Object fieldValue) {
        jdbcTemplate.update("DELETE  FROM " + tableName + " where " + field + " = ?", fieldValue);
    }

    public void delByFields(String[] fields, Object[] fieldValues) {
        String[] columns = fieldsToColumns(fields);
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE  FROM ").append(tableName);
        buildMultiWhere(sb, columns);
        jdbcTemplate.update(sb.toString(), fieldValues);
    }

    public List<T> all(String[] filterFields, Object[] values) {
        String[] columns = fieldsToColumns(filterFields);
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ")
                .append(tableName);
        buildMultiWhere(sb, columns);
        return jdbcTemplate.query(sb.toString(), values, rowMapper);
    }

    public List<T> all() {
        return jdbcTemplate.query("select * from " + tableName, rowMapper);
    }

    public List<T> limit(int start, int size, String column, String orderType) {
        column = fieldToColumn(column);
        return jdbcTemplate.query("SELECT * FROM " + this.tableName + "" +
                " INNER JOIN" +
                " (select * from " + tableName + "" +
                " order by " + column + " " + orderType + "" +
                " limit ?,?)" +
                " t2 USING (id)", new Object[]{start, size}, rowMapper);
    }

    public List<T> filterLimit(int start, int size, String column, String orderType, String[] filterColumns, Object[] filterValues) {
        column = fieldToColumn(column);
        filterColumns = fieldsToColumns(filterColumns);
        StringBuilder sb = new StringBuilder();
        sb.append("select * from ")
                .append(tableName)
                .append(" inner join (select id from ").append(tableName);
        buildMultiWhere(sb, filterColumns);
        sb.append(" order by ").append(column).append(" ").append(orderType)
                .append(" limit ?,?")
                .append(") as t2 using(id)");

        List<Object> both = new ArrayList<>(2 + filterValues.length);
        Collections.addAll(both, filterValues);
        both.add(start);
        both.add(size);
        Object[] values = both.toArray();
        return jdbcTemplate.query(sb.toString(), values, rowMapper);
    }

    public List<T> queryList(String sql, Object... args) {
        return jdbcTemplate.query(sql, rowMapper, args);
    }

    public List<Map<String, Object>> queryListMap(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    public void update(T pojo) {
        ClassInfo.Type update = classInfo.getUpdate();
        update(pojo.getId(), update.getColumns(), update.getValues(pojo));
    }

    public void update(int id, String[] columns, Object[] values) {
        columns = fieldsToColumns(columns);
        if (columns.length == 0 || values.length == 0 || columns.length != values.length) {
            throw new IllegalArgumentException("param error");
        }
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(tableName).append(" set ");
        for (String field : columns) {
            sb.append(field).append("=").append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" where id = ").append("?");
        Object[] data = Arrays.copyOf(values, values.length + 1);
        data[data.length - 1] = id;
        jdbcTemplate.update(sb.toString(), data);
    }

    public int insert(T pojo) {
        ClassInfo.Type insert = classInfo.getInsert();
        // 插入
        int id = (int) insert(insert.getColumns(), insert.getValues(pojo));
        pojo.setId(id);
        return id;
    }

    public long insert(String[] field, Object[] values) {
        final String[] columns = fieldsToColumns(field);
        if (field.length == 0 || values.length == 0 || field.length != values.length) {
            throw new IllegalArgumentException("param error");
        }
        GeneratedKeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                con.setAutoCommit(true);
                StringBuilder sb = new StringBuilder("INSERT INTO ");
                sb.append(tableName).append("(");
                for (String s : columns) {
                    sb.append(s).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append(") VALUES (");
                for (String aField : columns) {
                    sb.append("?").append(",");
                }
                sb.deleteCharAt(sb.length() - 1).append(")");

                PreparedStatement statement = con.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);
//                PreparedStatement statement = con.prepareStatement("INSERT INTO SOME_TABLE(NAME, VALUE) VALUES (?, ?) ", Statement.RETURN_GENERATED_KEYS);

                for (int i = 0; i < columns.length; i++) {
                    statement.setObject(i + 1, values[i]);
                }
                return statement;
            }
        }, holder);

        return holder.getKey().longValue();
    }

    private String fieldToColumn(String field) {
        ClassInfo.Type all = classInfo.getAll();
        return all.getColumn(field);
    }

    private String[] fieldsToColumns(String[] fields) {
        ClassInfo.Type all = classInfo.getAll();
        String[] columns = new String[fields.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = all.getColumn(fields[i]);
        }
        return columns;
    }

    private void buildMultiWhere(StringBuilder sb, String[] columns) {
        if (columns.length > 0) {
            sb.append(" where ");
            for (int i = 0; i < columns.length; i++) {
                String filterColumn = columns[i];

                sb.append(filterColumn).append("=").append("?");
                if (i + 1 < columns.length) {
                    sb.append(" and ");
                }
            }
            sb.append(" ");
        }
    }

    public String getTableName() {
        return tableName;
    }

}
