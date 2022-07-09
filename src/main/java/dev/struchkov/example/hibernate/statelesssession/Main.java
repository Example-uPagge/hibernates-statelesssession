package dev.struchkov.example.hibernate.statelesssession;

import dev.struchkov.example.hibernate.statelesssession.domain.Post;
import dev.struchkov.example.hibernate.statelesssession.domain.PostComment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final SessionFactory sessionFactory = getSessionFactory();
        firstExample(sessionFactory);
//        secondExample(sessionFactory);
//        thirdExample(sessionFactory);
//        thirdExampleWithSession(sessionFactory);
    }

    private static void firstExample(SessionFactory sessionFactory) {
        final StatelessSession statelessSession = sessionFactory.openStatelessSession();

        statelessSession.getTransaction().begin();

        final Post post = new Post();
        post.setTitle("New Past");

        statelessSession.insert(post);

        post.setTitle("New Post");

        statelessSession.update(post);

        statelessSession.getTransaction().commit();
    }

    private static void secondExample(SessionFactory sessionFactory) {
        final StatelessSession statelessSession = sessionFactory.openStatelessSession();

        statelessSession.getTransaction().begin();

        final Post post = statelessSession.createQuery("""
                        SELECT p
                        FROM Post p
                            JOIN FETCH p.comments
                        WHERE p.id=:id""", Post.class)
                .setParameter("id", 1L)
                .getSingleResult();

        log.info(post.getId() + " " + post.getTitle());
        log.info("Comments size: " + post.getComments().size());

        statelessSession.getTransaction().commit();
    }

    private static void thirdExample(SessionFactory sessionFactory) {
        final StatelessSession statelessSession = sessionFactory.openStatelessSession();

        statelessSession.getTransaction().begin();

        final Post post1 = statelessSession.createQuery("""
                        SELECT p
                        FROM Post p
                            JOIN FETCH p.comments
                        WHERE p.id=:id""", Post.class)
                .setParameter("id", 1L)
                .getSingleResult();

        final Post post2 = statelessSession.createQuery("""
                        SELECT p
                        FROM Post p
                            JOIN FETCH p.comments
                        WHERE p.id=:id""", Post.class)
                .setParameter("id", 1L)
                .getSingleResult();

        System.out.println();

        statelessSession.getTransaction().commit();
    }

    private static void thirdExampleWithSession(SessionFactory sessionFactory) {
        final Session session = sessionFactory.openSession();

        session.getTransaction().begin();

        final Post post1 = session.createQuery("""
                        SELECT p
                        FROM Post p
                            JOIN FETCH p.comments
                        WHERE p.id=:id""", Post.class)
                .setParameter("id", 1L)
                .getSingleResult();

        final Post post2 = session.createQuery("""
                        SELECT p
                        FROM Post p
                            JOIN FETCH p.comments
                        WHERE p.id=:id""", Post.class)
                .setParameter("id", 1L)
                .getSingleResult();

        System.out.println();

        session.getTransaction().commit();
        session.close();
    }

    public static SessionFactory getSessionFactory() {
        final Properties settings = new Properties();
        settings.put(AvailableSettings.DRIVER, "org.h2.Driver");
        settings.put(AvailableSettings.URL, "jdbc:h2:file:./data/demo");
        settings.put(AvailableSettings.USER, "sa");
        settings.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        settings.put(AvailableSettings.FORMAT_SQL, "true");
        settings.put(AvailableSettings.HBM2DDL_AUTO, "none");

        final Configuration configuration = new Configuration();
        configuration.setProperties(settings);
        configuration.addAnnotatedClass(Post.class);
        configuration.addAnnotatedClass(PostComment.class);

        final ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

}
