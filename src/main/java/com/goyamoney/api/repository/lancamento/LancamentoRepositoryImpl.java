package com.goyamoney.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.goyamoney.api.model.Lancamento;
import com.goyamoney.api.model.Lancamento_;
import com.goyamoney.api.repository.filter.LancamentoFilter;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	// injetando a pessitencia para poder trabalhar com a consulta
	@PersistenceContext
	private EntityManager manager;

	// lista paginada com Pegeable(size, page), Page(paginaçao) de uma pesquisa
	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {

		// fazendo a consulta com a criteria do JPA
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// adicionando os filtros, as restrições
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Lancamento> query = manager.createQuery(criteria);

		// adicionando a quantidade total de resultados para trazer
		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		// criando um array de Predicate que é variável
		List<Predicate> predicates = new ArrayList<>();

		// where descricao like '%aopnvoanc%'
		// criando uma lista de predicates
		if (!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicates.add(builder.like(builder.lower(root.get(Lancamento_.descricao)),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoDe()));
		}
		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoAte()));
		}
		return predicates.toArray(new Predicate[predicates.size()]);
	}

	// adicionar as retrições de paginação
	private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegitroPorPagina = paginaAtual * totalRegistrosPorPagina;

		// informando para a query o registro por página e o total
		query.setFirstResult(primeiroRegitroPorPagina);
		query.setMaxResults(totalRegistrosPorPagina);
	}

	// calcular a quantidade total p esse filtro
	private Long total(LancamentoFilter lancamentoFilter) {

		// criando uma nova query para pesquisar esse filtro
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

		// fazendo a consulta
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// adicionando o filtro
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		// contando os registros
		criteria.select(builder.count(root));

		return manager.createQuery(criteria).getSingleResult();
	}
}
