# Package:

`mvn package`

# Run

- Run (does not search for new podcasts, but processes data we already have):
`java -cp target/podcast-analyzer-0.1.0.jar Main`

- Perform a search for podcasts and then run:
`java -cp target/podcast-analyzer-0.1.0.jar Main --perform-search`

- Process our default query only ("podcast-data/artist_big-data.json"). Mostly for testing:
`java -cp target/podcast-analyzer-0.1.0.jar Main --process-default-query`
