package controllers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.queryparser.classic.ParseException;

import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;
import util.*;
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
    	
        return ok("indexado");
    }
    
    public Result search(String query) {
		Searcher s = new Searcher(query);
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
    	
    	return ok(form.render(queryForm));
    }
    
    public Result handleQuery(){
    	Form<Query> q = queryForm.bindFromRequest();
    	Query query = q.get();
    	String qStr = query.getQueryStr();
    	
    	return redirect(controllers.routes.Application.search(qStr));
    }

}
