package migrations;

public class MigrationRunner {
  // TODO find more elegant solution for running migrations so can run independently of connecting to db, perhaps a separate jar file I can run or something
  // NOTE currently not maintaining external versioning system for this (eg a separate file or a db table), not really necessary yet
  // since doing IF NOT EXISTS then can run all these all the time we want to migrate
  public static void runMigrations () throws Exception {
    M20200513211500CreateKeyspace.run(); 
    M20200513221500CreateSearchResultsTable.run();
    M20200524201500CreatePodcastsTable.run(); 
    M20200529151500CreateEpisodesByPodcastOrderTable.run();
    // Not doing for now
    // M20200530151500AddEpisodesToPodcastsTable.run();


    System.out.println("***finished writing migrations***");
  }
}
