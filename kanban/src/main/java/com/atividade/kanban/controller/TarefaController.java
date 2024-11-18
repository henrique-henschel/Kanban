package com.atividade.kanban.controller;

import com.atividade.kanban.controller.dto.CreateTarefaDto;
import com.atividade.kanban.model.Role;
import com.atividade.kanban.model.Tarefa;
import com.atividade.kanban.repository.TarefaRepository;
import com.atividade.kanban.repository.UsuarioRepository;
import com.atividade.kanban.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;

    public TarefaController(TarefaRepository tarefaRepository, UsuarioRepository usuarioRepository) {
        this.tarefaRepository = tarefaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Autowired
    private TarefaService tarefaService;

    @GetMapping
    public List<Tarefa> listarTodas() {
        return tarefaService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<Void> criarTarefa(@RequestBody CreateTarefaDto dto,
                                            JwtAuthenticationToken token){
        UUID userId = UUID.fromString(token.getName());
        var usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ">> Usuário não encontrado."));
        var tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setDescricao(dto.descricao());
        tarefa.setDataLimite(dto.dataLimite());
        tarefa.setUser(usuario);
        tarefaService.salvarTarefa(tarefa);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public Tarefa atualizarTarefa(@PathVariable Integer id, @RequestBody Tarefa tarefa) {
        return tarefaService.atualizarTarefa(id, tarefa);
    }

    @PutMapping("/{id}/move")
    public Tarefa moverTarefa(@PathVariable Integer id) {
        return tarefaService.moverTarefa(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTarefa(@PathVariable("id") Integer tarefaId,
                                             JwtAuthenticationToken token){
        var usuario = usuarioRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,">> Usuário não encontrado."));

        var tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,">> Tarefa não encontrada!"));
        var isAdmin = usuario.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || tarefa.getUser().getUserId().equals(usuario.getUserId())) {
            tarefaService.excluirTarefa(tarefaId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/atrasadas")
    public List<Tarefa> listarAtrasadas() {
        return tarefaService.listarAtrasadas();
    }

    @GetMapping("/prioridades")
    public List<Tarefa> listarPorPrioridade() {
        return tarefaService.listarPrioridade();
    }
}