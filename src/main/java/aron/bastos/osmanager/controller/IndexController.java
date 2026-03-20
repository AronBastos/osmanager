package aron.bastos.osmanager.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * ManagedBean para controlar a página inicial
 */
@ManagedBean(name = "indexController")
@SessionScoped
public class IndexController implements Serializable {

    private static final long serialVersionUID = 1L;

    // Propriedades
    private String welcomeMessage;

    // Construtor
    public IndexController() {
        this.welcomeMessage = "Bem-vindo ao OSManager";
    }

    // Métodos de navegação
    public String goToOrders() {
        return "orders/list";
    }

    public String goToClients() {
        return "clients/list";
    }

    public String goToVehicles() {
        return "vehicles/list";
    }

    public String goToServices() {
        return "services/list";
    }

    // Getters e Setters
    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
}

