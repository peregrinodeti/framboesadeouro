package com.premio.cinema.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.premio.cinema.modelo.Filme;
import com.premio.cinema.modelo.interno.ProdutorVencedor;
import com.premio.cinema.modelo.interno.ProdutorVencedorMinMax;

public class FiltroProdutorVencedor {

	private List<String> extrairListaNomesProdutores(List<Filme> filmes) {
		List<String> listaNomes = new ArrayList<>();
		Iterator<Filme> filmeIterator = filmes.iterator();
		Filme filme;

		while (filmeIterator.hasNext()) {
			filme = filmeIterator.next();

			for (String item : filme.getProducers().trim().split(",")) {
				listaNomes.addAll(Arrays.asList(item.trim().split(" and ")));
			}
		}
		return listaNomes;
	}
	
	public ProdutorVencedorMinMax montarListaProdutorVencedorMinMax(List<Filme> filmes) {
		List<ProdutorVencedor> listaProdutorVencedor;
		List<ProdutorVencedor> listaProdutorVencedorMin;
		List<ProdutorVencedor> listaProdutorVencedorMax;
		
		ProdutorVencedor produtorVencedorMin;
		ProdutorVencedor produtorVencedorMax;
		ProdutorVencedorMinMax produtorVencedorMinMax = new ProdutorVencedorMinMax();
		
		listaProdutorVencedor = this.montarListaProdutoresVencedores(filmes);
		
		produtorVencedorMin = listaProdutorVencedor.stream().min(Comparator.comparing(ProdutorVencedor::getInterval)).orElse(null);
		listaProdutorVencedorMin = listaProdutorVencedor.stream().filter(produtorVencedor -> produtorVencedor.getInterval() == produtorVencedorMin.getInterval()).collect(Collectors.toList());
		listaProdutorVencedorMin.sort(Comparator.comparing(ProdutorVencedor::getProducer));
		
		produtorVencedorMax = listaProdutorVencedor.stream().max(Comparator.comparing(ProdutorVencedor::getInterval)).orElse(null);
		listaProdutorVencedorMax = listaProdutorVencedor.stream().filter(produtorVencedor -> produtorVencedor.getInterval() == produtorVencedorMax.getInterval()).collect(Collectors.toList());
		listaProdutorVencedorMax.sort(Comparator.comparing(ProdutorVencedor::getProducer));
		
		produtorVencedorMinMax.setMin(listaProdutorVencedorMin);
		produtorVencedorMinMax.setMax(listaProdutorVencedorMax);
		
		return produtorVencedorMinMax;
	}
	
	private List<ProdutorVencedor> montarListaProdutoresVencedores(List<Filme> filmes) {
		List<String> listaNomesProdutores;
		Set<String> conjuntoNomesProdutores;
		List<Filme> buscaMinMax;
		List<ProdutorVencedor> produtores = new ArrayList<ProdutorVencedor>();
		Iterator<String> conjuntoNomesProdutoresIterator;
		
		Filme filmeMin;
		Filme filmeMax;
		ProdutorVencedor produtorVencedor;
		
		long conta = 0;
		
		listaNomesProdutores = this.extrairListaNomesProdutores(filmes);
		conjuntoNomesProdutores = this.organizarListaNomesProdutores(listaNomesProdutores);
		
		conjuntoNomesProdutoresIterator = conjuntoNomesProdutores.iterator();

		while (conjuntoNomesProdutoresIterator.hasNext()) {
			String nome = conjuntoNomesProdutoresIterator.next();

			conta = filmes.stream().filter(filme -> filme.getProducers().contains(nome)).count();

			if (conta > 1) {
				buscaMinMax = filmes.stream().filter(filme -> filme.getProducers().contains(nome))
						.collect(Collectors.toList());

				buscaMinMax.sort(Comparator.comparing(Filme::getYear));
				filmeMin = buscaMinMax.get(0);
				filmeMax = buscaMinMax.get(buscaMinMax.size() - 1);	
				
				produtorVencedor = new ProdutorVencedor();
				produtorVencedor.setProducer(nome);
				produtorVencedor.setPreviousWin(filmeMin.getYear());
				produtorVencedor.setFollowingWin(filmeMax.getYear());
				produtorVencedor.setInterval(filmeMax.getYear() - filmeMin.getYear());		
				produtores.add(produtorVencedor);
			}
		}
		
		return produtores;
	}

	private Set<String> organizarListaNomesProdutores(List<String> nomesProdutores) {
		Set<String> conjuntoNomes = new HashSet<>();

		Iterator<String> nomes = nomesProdutores.iterator();

		while (nomes.hasNext()) {
			conjuntoNomes.add(nomes.next());
		}

		return conjuntoNomes;
	}
}
