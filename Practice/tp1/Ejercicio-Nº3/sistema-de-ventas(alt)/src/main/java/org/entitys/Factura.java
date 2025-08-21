package org.entitys;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fecha;
    private int numero;
    private int total;

    @OneToMany
    private Set<DetalleFactura> detalles;

    @ManyToOne
    private Cliente cliente;


    public Factura() {}

    public Factura(String fecha, int numero, int total) {
        this.fecha = fecha;
        this.numero = numero;
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void addDetalle(DetalleFactura detalleFactura){
        detalles.add(detalleFactura);
    }

    public void removeDetalle(DetalleFactura detalleFactura){
        detalles.remove(detalleFactura);
    }

    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public Cliente getCliente(){
        return cliente;
    }


}
