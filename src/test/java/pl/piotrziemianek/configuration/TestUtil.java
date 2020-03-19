package pl.piotrziemianek.configuration;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class TestUtil {



    public SessionFactory getTestSessionFactory() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernateTest.cfg.xml")
                .build();
        Metadata metadata = new MetadataSources(registry)
                .buildMetadata();

        return metadata.buildSessionFactory();
    }
}

