package com.premio.cinema.controller;

import java.util.concurrent.TimeUnit;

import javax.persistence.EntityNotFoundException;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.premio.cinema.modelo.Filme;
import com.premio.cinema.repositorio.FilmeRepository;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

	private final FilmeRepository filmeRepository;

	private int findAllCacheTime = 1;
	private int findByIdCacheTime = 1;

	public FilmeController(FilmeRepository filmeRepository) {
		this.filmeRepository = filmeRepository;
	}

	@GetMapping
	ResponseEntity<Iterable<Filme>> getFilmes() {
		CacheControl cacheControl = CacheControl.maxAge(findAllCacheTime, TimeUnit.MINUTES);
		return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(this.filmeRepository.findAll());
	}

	@GetMapping("/{id}")
	ResponseEntity<Filme> getFilmeById(@PathVariable String id) {
		CacheControl cacheControl = CacheControl.maxAge(findByIdCacheTime, TimeUnit.MINUTES);
		return ResponseEntity.status(HttpStatus.OK).cacheControl(cacheControl).body(this.filmeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Não foi possível localizar o registro!")));
	}

	@PostMapping
	Filme postFilme(@RequestBody Filme filme) {
		return this.filmeRepository.save(filme);
	}

	@PatchMapping("/{id}")
	ResponseEntity<Filme> patchFilme(@PathVariable String id, @RequestBody Filme filme) throws EntityNotFoundException {
		filme.setId(id);
		if (!this.filmeRepository.existsById(id)) {
			throw new EntityNotFoundException("Não foi possível localizar o registro para alteração!");
		}
		return new ResponseEntity<>(this.filmeRepository.save(filme), HttpStatus.OK);
	}

	@PutMapping("/{id}")
	ResponseEntity<Filme> putFilme(@PathVariable String id, @RequestBody Filme filme) {
		filme.setId(id);
		if (!this.filmeRepository.existsById(id)) {
			throw new EntityNotFoundException("Não foi possível localizar o registro para alteração!");
		}
		return new ResponseEntity<>(this.filmeRepository.save(filme), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	void deleteFilme(@PathVariable String id) throws EntityNotFoundException {
		if (this.filmeRepository.existsById(id)) {
			this.filmeRepository.deleteById(id);
		} else {
			throw new EntityNotFoundException("Não foi possível localizar o registro para exclusão!");
		}
	}
}
