package com.premio.cinema.modelo.interno;

import java.util.List;

public class ProdutorVencedorMinMax {

	private List<ProdutorVencedor> min;
	private List<ProdutorVencedor> max;

	public List<ProdutorVencedor> getMin() {
		return min;
	}

	public void setMin(List<ProdutorVencedor> min) {
		this.min = min;
	}

	public List<ProdutorVencedor> getMax() {
		return max;
	}

	public void setMax(List<ProdutorVencedor> max) {
		this.max = max;
	}
}
