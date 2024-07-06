package com.jbaacount.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;

public class TestContainerExtension implements BeforeAllCallback
{
    @Container
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest");

    /*@Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
            .withExposedPorts(6379)
            .withReuse(true);*/

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception
    {
        if (!mySQLContainer.isRunning()) {
            mySQLContainer.start();
        }

        /*if(!redisContainer.isRunning()) {
            redisContainer.start();
        }*/

        setMySqlContainer();
        //setRedisContainer();
    }

    private void setMySqlContainer()
    {
        System.setProperty("spring.datasource.url", mySQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", mySQLContainer.getUsername());
        System.setProperty("spring.datasource.password", mySQLContainer.getPassword());
    }

    /*private void setRedisContainer()
    {
        String host = redisContainer.getHost();
        Integer redisPort = redisContainer.getMappedPort(6379);

        System.setProperty("spring.redis.host", host);
        System.setProperty("spring.redis.port", redisPort.toString());

        System.out.println("redis running on = " + host + ":" + redisPort);
    }*/
}
