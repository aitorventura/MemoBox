package org.example.memobox.service;

import org.example.memobox.model.Deck;
import org.example.memobox.repository.DeckRepository;

import java.util.List;
import java.util.UUID;

public class DeckService {

    public List<Deck> getAll() throws Exception {
        return DeckRepository.getAll();
    }

    public Deck create(String name, String description) throws Exception {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del mazo es obligatorio.");
        return DeckRepository.insertar(name.trim(), description);
    }

    public void update(UUID id, String name, String description) throws Exception {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("El nombre del mazo es obligatorio.");
        DeckRepository.actualizar(id, name.trim(), description);
    }

    public void delete(UUID id) throws Exception {
        DeckRepository.borrar(id);
    }
}
