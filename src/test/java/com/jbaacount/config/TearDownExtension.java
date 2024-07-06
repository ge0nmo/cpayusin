package com.jbaacount.config;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class TearDownExtension implements BeforeEachCallback
{
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception
    {
        ApplicationContext context = SpringExtension.getApplicationContext(extensionContext);
        cleanDatabase(context);
    }

    private static void cleanDatabase(ApplicationContext applicationContext)
    {
        try{
            CleanDatabase.tearDown(applicationContext);
        } catch (NoSuchBeanDefinitionException e){

        }
    }
}
