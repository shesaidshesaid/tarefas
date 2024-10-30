// src/main/java/com/example/tarefas/repository/TarefaRepository.java

package com.example.tarefas.repository;

import com.example.tarefas.model.Tarefa;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends CrudRepository<Tarefa, Long> {
    // Métodos adicionais, se necessário
}
