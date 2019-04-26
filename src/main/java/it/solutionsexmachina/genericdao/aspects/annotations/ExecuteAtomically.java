package it.solutionsexmachina.genericdao.aspects.annotations;

import it.solutionsexmachina.genericdao.jpa.DBMSPersistenceUnit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecuteAtomically {
    DBMSPersistenceUnit value();
}
