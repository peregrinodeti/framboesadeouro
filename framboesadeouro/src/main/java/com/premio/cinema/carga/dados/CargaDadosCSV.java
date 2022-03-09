package com.premio.cinema.carga.dados;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.premio.cinema.modelo.Filme;
import com.premio.cinema.repositorio.FilmeRepository;

@Component
public class CargaDadosCSV {

	private static final Logger log = LoggerFactory.getLogger(CargaDadosCSV.class);

	private static final String ARQUIVO_CSV = System.getenv("ARQUIVO_CSV");

	private final FilmeRepository filmeRepository;

	private String nomeArquivoCSV;

	public CargaDadosCSV(FilmeRepository filmeRepository) {
		this.filmeRepository = filmeRepository;
		this.nomeArquivoCSV = CargaDadosCSV.ARQUIVO_CSV;
	}

	@PostConstruct
	private void popula() {
		CSVParser parser;
		CSVReader reader;
		ColumnPositionMappingStrategy<Filme> strategy;
		CsvToBean<Filme> csvToBean;

		long contador = 0;

		try {
			if (Strings.isBlank(nomeArquivoCSV)) {
				log.info("Nenhum arquivo para importar! A variável de ambiente ARQUIVO_CSV está vazia.");
			} else {
				log.info("Iniciando a importação do arquivo: {}", nomeArquivoCSV);

				parser = new CSVParserBuilder().withSeparator(';').withQuoteChar('"')
						.withFieldAsNull(CSVReaderNullFieldIndicator.BOTH).build();
				reader = new CSVReaderBuilder(new FileReader(nomeArquivoCSV)).withCSVParser(parser).withSkipLines(1)
						.build();

				strategy = new ColumnPositionMappingStrategy<Filme>();
				strategy.setType(Filme.class);
				strategy.setColumnMapping(new String[] { "year", "title", "studios", "producers", "winner" });

				csvToBean = new CsvToBeanBuilder<Filme>(reader).withMappingStrategy(strategy)
						.withFilter(new CsvToBeanFilter() {
							// Ignorando linhas em branco do arquivo
							@Override
							public boolean allowLine(String[] strings) {
								for (String one : strings) {
									if (one != null && one.length() > 0) {
										return true;
									}
								}
								return false;
							}
						}).build();

				Iterator<Filme> filmeIterator = csvToBean.iterator();
				while (filmeIterator.hasNext()) {
					Filme filme = filmeIterator.next();
					this.filmeRepository.save(filme);
					contador++;
				}
				log.info("Total de filmes importados: {}", contador);
				reader.close();
			}
		} catch (IOException e) {
			log.error("Não foi possível ler o arquivo CSV!");
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Ocorreu um problema na aplicação!");
			e.printStackTrace();
		}
	}
}