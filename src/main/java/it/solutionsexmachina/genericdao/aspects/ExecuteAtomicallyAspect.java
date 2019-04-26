package it.solutionsexmachina.genericdao.aspects;

import it.solutionsexmachina.genericdao.aspects.annotations.ExecuteAtomically;
import it.solutionsexmachina.genericdao.jpa.DAOFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

@Aspect
public class ExecuteAtomicallyAspect {

    @Pointcut("@annotation(executeAtomically) && execution(* *(..))")
    public void audited(ExecuteAtomically executeAtomically) {
    }

    @Before("audited(executeAtomically)")
    public void executeAuditAnnotationBefore(JoinPoint pjp, ExecuteAtomically executeAtomically) throws Throwable {
        String unitName = executeAtomically.value().getUnitName();

        DAOFactory daoFactory = CDI.current().select(DAOFactory.class).get();

        EntityManager entityManager = daoFactory.getEntityManagerMap().get(unitName);

        if (entityManager!=null){
            entityManager.getTransaction().begin();
        }
    }

    @AfterReturning(pointcut = "audited(executeAtomically)", returning = "result")
    public void afterReturning(JoinPoint pjp, Object result, ExecuteAtomically executeAtomically) {
        String unitName = executeAtomically.value().getUnitName();
        DAOFactory daoFactory = CDI.current().select(DAOFactory.class).get();

        EntityManager entityManager = daoFactory.getEntityManagerMap().get(unitName);

        if (entityManager!=null){
            entityManager.getTransaction().commit();
        }
    }
}
