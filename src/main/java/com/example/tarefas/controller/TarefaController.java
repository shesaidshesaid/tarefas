// src/main/java/com/example/tarefas/controller/TarefaController.java

package com.example.tarefas.controller;

import com.example.tarefas.model.Tarefa;
import com.example.tarefas.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaRepository tarefaRepository;

    // Método para obter todas as tarefas
    @GetMapping
    public ResponseEntity<Iterable<Tarefa>> getAllTarefas() {
        Iterable<Tarefa> tarefas = tarefaRepository.findAll();
        return ResponseEntity.ok(tarefas);
    }

    // Método para criar uma nova tarefa
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Tarefa> criarTarefa(
        @RequestParam("titulo") String titulo,
        @RequestParam("descricao") String descricao,
        @RequestParam(value = "concluida", required = false) Boolean concluida,
        @RequestPart(name = "foto", required = false) MultipartFile foto) {
    try {
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(titulo);
        tarefa.setDescricao(descricao);
        tarefa.setConcluida(concluida != null ? concluida : false); // Define o status de concluída

        if (foto != null && !foto.isEmpty()) {
            String originalFilename = foto.getOriginalFilename();
            if (originalFilename != null && !originalFilename.isEmpty()) {
                String filename = StringUtils.cleanPath(originalFilename);
                Path uploadPath = Paths.get("uploads/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(filename);
                Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                tarefa.setFotoUrl("/uploads/" + filename);
            }
        }

        Tarefa tarefaSalva = tarefaRepository.save(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaSalva);
    } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}

    // Método para atualizar uma tarefa existente
    @PutMapping("/{id}")
public ResponseEntity<?> atualizarTarefa(
        @PathVariable Long id,
        @RequestParam("titulo") String titulo,
        @RequestParam("descricao") String descricao,
        @RequestParam(value = "concluida", required = false) Boolean concluida,
        @RequestPart(name = "foto", required = false) MultipartFile foto) {
    return tarefaRepository.findById(id)
            .map(tarefa -> {
                tarefa.setTitulo(titulo);
                tarefa.setDescricao(descricao);
                if (concluida != null) {
                    tarefa.setConcluida(concluida);
                }

                if (foto != null && !foto.isEmpty()) {
                    String originalFilename = foto.getOriginalFilename();
                    if (originalFilename != null && !originalFilename.isEmpty()) {
                        String filename = StringUtils.cleanPath(originalFilename);
                        Path uploadPath = Paths.get("uploads/");
                        try {
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }
                            Path filePath = uploadPath.resolve(filename);
                            Files.copy(foto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                            tarefa.setFotoUrl("/uploads/" + filename);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar a foto");
                        }
                    }
                }

                Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
                return ResponseEntity.ok(tarefaAtualizada);
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada"));
    }

    // Método para deletar uma tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable Long id) {
        if (tarefaRepository.existsById(id)) {
            tarefaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
