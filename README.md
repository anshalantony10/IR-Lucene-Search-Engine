# Information-Retrieval-Lucene

java -jar target/lucence-cranfield-1.0-SNAPSHOT.jar -indexpath index -querypath cran/cran.qry -analyzer english -similarity bm25


mvn exec:java -Dexec.mainClass="org.example.App" -Dexec.args="-indexpath index -querypath cran/cran.qry -analyzer english -similarity bm25"