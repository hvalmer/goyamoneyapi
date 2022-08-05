package com.goyamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.goyamoney.api.model.Lancamento;
import com.goyamoney.api.model.Pessoa;
import com.goyamoney.api.repository.LancamentoRepository;
import com.goyamoney.api.repository.PessoaRepository;
import com.goyamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	//implementando a regra de negocio p o lancamento
	public Lancamento salvar(@Valid Lancamento lancamento) throws PessoaInexistenteOuInativaException {
		//buscando a pessoa pelo codigo, caso não exita, lança uma exceção específica
		Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if(!pessoa.isPresent() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
		//salvando o lancamento
		return lancamentoRepository.save(lancamento);
	}

}
