[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/RyanQuey/java-podcast-processor) 

# Package:

`mvn package`

# Run

## UPDATE
use `mvn exec:exec` instead of `java -cp target/podcast-analyzer-0.1.0.jar Main`, for whatever reason it wasn't finding the packages correctly when running, even though it was working fine when packaging. See issue [here](https://stackoverflow.com/questions/37960551/caused-by-java-lang-classnotfoundexception-org-apache-commons-io-fileutils/37960658#comment109230841_37960658).

- Run (does not search for new podcasts, but processes data we already have):
`mvn exec:exec` or `mvn exec:java`

- Perform a search for podcasts and then run:
Change default args to have `--perform-search=true`

- process only the new search results
Change default args to have `--process=new`

- Process our default query only ("podcast-data/artist_big-data.json"). Mostly for testing:
Change default args to have `--process=default-query`
