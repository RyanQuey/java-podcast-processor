[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/RyanQuey/java-podcast-processor) 

# Package:

`mvn package`

# Run

## UPDATE
use `mvn exec:exec` instead of `java -cp target/podcast-analyzer-0.1.0.jar Main`, for whatever reason it wasn't finding the packages correctly when running, even though it was working fine when packaging. See issue [here](https://stackoverflow.com/questions/37960551/caused-by-java-lang-classnotfoundexception-org-apache-commons-io-fileutils/37960658#comment109230841_37960658).

- Run 
`mvn exec:exec` or `mvn exec:java`

### Options
- Do not perform a search for podcasts:
Change default args to have `--perform-search=false`

- Perform a search for podcasts and then run:
Change default args to have `--perform-search=true`

- process all the search results ever found
Change default args to have `--process=all`

- process only the new search results retrieved in this last run
Change default args to have `--process=new`
*TODO* should persist something on a SearchQuery record that says if we've processed it or not. Much more reliable

- Process our default query only ("podcast-data/artist_big-data.json"). Mostly for testing:
Change default args to have `--process=default-query`

#### Good defaults:
`--process=new`
`--perform-search=true`

## Next TODOs
- Change it around so that it runs iteratively on an Airflow job
- Once Search result is retrieved and persisted, have a hook that sends to Kafka stream. 
- Have a Spark job consume that Kafka topic and process each podcast to retrieve and persist all episodes
- Set things up so I can find podcasts that I really want
- Add visualizations
