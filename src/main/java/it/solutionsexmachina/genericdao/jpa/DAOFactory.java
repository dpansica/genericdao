package it.solutionsexmachina.genericdao.jpa;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DAOFactory {

    private Map<String, JpaGenericDAO> daoMap = new HashMap<>();

    private Map<String, EntityManager> entityManagerMap = new HashMap<>();

    @Produces
    @GenericDAO
    public JpaGenericDAO createDAO(@Any Instance<JpaGenericDAO> instance, InjectionPoint injectionPoint) {

        Annotated annotated = injectionPoint.getAnnotated();
        DAOClass daoClassAnnotation = annotated.getAnnotation(DAOClass.class);
        DBMSUnit dbmsUnit = annotated.getAnnotation(DBMSUnit.class);

        EntityManager entityManager = null;
        if (entityManagerMap.get(dbmsUnit.value().getUnitName())==null) {
            entityManager = Persistence.createEntityManagerFactory(dbmsUnit.value().getUnitName()).createEntityManager();
            entityManagerMap.put(dbmsUnit.value().getUnitName(), entityManager);
        }
        else{
            entityManager = entityManagerMap.get(dbmsUnit.value().getUnitName());
        }

        JpaGenericDAO jpaGenericDAO = null;
        if (daoMap.get(daoClassAnnotation.value().getName())==null) {

            jpaGenericDAO = new JpaGenericDAO<>(daoClassAnnotation.value(), entityManager);

            daoMap.put(daoClassAnnotation.value().getName(), jpaGenericDAO);
        }
        else{
            jpaGenericDAO = daoMap.get(daoClassAnnotation.value().getName());
        }

        return jpaGenericDAO;
    }

    public Map<String, EntityManager> getEntityManagerMap() {
        return entityManagerMap;
    }
}
