package com.chinasofti.framework;

import com.chinasofti.framework.dao.entity.Book;
import com.chinasofti.framework.dao.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Date;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SessionFactory sessionFactory = null;
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        // configures settings from hibernate.cfg.xml
        sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        Session session = sessionFactory.openSession();
        insert(session);

        session.close();


    }
    public static void insert(Session session) {
        session.getTransaction().begin();
        User user = new User();
        user.setAge(12);
        user.setBirthday(new Date());
        user.setUserId("201608071201821");

        Book b = new Book();
        b.setBookName("海底两万里");
        b.setIsbn("xxxx-xxx-xx");
        b.setPubTime(new Date());
               session.save(user);
        session.save(b);
        session.getTransaction().commit();
    }
}
