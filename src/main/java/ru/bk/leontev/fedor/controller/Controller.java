package ru.bk.leontev.fedor.controller;

import ru.bk.leontev.fedor.models.Entity;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;




public class Controller {
    private final SessionFactory sessionFactory = buildSessionFactory();
    private ServiceRegistry serviceRegistry;

    private SessionFactory buildSessionFactory() {
        try {
            // ������� ������ � hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure();
            serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        // ������ ��� � ��������� ���������� � ��
        getSessionFactory().close();
    }

    public void save(Entity entity) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.save(entity);
        session.getTransaction().commit();
    }

    public void delete(Integer id, Class c) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(session.load(c, id));
        session.getTransaction().commit();
    }

    private LinkedList<Integer> getIds(String query) {
        LinkedList<Integer> ids = new LinkedList();
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        SQLQuery all = session.createSQLQuery(query);
        List list = all.list();
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();
            ids.add((Integer) row[0]);
        }
        return ids;
    }



    public LinkedList<Entity> listOnQuery(String query, Class c) {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        LinkedList<Entity> listAll = new LinkedList();
        LinkedList<Integer> ids = getIds(query);
        for (int i = 0; i < ids.size(); i++) {
            listAll.add((Entity) session.get(c, ids.get(i)));
        }
        return listAll;
    }

    public void correct(Entity entity){
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.update(entity);
        session.getTransaction().commit();
    }

}
