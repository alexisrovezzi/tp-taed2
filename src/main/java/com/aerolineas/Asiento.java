package com.aerolineas;

/**
 * Representa un asiento en una aeronave
 */
public class Asiento implements Comparable<Asiento> {
    private char seccion;
    private int numero;

    public Asiento(char seccion, int numero) {
        this.seccion = seccion;
        this.numero = numero;
    }

    public char getSeccion() {
        return seccion;
    }

    public int getNumero() {
        return numero;
    }

    @Override
    public int compareTo(Asiento otro) {
        if (this.seccion != otro.seccion) {
            return Character.compare(this.seccion, otro.seccion);
        }
        return Integer.compare(this.numero, otro.numero);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Asiento asiento = (Asiento) obj;
        return seccion == asiento.seccion && numero == asiento.numero;
    }

    @Override
    public int hashCode() {
        return seccion * 100 + numero;
    }

    @Override
    public String toString() {
        return "" + seccion + numero;
    }
}
