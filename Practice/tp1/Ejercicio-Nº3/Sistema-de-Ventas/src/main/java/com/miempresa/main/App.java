package com.miempresa.main;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import com.miempresa.model.Cliente;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args )    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("SistemaVentasPU");
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Cliente cliente = new Cliente("facundo","lella","45965786");
            em.persist(cliente);
            em.flush();
            em.getTransaction().commit();
        }catch(Exception e){
            em.getTransaction().rollback();
        }
        em.close();
        SwingUtilities.invokeLater(() -> {
            // Crear ventana
            JFrame frame = new JFrame("Mi primera ventana");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            // Crear etiqueta con el texto
            JLabel label = new JLabel("¡Hola Mundo!", JLabel.CENTER);

            // Agregar la etiqueta a la ventana
            frame.add(label);

            // Hacer visible la ventana
            frame.setVisible(true);
        });
    }
        
}

