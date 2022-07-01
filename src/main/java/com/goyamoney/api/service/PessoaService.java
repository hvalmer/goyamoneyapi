package com.goyamoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.goyamoney.api.model.Pessoa;
import com.goyamoney.api.repository.PessoaRepository;

/*
 * Classe mais específica para negócio*/
@Service
public class PessoaService {
	
	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizarPessoa(Long codigo, Pessoa pessoa) {
		// buscando a pessoa que esta no BD, encontrar ao menos 1
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		return pessoaSalva;
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}
	
	private Pessoa buscarPessoaPeloCodigo(Long codigo) {
		Pessoa pessoaSalva = pessoaRepository.findById(codigo).orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pessoaSalva;
	}
	
}
