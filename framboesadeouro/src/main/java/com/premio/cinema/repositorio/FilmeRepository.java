package com.premio.cinema.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.premio.cinema.modelo.Filme;

public interface FilmeRepository extends CrudRepository<Filme, String> {

	@Query(value = "select * " + "from filme " + "where winner = true "
			+ "order by year ", nativeQuery = true)
	public List<Filme> buscarFilmesVencedores();

}
