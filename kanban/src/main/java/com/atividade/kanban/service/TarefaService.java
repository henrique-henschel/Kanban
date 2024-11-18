package com.atividade.kanban.service;

import com.atividade.kanban.model.Status;
import com.atividade.kanban.model.Tarefa;
import com.atividade.kanban.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll();
    }

    public Tarefa salvarTarefa(Tarefa tarefa) {
        return tarefaRepository.save(tarefa);
    }

    public Tarefa atualizarTarefa(Integer id, Tarefa tarefa) {
        Tarefa tarefaAtual = tarefaRepository.findById(id).orElseThrow(() -> new RuntimeException(">> A tarefa não foi encontrada!"));
        tarefaAtual.setTitulo(tarefa.getTitulo());
        tarefaAtual.setDescricao(tarefa.getDescricao());
        tarefaAtual.setPrioridade(tarefa.getPrioridade());
        tarefaAtual.setDataLimite(tarefa.getDataLimite());
        return tarefaRepository.save(tarefaAtual);
    }

    public void excluirTarefa(Integer id) {
        tarefaRepository.deleteById(id);
    }

    public Tarefa moverTarefa(Integer id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new RuntimeException(">> A Tarefa não foi encontrada!"));
        if (tarefa.getStatus() == Status.A_FAZER) {
            tarefa.setStatus(Status.EM_PROGRESSO);
        } else if (tarefa.getStatus() == Status.EM_PROGRESSO) {
            tarefa.setStatus(Status.CONCLUIDA);
        }
        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> listarPrioridade() {
        return tarefaRepository.findByOrderByPrioridadeAsc();
    }

    public List<Tarefa> listarAtrasadas() {
        return tarefaRepository.findAll().stream().filter(tarefa -> tarefa.getDataLimite() != null && tarefa.getDataLimite().isBefore(LocalDate.now()) && tarefa.getStatus() != Status.CONCLUIDA).collect(Collectors.toList());
    }
}