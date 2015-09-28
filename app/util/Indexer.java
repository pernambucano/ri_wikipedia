package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import play.Logger;

public class Indexer {

	static String indexPath;
	static String docsPath;
	static boolean create;
	
	public Indexer()
	{
		indexPath = "index";
		docsPath = "arquivos";
		create = true;
	}
	
	public static void indexFiles()
	{
		
		
		// Check if the directory exists
		final Path docDir = Paths.get(docsPath);
		if (!Files.isReadable(docDir))
		{
			System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}
		
		Date start = new Date();
		
		try {
			System.out.println("Indexing to directory " + indexPath + " ..");
			
			Directory dir = FSDirectory.open(Paths.get(indexPath));
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			
			if (create)
			{
				iwc.setOpenMode(OpenMode.CREATE);
			}
			else 
			{
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}
			
			IndexWriter writer = new IndexWriter(dir, iwc);
			indexDocs(writer, docDir);
			
			writer.close();
			
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds.");
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	//Index many documents
	static void indexDocs (final IndexWriter writer, Path path) throws IOException 
	{
		if (Files.isDirectory(path))
		{
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					try {
						indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
						
					}catch(IOException e)
					{
						//dont index files that cant be read
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
		else {
			//indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
		}
	}
	
	// Index one document
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
	{
		try (InputStream stream = Files.newInputStream(file)) 
		{
			//make a new, empty document
			Document doc = new Document();
			
			Field pathField = new StringField("path", file.toString(), Field.Store.YES);
			doc.add(pathField);
			doc.add(new LongField("modified", lastModified, Field.Store.NO));
			doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
			
			if (writer.getConfig().getOpenMode() == OpenMode.CREATE)
			{
				System.out.println("Adding " + file);
				writer.addDocument(doc);
			}
			else {
				System.out.println("updating " + file);
				writer.updateDocument(new Term("path", file.toString()), doc);
			}
		}
	}
}
