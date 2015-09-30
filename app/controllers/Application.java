package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;
import util.*;
import models.Opcoes;
import models.Query;


public class Application extends Controller {
	private final Form<Query> queryForm = Form.form(Query.class);
	
    public Result index() {
    	Indexer i0 = new Indexer(0);
    	i0.indexFiles();
    	
    	Indexer i1 = new Indexer(1);
    	i1.indexFiles();
    	
    	Indexer i2 = new Indexer(2);
    	i2.indexFiles();
    	
    	Indexer i3 = new Indexer(3);
    	i3.indexFiles();
    	
        return ok("indexado");
    }
    
    public Result search(String query, String ck1, String ck2) {
    	int version = 0;
    	if (ck1.equals("0") && ck2.equals("1")){ // Version1
    		version = Indexer.VERSION1;
    	}
    	else if (ck1.equals("1") && ck2.equals("1")) {  //Version2
    		version = Indexer.VERSION2;
    	}
    	else if (ck1.equals("1") && ck2.equals("0")){ //Version3
    		version = Indexer.VERSION3;
    	}
    	else {
    		version = Indexer.VERSION4;
    	}
    	
    	
		Searcher s = new Searcher(query, version);
		ArrayList<String> results;
		try {
			results = s.SearchFiles();
			return ok(index.render(results));	  
			
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ok();
    }
    
    public Result query(){
//    	DynamicForm requestData = Form.form().bindFromRequest();
//    	String query = requestData.get("query");
//    	List<Opcoes> o = new ArrayList<>(); 
//    	Opcoes stem = new Opcoes(0, "stem");
//    	Opcoes stopwords = new Opcoes(1, "stopwords");
    	List<String> list = new ArrayList<>();
    	list.add("stem");
    	list.add("stopwords");
    	
    	
    	return ok(form.render(list, queryForm));
    }
    
    public Result handleQuery(){
    	Form<Query> q = queryForm.bindFromRequest();
    	Query query = q.get();
//    	System.out.println(q.toString());
//    	System.out.println(q.get().toString());
//    	System.out.println(q.get().getOpcoes().get(0));
//    	Logger.debug(q.toString());
//    	Logger.debug(q.get().toString());
       	
    	
    	
    	String ck1 = "0";
    	String ck2 = "0";
    	
    	if (q.get().getOpcoes().contains("stem")) {
    		ck1 = "1";
    	}
    	if (q.get().getOpcoes().contains("stopwords")) {
    		ck2 = "1";
    	}
    	
    	System.out.println(q.get().getOpcoes());
    	
    	
   
    	String qStr = query.getQueryStr();
    	
    	return redirect(controllers.routes.Application.search(qStr, ck1, ck2));
    }

}
