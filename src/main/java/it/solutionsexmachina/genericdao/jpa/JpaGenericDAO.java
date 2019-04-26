package it.solutionsexmachina.genericdao.jpa;

import org.apache.commons.beanutils.PropertyUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;


public class JpaGenericDAO<T>
{

    private Class<T> persistentClass;

    private EntityManager entityManager;

    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<T> criteriaQuery;
    private Root<T> root;

    public JpaGenericDAO(Class<T> entityClass, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.persistentClass = entityClass;
        this.entityManager.getMetamodel().entity(this.persistentClass);
        initQueryRoot();
    }

    public void initQueryRoot()
    {
        criteriaBuilder = entityManager.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(persistentClass);
        root = criteriaQuery.from(persistentClass);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }

    public CriteriaBuilder getCriteriaBuilder()
    {
        return criteriaBuilder;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder)
    {
        this.criteriaBuilder = criteriaBuilder;
    }

    public Root<T> getRoot()
    {
        return root;
    }

    public void setRoot(Root<T> root)
    {
        this.root = root;
    }

    public CriteriaQuery<T> getCriteriaQuery()
    {
        return criteriaQuery;
    }

    public void setCriteriaQuery(CriteriaQuery<T> criteriaQuery)
    {
        this.criteriaQuery = criteriaQuery;
    }

    public List<T> findAll()
    {
        initQueryRoot();

        criteriaQuery.select(root);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public T findById(String id)
    {
        return entityManager.find(persistentClass, id);
    }

    public List<T> findByPredicates(List<Predicate> predicates)
    {
        try
        {
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        criteriaQuery.select(root);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    public Order createOrderByPredicate(String propertyName, String direction)
    {
        if (direction.equals("ASC"))
        {
            return criteriaBuilder.asc(root.get(propertyName));
        } else
        {
            return criteriaBuilder.desc(root.get(propertyName));
        }
    }

    public List<T> findByExample(T exampleInstance, Order... orderList)
    {
        //initQueryRoot();
        try
        {
            List<Predicate> predicates = new ArrayList<Predicate>();
            Map<?, ?> classDescription = PropertyUtils.describe(getPersistentClass().newInstance());
            for (Iterator<?> iterator = classDescription.keySet().iterator(); iterator.hasNext(); )
            {
                String property = (String) iterator.next();
                Class<?> propertyType = PropertyUtils.getPropertyType(getPersistentClass().newInstance(), property);

                Object value = PropertyUtils.getNestedProperty(exampleInstance, property);
                if (propertyType != null && !propertyType.equals(Class.class) && !propertyType.isAssignableFrom(Set.class) && value != null)
                {
                    predicates.add(criteriaBuilder.equal(root.get(property), value));
                }
            }
            criteriaQuery.where(predicates.toArray(new Predicate[]{}));

            if (orderList != null)
            {
                criteriaQuery.orderBy(orderList);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        criteriaQuery.select(root);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();

    }
    
    public T findByExampleFirstResult(T exampleInstance)
    {
    	List<T> results = findByExample(exampleInstance);
    	if (results.size()>0){
    		return results.get(0);
    	}
    	else{
    		return null;
    	}
    }

    public List<T> collectById(List<String> ids)
    {
        initQueryRoot();
        try
        {
            Path<Object> path = root.get("id");
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(path);
            for (String id : ids)
            {
                in.value(id);
            }
            criteriaQuery.where(in);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        criteriaQuery.select(root);
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();

    }

    public void save(T object)
    {
        entityManager.persist(object);
        entityManager.flush();
        entityManager.refresh(object);
    }

    public void update(T object)
    {
        entityManager.merge(object);
        entityManager.flush();
    }

    public void remove(T object)
    {
        entityManager.remove(object);
        entityManager.flush();
    }

    public void refresh(T object)
    {
        entityManager.refresh(object);
    }

}
