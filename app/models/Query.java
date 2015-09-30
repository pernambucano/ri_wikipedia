package models;

import java.util.ArrayList;
import java.util.List;


import org.springframework.ui.Model;

public class Query {
	public String queryStr;
	public List<String> opcoes = new ArrayList<>();
	public long id;
	public Query() {}

	public List<String> getOpcoes() {
		return opcoes;
	}

	public void setOpcoes(List<String> opcoes) {
		this.opcoes = opcoes;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}
	
	
}
