package com.aerolineas;

/**
 * Implementación de Árbol AVL genérico
 */
public class ArbolAVL<T extends Comparable<T>, V> {
    private NodoAVL<T, V> raiz;

    /**
     * Inserta un elemento en el árbol AVL
     */
    public void insertar(T clave, V valor) {
        raiz = insertarRecursivo(raiz, clave, valor);
    }

    private NodoAVL<T, V> insertarRecursivo(NodoAVL<T, V> nodo, T clave, V valor) {
        if (nodo == null) {
            return new NodoAVL<>(clave, valor);
        }

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = insertarRecursivo(nodo.izquierdo, clave, valor);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRecursivo(nodo.derecho, clave, valor);
        } else {
            // Clave ya existe, actualizar valor
            nodo.valor = valor;
            return nodo;
        }

        // Actualizar altura
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        // Obtener factor de balance
        int balance = obtenerBalance(nodo);

        // Casos de rotación
        // Izquierda-Izquierda
        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) < 0) {
            return rotarDerecha(nodo);
        }

        // Derecha-Derecha
        if (balance < -1 && clave.compareTo(nodo.derecho.clave) > 0) {
            return rotarIzquierda(nodo);
        }

        // Izquierda-Derecha
        if (balance > 1 && clave.compareTo(nodo.izquierdo.clave) > 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        // Derecha-Izquierda
        if (balance < -1 && clave.compareTo(nodo.derecho.clave) < 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    /**
     * Busca un elemento por clave
     */
    public V buscar(T clave) {
        return buscarRecursivo(raiz, clave);
    }

    private V buscarRecursivo(NodoAVL<T, V> nodo, T clave) {
        if (nodo == null) {
            return null;
        }

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            return buscarRecursivo(nodo.izquierdo, clave);
        } else if (comparacion > 0) {
            return buscarRecursivo(nodo.derecho, clave);
        } else {
            return nodo.valor;
        }
    }

    /**
     * Elimina un elemento por clave
     */
    public void eliminar(T clave) {
        raiz = eliminarRecursivo(raiz, clave);
    }

    private NodoAVL<T, V> eliminarRecursivo(NodoAVL<T, V> nodo, T clave) {
        if (nodo == null) {
            return null;
        }

        int comparacion = clave.compareTo(nodo.clave);
        if (comparacion < 0) {
            nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, clave);
        } else if (comparacion > 0) {
            nodo.derecho = eliminarRecursivo(nodo.derecho, clave);
        } else {
            // Nodo encontrado
            if (nodo.izquierdo == null || nodo.derecho == null) {
                NodoAVL<T, V> temp = nodo.izquierdo != null ? nodo.izquierdo : nodo.derecho;

                if (temp == null) {
                    // No hay hijos
                    return null;
                } else {
                    // Un hijo
                    return temp;
                }
            } else {
                // Dos hijos - encontrar sucesor inorder
                NodoAVL<T, V> temp = encontrarMinimo(nodo.derecho);

                // Copiar datos del sucesor
                nodo.clave = temp.clave;
                nodo.valor = temp.valor;

                // Eliminar sucesor
                nodo.derecho = eliminarRecursivo(nodo.derecho, temp.clave);
            }
        }

        if (nodo == null) {
            return null;
        }

        // Actualizar altura
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        // Obtener factor de balance
        int balance = obtenerBalance(nodo);

        // Casos de rotación
        // Izquierda-Izquierda
        if (balance > 1 && obtenerBalance(nodo.izquierdo) >= 0) {
            return rotarDerecha(nodo);
        }

        // Izquierda-Derecha
        if (balance > 1 && obtenerBalance(nodo.izquierdo) < 0) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        // Derecha-Derecha
        if (balance < -1 && obtenerBalance(nodo.derecho) <= 0) {
            return rotarIzquierda(nodo);
        }

        // Derecha-Izquierda
        if (balance < -1 && obtenerBalance(nodo.derecho) > 0) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        return nodo;
    }

    /**
     * Recorrido inorder (de menor a mayor)
     */
    public void inorder(Visitor<T, V> visitor) {
        inorderRecursivo(raiz, visitor);
    }

    private void inorderRecursivo(NodoAVL<T, V> nodo, Visitor<T, V> visitor) {
        if (nodo != null) {
            inorderRecursivo(nodo.izquierdo, visitor);
            visitor.visitar(nodo.clave, nodo.valor);
            inorderRecursivo(nodo.derecho, visitor);
        }
    }

    // Métodos auxiliares
    private int altura(NodoAVL<T, V> nodo) {
        return nodo == null ? 0 : nodo.altura;
    }

    private int obtenerBalance(NodoAVL<T, V> nodo) {
        return nodo == null ? 0 : altura(nodo.izquierdo) - altura(nodo.derecho);
    }

    private NodoAVL<T, V> rotarDerecha(NodoAVL<T, V> y) {
        NodoAVL<T, V> x = y.izquierdo;
        NodoAVL<T, V> T2 = x.derecho;

        x.derecho = y;
        y.izquierdo = T2;

        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;

        return x;
    }

    private NodoAVL<T, V> rotarIzquierda(NodoAVL<T, V> x) {
        NodoAVL<T, V> y = x.derecho;
        NodoAVL<T, V> T2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho = T2;

        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;

        return y;
    }

    private NodoAVL<T, V> encontrarMinimo(NodoAVL<T, V> nodo) {
        NodoAVL<T, V> actual = nodo;
        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    /**
     * Nodo del árbol AVL
     */
    private static class NodoAVL<T, V> {
        T clave;
        V valor;
        NodoAVL<T, V> izquierdo, derecho;
        int altura;

        NodoAVL(T clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.altura = 1;
        }
    }

    /**
     * Interfaz para visitar nodos durante el recorrido
     */
    public interface Visitor<T, V> {
        void visitar(T clave, V valor);
    }
}
