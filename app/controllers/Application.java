package controllers;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;

import play.*;
import play.mvc.*;

import views.html.*;
import util.*;


public class Application extends Controller {

    public Result index() {
    	Indexer i = new Indexer();
    	i.indexFiles();
        return ok("indexado");
    }
    
    public Result search(String query) {
		Searcher s = new Searcher(query);
		try {
			String[] str_array = s.SearchFiles();
			 
			return ok(str_array[0]);
			
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok("ok");
    }

}
