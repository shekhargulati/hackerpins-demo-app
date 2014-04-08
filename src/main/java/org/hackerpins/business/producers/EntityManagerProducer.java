package org.hackerpins.business.producers;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by shekhargulati on 04/04/14.
 */
public class EntityManagerProducer {

    @PersistenceContext(unitName = "hackerpinsPU")
    @Produces
    private EntityManager entityManager;
}
