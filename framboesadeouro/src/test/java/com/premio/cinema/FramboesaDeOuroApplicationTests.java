package com.premio.cinema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.premio.cinema.modelo.Filme;
import com.premio.cinema.modelo.interno.ProdutorVencedorMinMax;
import com.premio.cinema.repositorio.FilmeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FramboesaDeOuroApplicationTests {

	@Autowired
	private FilmeRepository filmeRepository;

	@Autowired
	private TestRestTemplate testRestTemplate;

	Filme filme;

	@BeforeAll
	public void iniciar() {
		this.filme = new Filme();
		this.filme.setYear(2022);
		this.filme.setTitle("Teste Framboesa");
		this.filme.setStudios("MGM Framboesa");
		this.filme.setProducers("Fulano, Beltrano and Ciclano");
		this.filme.setWinner(true);
	}

	@Test
	public void getVencedores() {
		ResponseEntity<ProdutorVencedorMinMax> response = this.testRestTemplate.exchange("/vencedores", HttpMethod.GET,
				null, ProdutorVencedorMinMax.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void getFilmes() {
		ResponseEntity<Filme[]> response = this.testRestTemplate.exchange("/filmes", HttpMethod.GET, null,
				Filme[].class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void getFilmeById() {
		Filme filmeSalvo = this.filmeRepository.save(this.filme);

		ResponseEntity<Filme> response = this.testRestTemplate.exchange("/filmes/" + filmeSalvo.getId(), HttpMethod.GET,
				null, Filme.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getWinner(), true);
	}

	@Test
	public void postFilm() {
		HttpEntity<Filme> httpEntity = new HttpEntity<>(this.filme);

		ResponseEntity<Filme> response = this.testRestTemplate.exchange("/filmes", HttpMethod.POST, httpEntity,
				Filme.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getYear(), 2022);
	}

	@Test
	public void putFilm() {
		Filme filmeSalvo = this.filmeRepository.save(this.filme);

		Filme filme = new Filme();
		filme.setYear(2100);
		filme.setTitle("Teste Framboesa");
		filme.setStudios("MGM Framboesa");
		filme.setProducers("Ciclano, Fulano and Beltrano");
		filme.setWinner(true);

		HttpEntity<Filme> httpEntity = new HttpEntity<>(filme);

		ResponseEntity<Filme> response = this.testRestTemplate.exchange("/filmes/" + filmeSalvo.getId(), HttpMethod.PUT,
				httpEntity, Filme.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
		assertEquals(response.getBody().getYear(), 2100);
		assertEquals(response.getBody().getProducers(), "Ciclano, Fulano and Beltrano");
	}

	@Test
	public void deleteFilm() {
		Filme filmeSalvo = this.filmeRepository.save(this.filme);

		ResponseEntity<Void> response = this.testRestTemplate.exchange("/filmes/" + filmeSalvo.getId(),
				HttpMethod.DELETE, null, Void.class);

		assertEquals(response.getStatusCode(), HttpStatus.OK);
	}

	@Test
	void contextLoads() {
		assertThat(this.filme).isNotNull();
	}
}