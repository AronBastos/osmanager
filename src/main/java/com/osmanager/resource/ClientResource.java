package com.osmanager.resource;

import com.osmanager.dto.ClientDTO;
import com.osmanager.model.Cliente;
import com.osmanager.service.ClienteService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Resource para Cliente
 * Fornece endpoints REST para operações CRUD
 */
@Path("/clients")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClientResource {

    @Inject
    private ClienteService clienteService;

    /**
     * Lista todos os clientes ativos
     */
    @GET
    public Response getAllClients() {
        try {
            List<Cliente> clients = clienteService.getAllActiveClients();
            List<ClientDTO> dtos = clients.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar clientes: " + e.getMessage()).build();
        }
    }

    /**
     * Busca cliente por ID
     */
    @GET
    @Path("/{id}")
    public Response getClientById(@PathParam("id") Long id) {
        try {
            Cliente client = clienteService.getClientById(id);
            if (client == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Cliente não encontrado").build();
            }
            ClientDTO dto = convertToDTO(client);
            return Response.ok(dto).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar cliente: " + e.getMessage()).build();
        }
    }

    /**
     * Cria um novo cliente
     */
    @POST
    public Response createClient(ClientDTO clientDTO) {
        try {
            Cliente client = convertToEntity(clientDTO);
            Cliente created = clienteService.createClient(client);
            ClientDTO responseDTO = convertToDTO(created);
            return Response.status(Response.Status.CREATED).entity(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar cliente: " + e.getMessage()).build();
        }
    }

    /**
     * Atualiza um cliente existente
     */
    @PUT
    @Path("/{id}")
    public Response updateClient(@PathParam("id") Long id, ClientDTO clientDTO) {
        try {
            Cliente existing = clienteService.getClientById(id);
            if (existing == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Cliente não encontrado").build();
            }
            Cliente client = convertToEntity(clientDTO);
            client.setId(id);
            Cliente updated = clienteService.updateClient(client);
            ClientDTO responseDTO = convertToDTO(updated);
            return Response.ok(responseDTO).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar cliente: " + e.getMessage()).build();
        }
    }

    /**
     * Desativa um cliente
     */
    @DELETE
    @Path("/{id}")
    public Response deleteClient(@PathParam("id") Long id) {
        try {
            clienteService.deactivateClient(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar cliente: " + e.getMessage()).build();
        }
    }

    /**
     * Busca clientes por nome
     */
    @GET
    @Path("/search")
    public Response searchClients(@QueryParam("name") String name) {
        try {
            List<Cliente> clients = clienteService.searchClientsByName(name);
            List<ClientDTO> dtos = clients.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return Response.ok(dtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar clientes: " + e.getMessage()).build();
        }
    }

    // Métodos auxiliares para conversão
    private ClientDTO convertToDTO(Cliente client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCpf(),
                client.getAddress(),
                client.getCity(),
                client.getState(),
                client.getZipCode(),
                client.getCreatedAt(),
                client.getActive()
        );
    }

    private Cliente convertToEntity(ClientDTO dto) {
        Cliente client = new Cliente();
        client.setId(dto.getId());
        client.setName(dto.getName());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());
        client.setCpf(dto.getCpf());
        client.setAddress(dto.getAddress());
        client.setCity(dto.getCity());
        client.setState(dto.getState());
        client.setZipCode(dto.getZipCode());
        client.setCreatedAt(dto.getCreatedAt());
        client.setActive(dto.getActive());
        return client;
    }
}
