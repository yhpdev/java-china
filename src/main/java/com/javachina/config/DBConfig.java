package com.javachina.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.blade.annotation.Order;
import com.blade.config.BaseConfig;
import com.blade.config.Configuration;
import com.blade.ioc.annotation.Component;
import com.blade.jdbc.ActiveRecord;
import com.blade.jdbc.ar.SampleActiveRecord;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by biezhi on 2016/12/25.
 */
@Component
@Order(sort = 1)
public class DBConfig implements BaseConfig {

    public static ActiveRecord activeRecord;

    @Override
    public void config(Configuration configuration) {
        try {
            InputStream in = DBConfig.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(in);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
            activeRecord = new SampleActiveRecord(dataSource);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
