package com.premio.cinema.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.premio.cinema.modelo.interno.ProdutorVencedorMinMax;
import com.premio.cinema.repositorio.FilmeRepository;
import com.premio.cinema.util.FiltroProdutorVencedor;

@RestController
@RequestMapping("/vencedores")
public class ProdutorVencedorController {

	private final FilmeRepository filmeRepository;

	public ProdutorVencedorController(FilmeRepository filmeRepository) {
		this.filmeRepository = filmeRepository;
	}

	@GetMapping
	ResponseEntity<ProdutorVencedorMinMax> getFilmes() {
		FiltroProdutorVencedor filtroProdutorVencedor = new FiltroProdutorVencedor();

		filtroProdutorVencedor.montarListaProdutorVencedorMinMax(this.filmeRepository.buscarFilmesVencedores());

		return new ResponseEntity<>(
				filtroProdutorVencedor.montarListaProdutorVencedorMinMax(this.filmeRepository.buscarFilmesVencedores()),
				HttpStatus.OK);
	}

}
