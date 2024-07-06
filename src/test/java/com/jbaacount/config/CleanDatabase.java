package com.jbaacount.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

public abstract class CleanDatabase
{
    public static void tearDown(ApplicationContext context)
    {
        EntityManager em = context.getBean(EntityManager.class);
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        TransactionTemplate transactionTemplate = context.getBean(TransactionTemplate.class);

        transactionTemplate
                .execute(status -> {
                    em.clear();
                    deleteAll(jdbcTemplate);
                    return null;
                });
    }

    public static void deleteAll(JdbcTemplate jdbcTemplate)
    {
        List<String> tableNames = findTables(jdbcTemplate);
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");

        for(String table : tableNames)
        {
            jdbcTemplate.execute(String.format("TRUNCATE TABLE %s", table));
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
    }

    public static List<String> findTables(JdbcTemplate jdbcTemplate)
    {
        return jdbcTemplate.query("SHOW TABLES", (rs, rowNum) -> rs.getString(1))
                .stream()
                .toList();
    }
}
