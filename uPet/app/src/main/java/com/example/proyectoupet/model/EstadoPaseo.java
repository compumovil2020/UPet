package com.example.proyectoupet.model;

public enum EstadoPaseo {
    CANCELADO("Cancelado"), CONFIRMADO("Confirmado"), SOLICITADO("Solicitado");

    private final String text;

    /**
     * @param text
     */
    EstadoPaseo(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }

}
