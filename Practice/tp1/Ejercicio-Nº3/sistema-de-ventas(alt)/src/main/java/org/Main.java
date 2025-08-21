package org;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FioripersistencePU");
        EntityManager em = null;

        try {
            em = emf.createEntityManager();
            //Cliente cliente1 = new Cliente("Renzo", "Davila", 46258756);
            //em.getTransaction().begin();
            //em.persist(cliente1);
            //em.getTransaction().commit();
            System.out.println("Cliente guardado con éxito.");

        } catch (RuntimeException e) {
            // Check if the EntityManager is open and a transaction is active before rollback
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
        }finally {
            // Ensure the EntityManager and EntityManagerFactory are always closed
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
    }
}